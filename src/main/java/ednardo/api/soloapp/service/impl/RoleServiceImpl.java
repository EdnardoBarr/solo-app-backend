package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.exception.RoleAlreadyExistsException;
import ednardo.api.soloapp.exception.RoleValidationException;
import ednardo.api.soloapp.model.Role;
import ednardo.api.soloapp.repository.RoleRepository;
import ednardo.api.soloapp.service.RoleService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    @Autowired
    RoleRepository roleRepository;

    @Override
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public void createRole(Role role) {
        if (roleRepository.existsByNameIgnoreCase(role.getName())) {
            throw new RoleValidationException("Role already exists.");
        }

        try {
            Role newRole = Role.builder().name(role.getName())
                .description(role.getDescription())
                .build();

            roleRepository.save(newRole);
        } catch (Exception exception) {
            throw new RoleValidationException("Error saving new Role.");
        }
    }

    @Override
    public void updateRole(Role role) {
        Role roleUpdated = this.roleRepository.findByNameIgnoreCase(role.getName());
        if (roleUpdated == null) {
            throw new RoleValidationException("Role not found.");
        }

        try {
            roleUpdated.setName(role.getName());
            roleUpdated.setDescription(role.getDescription());

            this.roleRepository.save(roleUpdated);
        } catch (Exception exception) {
            throw new RoleValidationException("Error updating Role.");
        }
    }

    @Override
    public void deleteRoleById(Long id) {
        Optional<Role> role = roleRepository.findById(id);
        if (role.isEmpty()) {
            throw new RoleValidationException("Role not found.");
        }
        roleRepository.deleteById(id);
    }

}
