package ednardo.api.soloapp.repository;

import ednardo.api.soloapp.model.Friendship;
import ednardo.api.soloapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("SELECT f From Friendship f WHERE (f.from = :fromId AND f.to = : toId) OR (f.from = :toId AND f.to = :fromId)")
    Optional<Friendship> findFriendshipByUsers(@Param("fromId") Long fromId, @Param("toId") Long toId);

    @Query("SELECT CASE WHEN f.from.id = :userId THEN f.to ELSE f.from END FROM Friendship f " +
            "WHERE (f.from.id = :userId OR f.to.id = :userId) AND f.status = 'FRIENDSHIP_ACCEPTED'")
    Optional<List<User>> findFriendsByUserId(@Param("userId") Long userId);

  //  Optional<List<User>> findFriendsByStatus(@Param("userId") Long id);

}
