package ednardo.api.soloapp.repository;

import ednardo.api.soloapp.enums.ActivityCategory;
import ednardo.api.soloapp.model.Activity;
import ednardo.api.soloapp.model.LocationActivity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ActivityRepositoryTests {
    private ActivityRepository activityRepository;

    @Autowired
    public ActivityRepositoryTests(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Test
    public void ActivityRepository_Save_ReturnsSavedActivity() {
        Activity activity = Activity.builder()
                .title("Beach Volleyball")
                .description("Let's play Beach Volleyball at Ipanema")
                .category(ActivityCategory.BEACH)
                .active(true)
                .maxParticipants(10)
                .mediaLocation("http://")
                .participantsJoined(0)
                .startsAt(LocalDateTime.now())
                .finishesAt(LocalDateTime.now())
                .build();
    }
}
