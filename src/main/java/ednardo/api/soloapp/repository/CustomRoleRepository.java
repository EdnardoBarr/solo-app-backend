package ednardo.api.soloapp.repository;

import ednardo.api.soloapp.enums.RoleName;
import ednardo.api.soloapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomRoleRepository {
    Role findByRoleNameOrDefault(RoleName name);
}
