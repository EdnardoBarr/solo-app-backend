package ednardo.api.soloapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@Table(name = "user_account")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    @JsonIgnore
    private String password;
    @Column(name = "given_name")
    private String givenName;
    private String surname;
    private String country;
    private String city;
    @Column(name = "date_of_birth")
    private String dateOfBirth;
    @Column(name = "picture_location")
    private String pictureLocation;
    private String bio;
    private boolean active;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name="user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="role_id", referencedColumnName = "id"))
    private List<Role> roles;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Activity> activities;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL)
    private List<ActivityMember> activityMembers;
//
//    @OneToMany(mappedBy = "to")
//    private List<Friendship> friends;

//    @OneToMany(mappedBy = "from")
//    private List<Friendship> following;
}
