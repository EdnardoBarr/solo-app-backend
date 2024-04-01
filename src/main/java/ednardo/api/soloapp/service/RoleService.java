package ednardo.api.soloapp.service;

import ednardo.api.soloapp.model.Role;

import java.util.Optional;

public interface RoleService {
    void createRole(Role role);
    void updateRole(Role role);
    Optional<Role> getRoleById(Long id);
    void deleteRoleById(Long id);
}
