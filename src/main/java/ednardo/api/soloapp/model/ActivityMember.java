package ednardo.api.soloapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ednardo.api.soloapp.enums.ActivityMemberPrivilege;
import ednardo.api.soloapp.enums.ActivityStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_member")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User member;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;
    @Enumerated(EnumType.STRING)
    private ActivityStatus status;
    private ActivityMemberPrivilege privilege;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
