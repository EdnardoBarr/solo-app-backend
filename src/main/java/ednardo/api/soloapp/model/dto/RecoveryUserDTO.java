package ednardo.api.soloapp.model.dto;

import ednardo.api.soloapp.model.Role;

import java.util.Collection;

public class RecoveryUserDTO {
    Long id;
    String email;
    Collection<Role> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }
}
