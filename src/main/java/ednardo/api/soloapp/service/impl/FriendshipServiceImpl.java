package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.enums.FriendshipStatus;

import ednardo.api.soloapp.model.Friendship;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.RequestFriendshipDTO;
import ednardo.api.soloapp.repository.FriendshipRepository;
import ednardo.api.soloapp.service.FriendshipService;
import ednardo.api.soloapp.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FriendshipServiceImpl implements FriendshipService {
    @Autowired
    UserService userService;

    @Autowired
    FriendshipRepository friendshipRepository;

    @Override
    public Optional<Friendship> findFriendship(User userFrom, User userTo) {
        return this.friendshipRepository.findFriendshipByUsers(userFrom, userTo);
    }

    @Override
    public Optional<Friendship> getById(Long id) {
        return this.friendshipRepository.findById(id);
    }

    @Override
    @Transactional
    public Friendship requestFriendship(RequestFriendshipDTO requestFriendshipDTO) {
        User newUserFrom = userService.getById(requestFriendshipDTO.getFromId());
        User newUserTo = userService.getById(requestFriendshipDTO.getToId());

        Optional<Friendship> existingFriendship = this.friendshipRepository.findFriendshipByUsers(newUserFrom, newUserTo);

        if (existingFriendship.isPresent()) {
            FriendshipStatus currentStatus = existingFriendship.get().getStatus();

            switch (currentStatus) {
                case FRIENDSHIP_PENDING:
                    throw new IllegalArgumentException("Friendship request is pending");
                case FRIENDSHIP_ACCEPTED:
                    throw new IllegalArgumentException("Friendship already exists");
                case FRIENDSHIP_BLOCKED:
                    throw new IllegalArgumentException("Friendship is blocked");
                case FRIENDSHIP_DECLINED:
                case FRIENDSHIP_REMOVED:
                    Friendship friendship = existingFriendship.get();
                    friendship.setFrom(newUserFrom);
                    friendship.setTo(newUserTo);
                    friendship.setStatus(FriendshipStatus.FRIENDSHIP_PENDING);
                    friendship.setUpdatedAt(LocalDateTime.now());
                    return friendshipRepository.save(friendship);
                default:
                    throw new IllegalArgumentException("Friendship request has failed");
            }
        }

        Friendship newFriendship = Friendship.builder()
                .from(newUserFrom)
                .to(newUserTo)
                .status(FriendshipStatus.FRIENDSHIP_PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(null).build();

        return this.friendshipRepository.save(newFriendship);
    }

    @Override
    @Transactional
    public void updateFriendship (Long id, Friendship friendship) {
        Friendship existingFriendship = friendshipRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Friendship not found"));

        existingFriendship.setStatus(friendship.getStatus());
        existingFriendship.setUpdatedAt(LocalDateTime.now());

        friendshipRepository.save(existingFriendship);
    }
}
