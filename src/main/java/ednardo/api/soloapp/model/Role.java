package ednardo.api.soloapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ednardo.api.soloapp.enums.RoleName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name="role")
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name")
    @Enumerated(EnumType.STRING)
    private RoleName roleName;

//    public Role(String role_default) {
//    }

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private List<User> users;
}
