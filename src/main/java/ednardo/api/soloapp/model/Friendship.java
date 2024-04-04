package ednardo.api.soloapp.model;

import ednardo.api.soloapp.enums.FriendshipStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "friendships")
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    @Column(name = "requested_at")
    private String requestedAt;

    @Column(name = "created_at")
    private String createdAt;
}
