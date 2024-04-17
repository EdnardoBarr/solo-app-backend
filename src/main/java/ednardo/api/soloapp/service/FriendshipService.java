package ednardo.api.soloapp.service;

import ednardo.api.soloapp.model.Friendship;
import ednardo.api.soloapp.model.User;

import java.util.Optional;

public interface FriendshipService {
    Friendship requestFriendship(User userFrom, User userTo);
    Optional<Friendship> findFriendship(Long fromId, Long toId);
}
