package ednardo.api.soloapp.repository;

import ednardo.api.soloapp.enums.RoleName;
import ednardo.api.soloapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>, CustomRoleRepository {
 //   List<Role> findAllByRoleNameIgnoreCase(RoleName name);
 //   boolean existsByRoleNameIgnoreCase(RoleName name);
    Role findByRoleName(RoleName name);
    Optional<Role> findById(Long id);
  //  Role findByRoleNameOrDefault(RoleName name);
}
