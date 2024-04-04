package ednardo.api.soloapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "location_activities")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(mappedBy = "location")
    private Activity activity;
    private String country;
    private String city;
    private String address;
    private String coords;
}