package ednardo.api.soloapp.repository;

import ednardo.api.soloapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAllByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    Role findByNameIgnoreCase(String name);
    Optional<Role> findById(Long id);
}
