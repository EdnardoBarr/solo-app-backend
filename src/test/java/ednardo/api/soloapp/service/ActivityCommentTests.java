package ednardo.api.soloapp.service;

import ednardo.api.soloapp.enums.ActivityCategory;
import ednardo.api.soloapp.enums.ActivityMemberPrivilege;
import ednardo.api.soloapp.enums.ActivityStatus;
import ednardo.api.soloapp.exception.ActivityValidationException;
import ednardo.api.soloapp.exception.UserValidationException;
import ednardo.api.soloapp.model.*;
import ednardo.api.soloapp.model.dto.ActivityCommentDTO;
import ednardo.api.soloapp.model.dto.ActivityDTO;
import ednardo.api.soloapp.model.dto.ActivityMemberRequestDTO;
import ednardo.api.soloapp.repository.ActivityCommentRepository;
import ednardo.api.soloapp.repository.ActivityRepository;
import ednardo.api.soloapp.repository.UserRepository;
import ednardo.api.soloapp.service.impl.ActivityCommentServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActivityCommentTests {
    @Mock
    ActivityCommentRepository activityCommentRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ActivityRepository activityRepository;

    @InjectMocks
    ActivityCommentServiceImpl activityCommentService;

    private User user;
    private LocationActivity locationActivity;
    private Activity activity;
    private ActivityCommentDTO activityCommentDTO;
    private ActivityComment activityComment;

    @BeforeEach
    public void setUp() {
        activityCommentDTO = new ActivityCommentDTO(1L, 1L, "comment");

        user = User.builder()
                .id(1L)
                .email("ednardobl@gmail.ocom")
                .password("encodedOldPassword")
                .givenName("Ednardo")
                .surname("Barreto")
                .country("Brazil")
                .city("Rio de Janeiro")
                .dateOfBirth("06/09/1989")
                .bio("I am an easygoing person and I like doing sports.")
                .active(true)
                .pictureLocation("http://")
                .interests(List.of())
                .roles(List.of()).build();

        locationActivity = LocationActivity.builder()
                .id(1L)
                .address("address")
                .city("city")
                .country("country")
                .coords("coords").build();

        activity = Activity.builder()
                .id(1L)
                .owner(user)
                .title("title")
                .description("description")
                .mediaLocation("http://")
                .maxParticipants(10)
                .participantsJoined(0)
                .category(ActivityCategory.BEACH)
                .startsAt(LocalDateTime.now())
                .finishesAt(LocalDateTime.now())
                .location(locationActivity)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        activityComment = ActivityComment.builder()
                .id(1L)
                .activity(activity)
                .comment("comment")
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build();
    }

    @Test
    public void testCreateCommentSuccess() {
        when(activityRepository.findById(activityCommentDTO.getActivityId())).thenReturn(Optional.of(activity));
        when(userRepository.findById(activityCommentDTO.getUserId())).thenReturn(Optional.of(user));

        activityCommentService.create(activityCommentDTO);

        verify(activityCommentRepository).save(any(ActivityComment.class));
    }

    @Test
    public void testCreateCommentActivityNotFound() {
        when(activityRepository.findById(activityCommentDTO.getActivityId())).thenReturn(Optional.empty());

        Assertions.assertThrows(ActivityValidationException.class, () -> activityCommentService.create(activityCommentDTO));
        verifyNoMoreInteractions(activityCommentRepository);
    }

    @Test
    public void testCreateCommentUserNotFound() {
        when(activityRepository.findById(activityCommentDTO.getActivityId())).thenReturn(Optional.of(activity));
        when(userRepository.findById(activityCommentDTO.getUserId())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserValidationException.class, () -> activityCommentService.create(activityCommentDTO));
        verifyNoMoreInteractions(activityCommentRepository);
    }

    @Test
    public void testUpdateCommentSuccess() {
        Long activityCommentId = 1L;
        when(activityCommentRepository.findById(activityCommentId)).thenReturn(Optional.of(activityComment));

        activityCommentService.update(activityCommentId, activityCommentDTO);

        verify(activityCommentRepository).save(any(ActivityComment.class));
    }

    @Test
    public void testUpdateCommentActivityCommentNotFound() {
        Long activityCommentId = 1L;
        when(activityCommentRepository.findById(activityCommentId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ActivityValidationException.class, () -> activityCommentService.update(activityCommentId, activityCommentDTO));
        verifyNoMoreInteractions(activityCommentRepository);
    }
}
