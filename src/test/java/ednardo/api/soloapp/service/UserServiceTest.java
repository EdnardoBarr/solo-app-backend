package ednardo.api.soloapp.service;

import ednardo.api.soloapp.enums.ActivityCategory;
import ednardo.api.soloapp.enums.RoleName;
import ednardo.api.soloapp.exception.UserNotFoundException;
import ednardo.api.soloapp.exception.UserValidationException;
import ednardo.api.soloapp.model.Role;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.*;
import ednardo.api.soloapp.model.security.JwtUtils;
import ednardo.api.soloapp.repository.RoleRepository;
import ednardo.api.soloapp.repository.UserRepository;
import ednardo.api.soloapp.service.impl.UserServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<User> criteriaQuery;

    @Mock
    private Root<User> root;

    @Mock
    private TypedQuery<User> typedQuery;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDto;
    private Role role;

    @BeforeEach
    public void setUp() {
        userDto = UserDTO.builder().email("ednardobl@gmail.com")
                .password("123")
                .givenName("Ednardo")
                .surname("Barreto")
                .country("Brazil")
                .city("Rio de Janeiro")
                .dateOfBirth("06/09/1989")
                .bio("I am an easygoing person and I like doing sports.")
                .active(true)
                .pictureLocation("http://")
                .interests(List.of())
                .build();

        user = User.builder().email("ednardobl@gmail.ocom")
                .password("123")
                .givenName("Ednardo")
                .surname("Barreto")
                .country("Brazil")
                .city("Rio de Janeiro")
                .dateOfBirth("06/09/1989")
                .bio("I am an easygoing person and I like doing sports.")
                .active(true)
                .pictureLocation("http://")
                .interests(List.of())
                .roles(List.of()).build();
    }

    @Test
    public void testGetUserByIdSuccess() {
        Long id = Long.valueOf(1);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        User userReturn = userService.getById(id);

        assertNotNull(userReturn);
        assertEquals(user.getId(), userReturn.getId());
        assertEquals(user.getEmail(), userReturn.getEmail());
    }

    @Test
    public void testGetUserByIdThrowsUserNotFoundException() {
        Long id = Long.valueOf(1);

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.getById(id));
    }

    @Test
    public void testGetUserByEmailSuccess() {
        String email = "ednardobl@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User userReturn = userService.getByEmail(email);

        assertNotNull(userReturn);
        assertEquals(userReturn.getEmail(), user.getEmail());
        assertEquals(userReturn.getId(), user.getId());
    }

    @Test
    public void testGetUserByEmailThrowsUserNotFoundException() {
        String email = "ednardobl@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.getByEmail(email));
    }

//    @Test
//    public void testGetAllPageUsersWithFiltersSuccess() {
//        ActivityCategoryDTO activityCategoryDTO = new ActivityCategoryDTO(1, "GYM", "GYM");
//        UserFilterDTO userFilterDTO = UserFilterDTO.builder()
//                .userId(1L)
//                .city("Rio de Janeiro")
//                .givenName("Ednardo")
//                .interests(List.of(activityCategoryDTO))
//                .build();
//        Pageable pageable = PageRequest.of(0, 4);
//        List<User> userList = Collections.singletonList(new User());
//
//        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
//        when(criteriaBuilder.createQuery(User.class)).thenReturn(criteriaQuery);
//        when(criteriaQuery.from(User.class)).thenReturn(root);
//        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
//        when(typedQuery.setMaxResults(pageable.getPageSize())).thenReturn(typedQuery);
//        when(typedQuery.setFirstResult(pageable.getPageSize() * pageable.getPageNumber())).thenReturn(typedQuery);
//        when(typedQuery.getResultList()).thenReturn(userList);
//
//        when(userService.buildPredicates(eq(userFilterDTO), any(CriteriaBuilder.class), any(Root.class))).thenReturn(Collections.emptyList());
//        when(userService.count(eq(userFilterDTO))).thenReturn(1L);
//        when(userService.getAll(eq(userFilterDTO), eq(pageable))).thenCallRealMethod(); // Call real method for getAll
//
//        // Mock TypedQuery behavior
//        when(typedQuery.setMaxResults(pageable.getPageSize())).thenReturn(typedQuery);
//        when(typedQuery.setFirstResult(pageable.getPageSize() * pageable.getPageNumber())).thenReturn(typedQuery);
//        when(typedQuery.getResultList()).thenReturn(userList);
//
//        Page<User> usersReturn = userService.getAll(userFilterDTO, pageable);
//
//        assertNotNull(usersReturn);
//        assertEquals(1, usersReturn.getTotalElements());
//        assertEquals(userList.size(), usersReturn.getContent().size());
//        assertEquals(userList.get(0), usersReturn.getContent().get(0));
//    }

    @Test
    public void testCreateUserSuccess() {
        //Arrange
        Role role = Role.builder().roleName(RoleName.ROLE_DEFAULT).build();

        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(roleRepository.findByRoleNameOrDefaultRoleName(userDto.getRoleName(), RoleName.ROLE_DEFAULT)).thenReturn(role);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");

        //Act
        userService.registerNewUser(userDto);

        //Assert
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testCreateUserThrowsUserValidationExceptionWhenEmailExists() {
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        Assertions.assertThrows(UserValidationException.class, () -> userService.registerNewUser(userDto));
    }

    @Test
    public void testCreateUserThrowsUserValidationExceptionWhenSaveFails() {
        Role role = Role.builder().roleName(RoleName.ROLE_DEFAULT).build();

        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(roleRepository.findByRoleNameOrDefaultRoleName(userDto.getRoleName(), RoleName.ROLE_DEFAULT)).thenReturn(role);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        doThrow(new RuntimeException("Error")).when(userRepository).save(any(User.class));

        Assertions.assertThrows(UserValidationException.class, () -> userService.registerNewUser(userDto));
    }

    @Test
    public void testUpdateUserSuccess() {
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);

        assertDoesNotThrow(() -> userService.update(id, userDto));

        verify(userRepository).save(user);

        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getGivenName(), user.getGivenName());
        assertEquals(userDto.getSurname(), user.getSurname());
        assertEquals(userDto.getCountry(), user.getCountry());
        assertEquals(userDto.getCity(), user.getCity());
        assertEquals(userDto.getDateOfBirth(), user.getDateOfBirth());
        assertEquals(userDto.getBio(), user.getBio());
        assertEquals(userDto.getInterests(), user.getInterests());
    }

    @Test
    public void testUpdateUserThrowsUserNotFoundException() {
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.update(id, userDto));
    }

    @Test
    public void testUserUpdateEmailThrowsUserValidationException() {
        Long id = 1L;
        userDto.setEmail("different@gmail.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        Assertions.assertThrows(UserValidationException.class, () -> userService.update(id, userDto));
    }

    @Test
    public void testUserUpdateThrowsUserValidationExceptionWhenSaveFails() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        doThrow(new RuntimeException("Error")).when(userRepository).save(any(User.class));

        Assertions.assertThrows(UserValidationException.class, () -> userService.update(1L, userDto));
    }

    @Test
    public void testDeleteUserByEmailSuccess() {
        String email = "ednardobl@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        assertDoesNotThrow(() -> userService.deleteByEmail(email));

        userService.deleteByEmail(email);

        assertEquals(false, user.isActive());
    }

    @Test
    public void testDeleteUserByEmailThrowsUserNotFoundException() {
        String email = "nonexisting@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.deleteByEmail(email));
    }

    @Test
    void testDeleteByEmailSaveFails() {
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(user));
        doThrow(new RuntimeException()).when(userRepository).save(any(User.class));

        UserValidationException exception = assertThrows(UserValidationException.class, () -> userService.deleteByEmail("existing@example.com"));
        assertEquals("Unable to delete user.", exception.getMessage());
    }

    @Test
    public void testAuthenticateUserSuccess() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("test@example.com", "password");
        UserDetails userDetails = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtils.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        // Act
        RecoveryJwtTokenDTO result = userService.authenticateUser(loginRequestDTO, request, session);

        // Assert
        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());

        verify(session).invalidate();
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        SecurityContext securityContext = SecurityContextHolder.getContext();
        assertEquals(authentication, securityContext.getAuthentication());
    }

    @Test
    void testAuthenticateUserInvalidCredentials() {
        // Arrange
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("test@example.com", "password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad credentials"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.authenticateUser(loginRequestDTO, request, session));
        assertEquals("Bad credentials", exception.getMessage());

        verify(session).invalidate();
        SecurityContext securityContext = SecurityContextHolder.getContext();
        assertNull(securityContext.getAuthentication());
    }




}
