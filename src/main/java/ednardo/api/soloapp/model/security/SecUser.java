package ednardo.api.soloapp.model.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ednardo.api.soloapp.model.Activity;
import ednardo.api.soloapp.model.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
public class SecUser implements UserDetails {

    @Id
    private Long id;
    private String email;
    @JsonIgnore
    private String password;
    private String name;
    private String lastName;
    private boolean active;
    private boolean changePassword;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name="users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id"))
    private List<SecUserRole> listSecUserRole;

    private List<Activity> activities;

    @JsonIgnore
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setChangePassword(boolean changePassword) {
        this.changePassword = changePassword;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public String getEmail() {
        return this.email;
    }

    public boolean getActive() {
        return this.getActive();
    }

    public List<SecUserRole> getListSecUserRole() {
        return listSecUserRole;
    }

    public void setListSecUserRole(List<SecUserRole> listSecUserRole) {
        this.listSecUserRole = listSecUserRole;
    }

    public boolean isChangePassword() {
        return this.changePassword;
    }

    private boolean isActive() {
        return this.getActive();
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getListSecUserRole();
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.isChangePassword();
    }

    @Override
    public boolean isEnabled() {
        return this.isActive();
    }




}
