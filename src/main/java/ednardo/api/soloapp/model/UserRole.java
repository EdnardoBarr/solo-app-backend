package ednardo.api.soloapp.model;

import ednardo.api.soloapp.enums.RoleName;

import javax.persistence.*;

@Entity
@Table(name="user_roles")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
