package ednardo.api.soloapp.repository;

import ednardo.api.soloapp.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("SELECT f From Friendship f WHERE (f.from = :fromId AND f.to = : toId) OR (f.from = :toId AND f.to = :fromId)")
    Optional<Friendship> findFriendshipByUsers(@Param("fromId") Long fromId, @Param("toId") Long toId);

}
