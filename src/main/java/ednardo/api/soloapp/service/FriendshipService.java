package ednardo.api.soloapp.service;

import ednardo.api.soloapp.model.Friendship;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.ActivityMemberRequestDTO;
import ednardo.api.soloapp.model.dto.RequestFriendshipDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface FriendshipService {
    Friendship requestFriendship(RequestFriendshipDTO requestFriendshipDTO);
    Optional<Friendship> findFriendship(User userFrom, User userTo);
    void updateFriendship(RequestFriendshipDTO requestFriendshipDTO);
    Optional<Friendship> getById(Long id);
    String getStatus(RequestFriendshipDTO requestFriendshipDTO);
    Page<User> getFriendshipsPending(Long userTO, Pageable pageable);
    Page<User> getFriendshipsAccepted(Long userId, Pageable pageable);
    int countFriendships(Long userId);
}
