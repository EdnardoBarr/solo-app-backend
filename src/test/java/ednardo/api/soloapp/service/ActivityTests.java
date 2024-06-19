package ednardo.api.soloapp.service;

import ednardo.api.soloapp.enums.ActivityCategory;
import ednardo.api.soloapp.enums.ActivityMemberPrivilege;
import ednardo.api.soloapp.enums.ActivityStatus;
import ednardo.api.soloapp.exception.ActivityValidationException;
import ednardo.api.soloapp.exception.UserNotFoundException;
import ednardo.api.soloapp.model.Activity;
import ednardo.api.soloapp.model.ActivityMember;
import ednardo.api.soloapp.model.LocationActivity;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.ActivityDTO;
import ednardo.api.soloapp.model.dto.LocationActivityDTO;
import ednardo.api.soloapp.repository.ActivityMemberRepository;
import ednardo.api.soloapp.repository.ActivityRepository;
import ednardo.api.soloapp.repository.UserRepository;
import ednardo.api.soloapp.service.impl.ActivityServiceImpl;
import ednardo.api.soloapp.service.impl.LocationActiveServiceImpl;
import ednardo.api.soloapp.service.impl.UserServiceImpl;
import jakarta.persistence.EntityManager;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActivityTests {
    @Mock
    UserServiceImpl userService;

    @Mock
    LocationActiveServiceImpl locationActiveService;

    @Mock
    ActivityRepository activityRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ActivityMemberRepository activityMemberRepository;

    @Mock
    EntityManager entityManager;

    @InjectMocks
    ActivityServiceImpl activityService;

    private User user;
    private ActivityDTO activityDTO;
    private LocationActivity locationActivity;
    private Activity activity;
    private ActivityMember activityMember;

    @BeforeEach
    public void setUp() {
        activityDTO = ActivityDTO.builder()
                .title("title")
                .description("description")
                .country("country")
                .city("city")
                .active(true)
                .ownerId(1L)
                .activityId(1L)
                .startsAt(LocalDateTime.now())
                .finishesAt(LocalDateTime.now())
                .cooords("coords")
                .address("address")
                .category(ActivityCategory.BEACH)
                .maxParticipants(10)
                .participantsJoined(0)
                .build();

        user = User.builder().email("ednardobl@gmail.ocom")
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
                .address("address")
                .city("city")
                .country("country")
                .coords("coords").build();

        activity = Activity.builder()
                .owner(user)
                .title(activityDTO.getTitle())
                .description(activityDTO.getDescription())
                .mediaLocation("http://")
                .maxParticipants(activityDTO.getMaxParticipants())
                .participantsJoined(0)
                .category(activityDTO.getCategory())
                .startsAt(activityDTO.getStartsAt())
                .finishesAt(activityDTO.getFinishesAt())
                .location(locationActivity)
                .createdAt(LocalDateTime.now())
                .active(activityDTO.getActive())
                .build();

        activityMember = ActivityMember.builder()
                .member(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .activity(activity)
                .status(ActivityStatus.MEMBER_PENDING)
                .privilege(ActivityMemberPrivilege.PRIVILEGE_DEFAULT)
                .build();
    }

    @Test
    public void testCreateActivitySuccess() {
        when(userService.getById(1L)).thenReturn(user);
        when(locationActiveService.create(any(LocationActivityDTO.class))).thenReturn(locationActivity);
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);

        Activity createdActivity = activityService.create(activityDTO);

        Assertions.assertNotNull(createdActivity);
        assertEquals("title", createdActivity.getTitle());
        verify(userService).getById(1L);
        verify(activityRepository).save(any(Activity.class));
    }

    @Test
    public void testCreateActivityUserNotFound() {
        when(userService.getById(1L)).thenThrow(new UserNotFoundException("User not found"));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            activityService.create(activityDTO);
        });

        assertEquals("User not found", exception.getMessage());
        verifyNoMoreInteractions(userService, locationActiveService, activityRepository);
    }

    @Test
    public void testAddParticipantSuccess() {
        when(activityRepository.findById(anyLong())).thenReturn(Optional.of(activity));
        when(activityMemberRepository.findByActivityIdAndMemberId(anyLong(), anyLong())).thenReturn(Optional.of(activityMember));

        activityService.addParticipant(anyLong(), activityDTO);
        assertEquals(ActivityStatus.MEMBER_ACCEPTED, activityMember.getStatus());
        assertEquals(1, activity.getParticipantsJoined());
        verify(activityRepository).save(activity);
        verify(activityMemberRepository).save(activityMember);
    }

    @Test
    public void testAddParticipantActivityNotFound() {
        when(activityRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ActivityValidationException.class, () -> activityService.addParticipant(anyLong(), activityDTO));
        verifyNoMoreInteractions(activityRepository, activityMemberRepository);
    }

    @Test
    public void testAddParticipantUserIsOwner() {
        activityDTO.setOwnerId(1L);
        when(activityRepository.findById(anyLong())).thenReturn(Optional.of(activity));
        when(activityMemberRepository.findByActivityIdAndMemberId(anyLong(), anyLong())).thenReturn(Optional.of(activityMember));

        ActivityValidationException exception = assertThrows(ActivityValidationException.class, () -> activityService.addParticipant(1L, activityDTO));

        assertEquals("The user is the owner of this activity and cannot join it", exception.getMessage());
        verifyNoMoreInteractions(activityRepository, activityMemberRepository);
    }

    @Test
    public void testAddParticipantActivityFull() {
        activity.setParticipantsJoined(10);
        activity.setMaxParticipants(10);
        when(activityRepository.findById(anyLong())).thenReturn(Optional.of(activity));
        when(activityMemberRepository.findByActivityIdAndMemberId(anyLong(), anyLong())).thenReturn(Optional.of(activityMember));

        ActivityValidationException exception = assertThrows(ActivityValidationException.class, () -> activityService.addParticipant(anyLong(), activityDTO));

        assertEquals("The activity has already reached its maximum number of participants", exception.getMessage());
        verifyNoMoreInteractions(activityRepository, activityMemberRepository);
    }

    @Test
    public void testDeclineParticipantSuccess() {
        Long userId = 2L;
        Long activityId = 1L;
        when(activityMemberRepository.findByActivityIdAndMemberId(activityId, userId)).thenReturn(Optional.of(activityMember));

        activityService.declineParticipant(userId, activityDTO);

        Assertions.assertEquals(ActivityStatus.MEMBER_DECLINED, activityMember.getStatus());
        Assertions.assertNotNull(activityMember.getUpdatedAt());
        verify(activityMemberRepository).save(activityMember);
    }

    @Test
    public void testDeclineParticipantActivityMemberNotFound() {
        Long userId = 2L;
        Long activityId = 1L;
        when(activityMemberRepository.findByActivityIdAndMemberId(activityId, userId)).thenReturn(Optional.empty());

        assertThrows(ActivityValidationException.class, () -> activityService.declineParticipant(userId, activityDTO));
        verifyNoMoreInteractions(activityMemberRepository);
    }

    @Test
    public void testDeclineParticipantUserIsOwner() {
        Long userId = 1L;
        Long activityId = 1L;
        when(activityMemberRepository.findByActivityIdAndMemberId(activityId, userId)).thenReturn(Optional.of(activityMember));

        ActivityValidationException exception = assertThrows(ActivityValidationException.class, () -> activityService.declineParticipant(userId, activityDTO));

        assertEquals("The user is the owner of this activity", exception.getMessage());
        verifyNoMoreInteractions(activityMemberRepository);
    }

    @Test
    public void testRemoveParticipantSuccess() {
        Long userId = 2L;
        Long activityId = 1L;
        activity.setParticipantsJoined(1);
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(activityMemberRepository.findByActivityIdAndMemberId(activityId, userId)).thenReturn(Optional.of(activityMember));

        activityService.removeParticipant(userId, activityDTO);

        assertEquals(0, activity.getParticipantsJoined());
        assertEquals(ActivityStatus.MEMBER_REMOVED, activityMember.getStatus());
        assertNotNull(activityMember.getUpdatedAt());
        verify(activityRepository).save(activity);
        verify(activityMemberRepository).save(activityMember);
    }

    @Test
    public void testRemoveParticipantActivityNotFound() {
        Long userId = 1L;
        Long activityId = 1L;
        when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

        ActivityValidationException exception = assertThrows(ActivityValidationException.class, () -> activityService.removeParticipant(userId, activityDTO));

        assertEquals("Activity not found", exception.getMessage());
        verifyNoMoreInteractions(activityRepository, activityMemberRepository);
    }

    @Test
    public void testRemoveParticipantUserIsOwner() {
        Long userId = 1L;
        Long activityId = 1L;
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(activityMemberRepository.findByActivityIdAndMemberId(activityId, userId)).thenReturn(Optional.of(activityMember));

        ActivityValidationException exception = assertThrows(ActivityValidationException.class, () -> activityService.removeParticipant(userId, activityDTO));

        assertEquals("The user is the owner of this activity", exception.getMessage());
        verifyNoMoreInteractions(activityRepository, activityMemberRepository);
    }

    @Test
    public void testRemoveParticipantParticipantsJoinedLessThanZero() {
        Long userId = 2L;
        Long activityId = 1L;
        activity.setParticipantsJoined(0);
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(activityMemberRepository.findByActivityIdAndMemberId(activityId, userId)).thenReturn(Optional.of(activityMember));

        ActivityValidationException exception = assertThrows(ActivityValidationException.class, () -> activityService.removeParticipant(userId, activityDTO));

        assertEquals("Unable to remove participant", exception.getMessage());
        verifyNoMoreInteractions(activityRepository, activityMemberRepository);
    }

    @Test
    public void testDropParticipantSuccess() {
        Long userId = 2L;
        Long activityId = 1L;
        activity.setParticipantsJoined(1);
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(activityMemberRepository.findByActivityIdAndMemberId(activityId, userId)).thenReturn(Optional.of(activityMember));

        activityService.dropParticipant(userId, activityDTO);

        assertEquals(0, activity.getParticipantsJoined());
        assertEquals(ActivityStatus.MEMBER_DROPPED, activityMember.getStatus());
        assertNotNull(activityMember.getUpdatedAt());
        verify(activityRepository).save(activity);
        verify(activityMemberRepository).save(activityMember);
    }

    @Test
    public void testDropParticipantActivityNotFound() {
        Long userId = 1L;
        Long activityId = 1L;
        when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

        ActivityValidationException exception = assertThrows(ActivityValidationException.class, () -> activityService.dropParticipant(userId, activityDTO));

        assertEquals("Activity not found", exception.getMessage());
        verifyNoMoreInteractions(activityRepository, activityMemberRepository);
    }

    @Test
    public void testDropParticipantUserIsOwner() {
        Long userId = 1L;
        Long activityId = 1L;
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(activityMemberRepository.findByActivityIdAndMemberId(activityId, userId)).thenReturn(Optional.of(activityMember));

        ActivityValidationException exception = assertThrows(ActivityValidationException.class, () -> activityService.dropParticipant(userId, activityDTO));

        assertEquals("The user is the owner of this activity", exception.getMessage());
        verifyNoMoreInteractions(activityRepository, activityMemberRepository);
    }

    @Test
    public void testDropParticipantParticipantsJoinedLessThanZero() {
        Long userId = 2L;
        Long activityId = 1L;
        activity.setParticipantsJoined(0);
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(activityMemberRepository.findByActivityIdAndMemberId(activityId, userId)).thenReturn(Optional.of(activityMember));

        ActivityValidationException exception = assertThrows(ActivityValidationException.class, () -> activityService.dropParticipant(userId, activityDTO));

        assertEquals("Unable to remove participant", exception.getMessage());
        verifyNoMoreInteractions(activityRepository, activityMemberRepository);
    }

}
