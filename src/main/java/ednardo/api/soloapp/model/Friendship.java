package ednardo.api.soloapp.model;

import ednardo.api.soloapp.enums.FriendshipStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "friendship")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_id")
    private User from;
    @ManyToOne
    @JoinColumn(name = "to_id")
    private User to;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
