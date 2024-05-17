package ednardo.api.soloapp.repository;

import ednardo.api.soloapp.model.Activity;
import ednardo.api.soloapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    boolean existsByOwner(User user);

    Page<Activity> findAll(Pageable pageable);

}
