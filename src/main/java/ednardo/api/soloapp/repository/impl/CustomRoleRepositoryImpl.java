package ednardo.api.soloapp.repository.impl;

import ednardo.api.soloapp.enums.RoleName;
import ednardo.api.soloapp.model.Role;
import ednardo.api.soloapp.repository.CustomRoleRepository;
import ednardo.api.soloapp.repository.RoleRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class CustomRoleRepositoryImpl implements CustomRoleRepository {
    @Autowired
    EntityManager entityManager;

    @Override
    public Role findByRoleNameOrDefault (RoleName roleName) {
        Role role = entityManager.createQuery("SELECT r FROM Role r WHERE r.roleName = :roleName", Role.class)
                .setParameter("roleName", roleName)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);

        if (role == null) {
            role = entityManager.createQuery("SELECT r FROM Role r WHERE r.roleName = :defaultRole", Role.class)
                    .setParameter("defaultRole", RoleName.ROLE_DEFAULT)
                    .getSingleResult();
        }

        return role;
    }
}
