package ednardo.api.soloapp.repository;

import ednardo.api.soloapp.model.Activity;
import ednardo.api.soloapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    boolean existsByOwner(User user);

    Page<Activity> findAll(Pageable pageable);



}
