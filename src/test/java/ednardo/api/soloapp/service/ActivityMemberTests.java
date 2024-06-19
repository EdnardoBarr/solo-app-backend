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
import ednardo.api.soloapp.model.dto.ActivityMemberRequestDTO;
import ednardo.api.soloapp.repository.ActivityMemberRepository;
import ednardo.api.soloapp.repository.ActivityRepository;
import ednardo.api.soloapp.service.impl.ActivityMemberServiceImpl;
import ednardo.api.soloapp.service.impl.ActivityServiceImpl;
import ednardo.api.soloapp.service.impl.UserServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActivityMemberTests {
    @Mock
    ActivityServiceImpl activityService;

    @Mock
    UserServiceImpl userService;

    @Mock
    ActivityMemberRepository activityMemberRepository;

    @Mock
    ActivityRepository activityRepository;

    @Mock
    EntityManager entityManager;

    @InjectMocks
    ActivityMemberServiceImpl activityMemberService;

    private User user;
    private ActivityDTO activityDTO;
    private LocationActivity locationActivity;
    private Activity activity;
    private ActivityMember activityMember;
    private ActivityMemberRequestDTO activityMemberRequestDTO;

    @BeforeEach
    public void setUp() {
        activityMemberRequestDTO = new ActivityMemberRequestDTO(1l, 1l);

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
                .status(ActivityStatus.MEMBER_REMOVED)
                .privilege(ActivityMemberPrivilege.PRIVILEGE_DEFAULT)
                .build();
    }

    @Test
    public void testRequestToJoinFirstTimeSuccess() {
        activity.setId(1L);
        user.setId(2L);
        when(activityService.getById(activityMemberRequestDTO.getActivityId())).thenReturn(Optional.of(activity));
        when(userService.getById(activityMemberRequestDTO.getUserId())).thenReturn(user);
        when(activityMemberRepository.findByActivityIdAndMemberId(activityMemberRequestDTO.getActivityId(), activityMemberRequestDTO.getUserId())).thenReturn(Optional.empty());

        activityMemberService.requestToJoin(activityMemberRequestDTO);

        verify(activityMemberRepository).save(any(ActivityMember.class));
    }

    @Test
    public void testRequestToJoinActivityNotFound() {
        when(activityService.getById(activityMemberRequestDTO.getActivityId())).thenReturn(Optional.empty());

        Assertions.assertThrows(ActivityValidationException.class, () -> activityMemberService.requestToJoin(activityMemberRequestDTO));
        verifyNoMoreInteractions(userService, activityMemberRepository);
    }

    @Test
    public void testRequestToJoinUserNotFound() {
        when(activityService.getById(activityMemberRequestDTO.getActivityId())).thenReturn(Optional.of(activity));
        when(userService.getById(activityMemberRequestDTO.getUserId())).thenThrow(new UserNotFoundException("User not found"));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            activityMemberService.requestToJoin(activityMemberRequestDTO);
        });

        assertEquals("User not found", exception.getMessage());
        verifyNoMoreInteractions(activityMemberRepository);
    }

    @Test
    public void testRequestToJoinUserIsOwner() {
        activity.setId(1L);
        user.setId(1L);
        when(activityService.getById(activityMemberRequestDTO.getActivityId())).thenReturn(Optional.of(activity));
        when(userService.getById(activityMemberRequestDTO.getUserId())).thenReturn(user);

        ActivityValidationException exception = assertThrows(ActivityValidationException.class, () -> activityMemberService.requestToJoin(activityMemberRequestDTO));

        assertEquals("User is the owner of the activity", exception.getMessage());
        verifyNoMoreInteractions(activityMemberRepository);
    }

    @Test
    public void testRequestToJoinAlreadyPending() {
        activity.setId(1L);
        user.setId(2L);
        activityMember.setId(1l);
        activityMember.setStatus(ActivityStatus.MEMBER_PENDING);
        when(activityService.getById(activityMemberRequestDTO.getActivityId())).thenReturn(Optional.of(activity));
        when(userService.getById(activityMemberRequestDTO.getUserId())).thenReturn(user);
        when(activityMemberRepository.findByActivityIdAndMemberId(1L, 1L)).thenReturn(Optional.of(activityMember));

        ActivityValidationException exception = assertThrows(ActivityValidationException.class, () -> activityMemberService.requestToJoin(activityMemberRequestDTO));

        assertEquals("Your request to join this activity is already pending.", exception.getMessage());
        verifyNoMoreInteractions(activityMemberRepository);
    }

    @Test
    public void testRequestToJoinAlreadyMember() {
        activity.setId(1L);
        user.setId(2L);
        activityMember.setId(1l);
        activityMember.setStatus(ActivityStatus.MEMBER_ACCEPTED);
        when(activityService.getById(activityMemberRequestDTO.getActivityId())).thenReturn(Optional.of(activity));
        when(userService.getById(activityMemberRequestDTO.getUserId())).thenReturn(user);
        when(activityMemberRepository.findByActivityIdAndMemberId(1L, 1L)).thenReturn(Optional.of(activityMember));

        ActivityValidationException exception = assertThrows(ActivityValidationException.class, () -> activityMemberService.requestToJoin(activityMemberRequestDTO));

        assertEquals("You are already a member of this activity.", exception.getMessage());
        verifyNoMoreInteractions(activityMemberRepository);
    }

    @Test
    public void testRequestToJoinRejoining() {
        activity.setId(1L);
        user.setId(2L);
        activityMember.setId(1l);
        activityMember.setStatus(ActivityStatus.MEMBER_DECLINED);
        when(activityService.getById(activityMemberRequestDTO.getActivityId())).thenReturn(Optional.of(activity));
        when(userService.getById(activityMemberRequestDTO.getUserId())).thenReturn(user);
        when(activityMemberRepository.findByActivityIdAndMemberId(1L, 1L)).thenReturn(Optional.of(activityMember));

        activityMemberService.requestToJoin(activityMemberRequestDTO);

        assertEquals(ActivityStatus.MEMBER_PENDING, activityMember.getStatus());
        assertNotNull(activityMember.getUpdatedAt());
        verify(activityMemberRepository).save(activityMember);
    }

    @Test
    public void testGetStatusNotMemberSuccess() {
        activity.setId(1L);
        user.setId(2L);
        when(activityService.getById(activityMemberRequestDTO.getActivityId())).thenReturn(Optional.of(activity));
        when(userService.getById(activityMemberRequestDTO.getUserId())).thenReturn(user);
        when(activityMemberRepository.findByActivityIdAndMemberId(activityMemberRequestDTO.getActivityId(), activityMemberRequestDTO.getUserId())).thenReturn(Optional.empty());

        String status = activityMemberService.getStatus(activityMemberRequestDTO);

        assertEquals("MEMBER_AVAILABLE", status);
    }

    @Test
    public void testGetStatusAlreadyMemberSuccess() {
        activity.setId(1L);
        user.setId(2L);
        when(activityService.getById(activityMemberRequestDTO.getActivityId())).thenReturn(Optional.of(activity));
        when(userService.getById(activityMemberRequestDTO.getUserId())).thenReturn(user);
        when(activityMemberRepository.findByActivityIdAndMemberId(activityMemberRequestDTO.getActivityId(), activityMemberRequestDTO.getUserId())).thenReturn(Optional.of(activityMember));

        String status = activityMemberService.getStatus(activityMemberRequestDTO);

        assertEquals(activityMember.getStatus().toString(), status);
    }

    @Test
    public void testGetStatusActivityNotFound() {
        when(activityService.getById(activityMemberRequestDTO.getActivityId())).thenReturn(Optional.empty());

        Assertions.assertThrows(ActivityValidationException.class, () -> activityMemberService.requestToJoin(activityMemberRequestDTO));
        verifyNoMoreInteractions(activityMemberRepository);
    }

    @Test
    public void testGetStatusUserNotFound() {
        when(activityService.getById(activityMemberRequestDTO.getActivityId())).thenReturn(Optional.of(activity));
        when(userService.getById(activityMemberRequestDTO.getUserId())).thenThrow(new UserNotFoundException("User not found"));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            activityMemberService.requestToJoin(activityMemberRequestDTO);
        });

        assertEquals("User not found", exception.getMessage());
        verifyNoMoreInteractions(activityMemberRepository);
    }

    @Test
    public void testGetPendingEmptyPageNoUsers() {
        Pageable pageable = PageRequest.of(0 ,10);
        Long activityId = 1L;
        activity.setId(1L);
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(activityMemberRepository.existsByActivityId(activityId)).thenReturn(false);

        Page<User> usersPage = activityMemberService.getPending(activityId, pageable);

        assertTrue(usersPage.isEmpty());
        verify(activityRepository).findById(activityId);
        verify(activityMemberRepository).existsByActivityId(activityId);
        verifyNoMoreInteractions(activityMemberRepository);
    }

    @Test
    public void testGetPendingEmptyPageUsersFound() {
        Pageable pageable = PageRequest.of(0 ,10);
        Long activityId = 1L;
        activity.setId(1L);
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        List<User> usersList = List.of(user1, user2);

        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(activityMemberRepository.existsByActivityId(activityId)).thenReturn(true);
        when(activityMemberRepository.findUsersPending(activityId)).thenReturn(usersList);

        Page<User> usersPage = activityMemberService.getPending(activityId, pageable);

        assertFalse(usersPage.isEmpty());
        assertEquals(2, usersPage.getTotalElements());
        assertEquals(usersList, usersPage.getContent());
        verify(activityRepository).findById(activityId);
        verify(activityMemberRepository).existsByActivityId(activityId);
    }

    @Test
    public void testGetPendingActivityNotFound() {
        Pageable pageable = PageRequest.of(0 ,10);
        Long activityId = 1L;
        when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

        assertThrows(ActivityValidationException.class, () -> activityMemberService.getPending(activityId, pageable));
        verifyNoMoreInteractions(activityMemberRepository);
    }

    @Test
    public void testGetAcceptedNoUsers() {
        Long activityId = 1L;

        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(activityMemberRepository.existsByActivityId(activityId)).thenReturn(false);

        List<User> usersList = activityMemberService.getAccepted(activityId);

        assertTrue(usersList.isEmpty());
        verifyNoMoreInteractions(activityMemberRepository);
    }

    @Test
    public void testGetAcceptedUsersFound() {
        Long activityId = 1L;
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        List<User> usersList = List.of(user1, user2);

        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(activityMemberRepository.existsByActivityId(activityId)).thenReturn(true);
        when(activityMemberRepository.findUsersAccepted(activityId)).thenReturn(usersList);

        List<User> result = activityMemberService.getAccepted(activityId);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(usersList, result);
        verify(activityRepository).findById(activityId);
        verify(activityMemberRepository).findUsersAccepted(activityId);
        verify(activityMemberRepository).existsByActivityId(activityId);        verifyNoMoreInteractions(activityMemberRepository);
    }
}
