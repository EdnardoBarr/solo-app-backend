package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.enums.ActivityCategory;
import ednardo.api.soloapp.enums.FriendshipStatus;
import ednardo.api.soloapp.enums.RoleName;
import ednardo.api.soloapp.exception.UserNotFoundException;
import ednardo.api.soloapp.exception.UserValidationException;
import ednardo.api.soloapp.model.*;
import ednardo.api.soloapp.model.dto.*;
import ednardo.api.soloapp.util.JwtUtils;
import ednardo.api.soloapp.repository.RoleRepository;
import ednardo.api.soloapp.repository.UserRepository;
import ednardo.api.soloapp.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    MyUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    EntityManager entityManager;

    public Long count(UserFilterDTO userFilterDTO) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<User> root = cq.from(User.class);
        cq.select(cb.count(root));
        List<Predicate> predicates = this.buildPredicates(userFilterDTO, cb, root);
        cq.where(predicates.toArray(Predicate[]::new));
        return entityManager.createQuery(cq).getSingleResult();
    }

    public List<Predicate> buildPredicates(UserFilterDTO userFilterDTO, CriteriaBuilder cb, Root<User> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (nonNull(userFilterDTO.getGivenName())) {
            Expression<String> upper = cb.upper(root.get("givenName"));
            Predicate user = cb.like(upper, "%" + userFilterDTO.getGivenName().toUpperCase() + "%");
            predicates.add(user);
        }
        if (nonNull(userFilterDTO.getCity())) {
            Expression<String> upper = cb.upper(root.get("city"));
            Predicate user = cb.like(upper, "%" + userFilterDTO.getCity().toUpperCase() + "%");
            predicates.add(user);
        }
        if (nonNull(userFilterDTO.getInterests())) {
            Join<User, ActivityCategory> interestsJoin = root.join("interests", JoinType.LEFT);
            Predicate interestsPredicate = interestsJoin.in(userFilterDTO.getInterests().stream()
                    .map(ActivityCategoryDTO::toEntity)
                    .collect(Collectors.toList()));
            predicates.add(interestsPredicate);
        }

        // Exclude users with FRIENDSHIP_ACCEPTED status
        Subquery<Long> subqueryFrom = cb.createQuery().subquery(Long.class);
        Root<Friendship> friendshipFromRoot = subqueryFrom.from(Friendship.class);
        subqueryFrom.select(friendshipFromRoot.get("to").get("id"))
                .where(cb.and(
                        cb.equal(friendshipFromRoot.get("from").get("id"), root.get("id")),
                        cb.equal(friendshipFromRoot.get("status"), FriendshipStatus.FRIENDSHIP_ACCEPTED)
                ));

        Subquery<Long> subqueryTo = cb.createQuery().subquery(Long.class);
        Root<Friendship> friendshipToRoot = subqueryTo.from(Friendship.class);
        subqueryTo.select(friendshipToRoot.get("from").get("id"))
                .where(cb.and(
                        cb.equal(friendshipToRoot.get("to").get("id"), root.get("id")),
                        cb.equal(friendshipToRoot.get("status"), FriendshipStatus.FRIENDSHIP_ACCEPTED)
                ));

        Predicate notAcceptedFriend = cb.and(
                cb.not(cb.exists(subqueryFrom)),
                cb.not(cb.exists(subqueryTo))
        );

        predicates.add(notAcceptedFriend);

        // Exclude the user himself
        Predicate notSelf = cb.notEqual(root.get("id"), userFilterDTO.getUserId());
        predicates.add(notSelf);

        return predicates;
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found: " + email));
    }

    @Override
    public Page<User> getAll(UserFilterDTO userFilterDTO, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        List<Predicate> predicates = this.buildPredicates(userFilterDTO, cb, root);

        cq.where(predicates.toArray(Predicate[]::new));
        int offset = pageable.getPageSize() * pageable.getPageNumber();
        TypedQuery<User> query = entityManager.createQuery(cq).setMaxResults(pageable.getPageSize()).setFirstResult(offset);

        return new PageImpl<>(query.getResultList(), pageable, this.count(userFilterDTO));
    }

    @Override
    public void registerNewUser(final UserDTO userDTO) {

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new UserValidationException("This login email already exists.");
        }

        Role role = roleRepository.findByRoleNameOrDefaultRoleName(userDTO.getRoleName(), RoleName.ROLE_DEFAULT);

        User newUser = User.builder()
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .givenName(userDTO.getGivenName())
                .surname(userDTO.getSurname())
                .country(userDTO.getCountry())
                .city(userDTO.getCity())
                .dateOfBirth("")
                .active(true)
                .roles(List.of(role))
                .interests(List.of())
                .pictureLocation("")
                .bio("")
                .build();
        try {
            userRepository.save(newUser);
        } catch (Exception exception) {
            throw new UserValidationException("Unable to save new user. Please try again.");
        }
    }

    @Override
    @Transactional
    public void update(Long id, UserDTO userDTO) {
        User userUpdated = this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

        if ((userRepository.existsByEmail(userDTO.getEmail())) && (!userUpdated.getEmail().equals(userDTO.getEmail()))) {
            throw new UserValidationException("Email is already being used by another user");
        }

        userUpdated.setEmail(userDTO.getEmail());
        userUpdated.setGivenName(userDTO.getGivenName());
        userUpdated.setSurname(userDTO.getSurname());
        userUpdated.setCountry(userDTO.getCountry());
        userUpdated.setCity(userDTO.getCity());
        userUpdated.setDateOfBirth(userDTO.getDateOfBirth());
        userUpdated.setBio(userDTO.getBio());
        userUpdated.setInterests(userDTO.getInterests());

        //  userUpdated.setRole(userDTO.getRole());
        //  userUpdated.setActive(userDTO.isActive());
        // userUpdated.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        try {
            this.userRepository.save(userUpdated);
        } catch (Exception exception) {
            throw new UserValidationException("Unable to update user.");
        }
    }

    @Override
    public void deleteByEmail(String email) {
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

        try {
            user.setActive(false);
            this.userRepository.save(user);
        } catch (Exception exception) {
            throw new UserValidationException("Unable to delete user.");
        }
    }

    @Override
    public RecoveryJwtTokenDTO authenticateUser(LoginRequestDTO loginRequestDTO, HttpServletRequest request, HttpSession session) {
        session.invalidate();
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        //   UserDetails userDetails = (UserDetails) userDetailsService.loadUserByUsername(loginRequestDTO.getEmail());

        return new RecoveryJwtTokenDTO(jwtUtils.generateToken(userDetails));
    }
}
