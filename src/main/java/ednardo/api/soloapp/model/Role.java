package ednardo.api.soloapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Entity
@Table(name="roles")
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    private String name;

    private String description;

    @ManyToMany(mappedBy = "role")
    private Collection<User> users;

}
