package ednardo.api.soloapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ednardo.api.soloapp.enums.ActivityCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "activity")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   // @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_activity_id", referencedColumnName = "id")
    private LocationActivity location;
    private String title;
    private String description;
    @Column(name = "media_location")
    private String mediaLocation;
    @Column(name = "max_participants")
    private int maxParticipants;
    @Enumerated(EnumType.STRING)
    private ActivityCategory category;
    @Column(name = "starts_at")
    private LocalDateTime startsAt;
    @Column(name = "finishes_at")
    private LocalDateTime finishesAt;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    private Boolean active;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "activity", cascade = CascadeType.ALL)
    private List<ActivityMember> member;
}
