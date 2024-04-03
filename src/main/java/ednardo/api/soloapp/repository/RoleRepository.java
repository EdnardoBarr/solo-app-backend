package ednardo.api.soloapp.repository;

import ednardo.api.soloapp.enums.RoleName;
import ednardo.api.soloapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>, CustomRoleRepository {
 //   List<Role> findAllByRoleNameIgnoreCase(RoleName name);
 //   boolean existsByRoleNameIgnoreCase(RoleName name);
    Optional<Role> findByRoleName(RoleName name);

    @Query("SELECT r From Role r WHERE r.roleName = :name OR r.roleName = :default")
    Role findByRoleNameOrDefaultRoleName(@Param("name") RoleName name, @Param("default") RoleName defaultName);
    Optional<Role> findById(Long id);
  //  Role findByRoleNameOrDefault(RoleName name);
}
