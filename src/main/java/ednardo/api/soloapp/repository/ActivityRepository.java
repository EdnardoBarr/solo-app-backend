package ednardo.api.soloapp.repository;

import ednardo.api.soloapp.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

}