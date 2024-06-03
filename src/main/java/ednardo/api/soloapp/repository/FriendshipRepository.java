package ednardo.api.soloapp.repository;

import ednardo.api.soloapp.model.Friendship;
import ednardo.api.soloapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("SELECT f From Friendship f WHERE (f.from = :userFrom AND f.to = : userTo) OR (f.from = :userTo AND f.to = :userFrom)")
    Optional<Friendship> findFriendshipByUsers(@Param("userFrom") User userFrom, @Param("userTo") User userTo);

    @Query("SELECT CASE WHEN f.from.id = :userId THEN f.to ELSE f.from END FROM Friendship f " +
            "WHERE (f.from.id = :userId OR f.to.id = :userId) AND f.status = 'FRIENDSHIP_ACCEPTED'")
    Optional<List<User>> findFriendsByUserId(@Param("userId") Long id);

    @Query("SELECT CASE WHEN f.from.id = :userId THEN f.to ELSE f.from END FROM Friendship f " +
            "WHERE (f.from.id = :userId OR f.to.id = :userId) AND f.status = 'FRIENDSHIP_BLOCKED'")
    Optional<List<User>> findBlockedFriendsByUserId(@Param("userId") Long id);

    @Query("SELECT CASE WHEN f.from.id = :userId THEN f.to ELSE f.from END FROM Friendship f " +
            "WHERE (f.from.id = :userId OR f.to.id = :userId) AND f.status = 'FRIENDSHIP_PENDING'")
    Optional<List<User>> findPendingFriendsByUserId(@Param("userId") Long id);

    @Query("SELECT f.from From Friendship f WHERE (f.to.id = :userId) AND f.status = 'FRIENDSHIP_PENDING'")
    Page<User> getPendingFriendshipsByUserId(@Param("userId") Long userId, Pageable pageable);
}
