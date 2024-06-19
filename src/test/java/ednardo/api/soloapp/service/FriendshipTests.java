package ednardo.api.soloapp.service;

import ednardo.api.soloapp.enums.ActivityCategory;
import ednardo.api.soloapp.enums.ActivityMemberPrivilege;
import ednardo.api.soloapp.enums.ActivityStatus;
import ednardo.api.soloapp.enums.FriendshipStatus;
import ednardo.api.soloapp.exception.FriendshipException;
import ednardo.api.soloapp.exception.UserValidationException;
import ednardo.api.soloapp.model.*;
import ednardo.api.soloapp.model.dto.ActivityDTO;
import ednardo.api.soloapp.model.dto.ActivityMemberRequestDTO;
import ednardo.api.soloapp.model.dto.RequestFriendshipDTO;
import ednardo.api.soloapp.repository.FriendshipRepository;
import ednardo.api.soloapp.repository.UserRepository;
import ednardo.api.soloapp.service.impl.FriendshipServiceImpl;
import ednardo.api.soloapp.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendshipTests {
    @Mock
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    FriendshipRepository friendshipRepository;

    @InjectMocks
    FriendshipServiceImpl friendshipService;

    private User userTo;
    private User userFrom;
    private RequestFriendshipDTO requestFriendshipDTO;
    private Friendship friendship;

    @BeforeEach
    public void setUp() {
        requestFriendshipDTO = new RequestFriendshipDTO(1l, 2l, FriendshipStatus.FRIENDSHIP_PENDING);

        userTo = User.builder()
                .id(1L)
                .email("userTo@gmail.ocom")
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

        userFrom = User.builder()
                .id(2L)
                .email("userFrom@gmail.ocom")
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

        friendship = Friendship.builder()
                .id(1L)
                .to(userTo)
                .from(userFrom)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .status(FriendshipStatus.FRIENDSHIP_PENDING)
                .build();
    }


    @Test
    public void testRequestFriendshipFirstTime() {
        when(userService.getById(requestFriendshipDTO.getFromId())).thenReturn(userFrom);
        when(userService.getById(requestFriendshipDTO.getToId())).thenReturn(userTo);
        when(friendshipRepository.findFriendshipByUsers(userFrom, userTo)).thenReturn(Optional.empty());
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);

        Friendship createdFriendship = friendshipService.requestFriendship(requestFriendshipDTO);

        Assertions.assertNotNull(createdFriendship);
        assertEquals(FriendshipStatus.FRIENDSHIP_PENDING, createdFriendship.getStatus());
        assertNull(createdFriendship.getUpdatedAt());
        assertEquals(userFrom, createdFriendship.getFrom());
        assertEquals(userTo, createdFriendship.getTo());
        verify(friendshipRepository).save(any(Friendship.class));
    }

    @Test
    public void testRequestFriendshipPreviouslyDeclinedOrRemoved() {
        friendship.setStatus(FriendshipStatus.FRIENDSHIP_DECLINED);
        when(userService.getById(requestFriendshipDTO.getFromId())).thenReturn(userFrom);
        when(userService.getById(requestFriendshipDTO.getToId())).thenReturn(userTo);
        when(friendshipRepository.findFriendshipByUsers(userFrom, userTo)).thenReturn(Optional.of(friendship));
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);

        Friendship createdFriendship = friendshipService.requestFriendship(requestFriendshipDTO);

        Assertions.assertNotNull(createdFriendship);
        assertEquals(FriendshipStatus.FRIENDSHIP_PENDING, createdFriendship.getStatus());
        assertNotNull(createdFriendship.getUpdatedAt());
        assertEquals(userFrom, createdFriendship.getFrom());
        assertEquals(userTo, createdFriendship.getTo());
        verify(friendshipRepository).save(any(Friendship.class));
    }

    @Test
    public void testRequestFriendshipAlreadyPending() {
        friendship.setStatus(FriendshipStatus.FRIENDSHIP_PENDING);
        when(userService.getById(requestFriendshipDTO.getFromId())).thenReturn(userFrom);
        when(userService.getById(requestFriendshipDTO.getToId())).thenReturn(userTo);
        when(friendshipRepository.findFriendshipByUsers(userFrom, userTo)).thenReturn(Optional.of(friendship));

        FriendshipException exception = assertThrows(FriendshipException.class, () -> friendshipService.requestFriendship(requestFriendshipDTO));

        assertEquals("Friendship request is pending", exception.getMessage());
        verifyNoMoreInteractions(friendshipRepository);
    }

    @Test
    public void testRequestFriendshipAlreadyAccepted() {
        friendship.setStatus(FriendshipStatus.FRIENDSHIP_ACCEPTED);
        when(userService.getById(requestFriendshipDTO.getFromId())).thenReturn(userFrom);
        when(userService.getById(requestFriendshipDTO.getToId())).thenReturn(userTo);
        when(friendshipRepository.findFriendshipByUsers(userFrom, userTo)).thenReturn(Optional.of(friendship));

        FriendshipException exception = assertThrows(FriendshipException.class, () -> friendshipService.requestFriendship(requestFriendshipDTO));

        assertEquals("Friendship already exists", exception.getMessage());
        verifyNoMoreInteractions(friendshipRepository);
    }

    @Test
    public void testRequestFriendshipIsBlocked() {
        friendship.setStatus(FriendshipStatus.FRIENDSHIP_BLOCKED);
        when(userService.getById(requestFriendshipDTO.getFromId())).thenReturn(userFrom);
        when(userService.getById(requestFriendshipDTO.getToId())).thenReturn(userTo);
        when(friendshipRepository.findFriendshipByUsers(userFrom, userTo)).thenReturn(Optional.of(friendship));

        FriendshipException exception = assertThrows(FriendshipException.class, () -> friendshipService.requestFriendship(requestFriendshipDTO));

        assertEquals("Friendship is blocked", exception.getMessage());
        verifyNoMoreInteractions(friendshipRepository);
    }

    @Test
    public void testUpdateFriendshipSuccess() {
        when(userService.getById(requestFriendshipDTO.getFromId())).thenReturn(userFrom);
        when(userService.getById(requestFriendshipDTO.getToId())).thenReturn(userTo);
        when(friendshipRepository.findFriendshipByUsers(userFrom, userTo)).thenReturn(Optional.of(friendship));

        friendshipService.updateFriendship(requestFriendshipDTO);

        verify(friendshipRepository).save(any(Friendship.class));
    }

    @Test
    public void testUpdateFriendshipNotFound() {
        when(userService.getById(requestFriendshipDTO.getFromId())).thenReturn(userFrom);
        when(userService.getById(requestFriendshipDTO.getToId())).thenReturn(userTo);
        when(friendshipRepository.findFriendshipByUsers(userFrom, userTo)).thenReturn(Optional.empty());

        assertThrows(FriendshipException.class, () -> friendshipService.updateFriendship(requestFriendshipDTO));

        verifyNoMoreInteractions(friendshipRepository);
    }

    @Test
    public void testGetStatusSuccess() {
        friendship.setStatus(FriendshipStatus.FRIENDSHIP_DECLINED);
        when(userService.getById(requestFriendshipDTO.getFromId())).thenReturn(userFrom);
        when(userService.getById(requestFriendshipDTO.getToId())).thenReturn(userTo);
        when(friendshipRepository.findFriendshipByUsers(userFrom, userTo)).thenReturn(Optional.of(friendship));

        String result = friendshipService.getStatus(requestFriendshipDTO);

        assertEquals("FRIENDSHIP_DECLINED", result);
    }

    @Test
    public void testGetStatusAvailableSuccess() {
        when(userService.getById(requestFriendshipDTO.getFromId())).thenReturn(userFrom);
        when(userService.getById(requestFriendshipDTO.getToId())).thenReturn(userTo);
        when(friendshipRepository.findFriendshipByUsers(userFrom, userTo)).thenReturn(Optional.empty());

        String result = friendshipService.getStatus(requestFriendshipDTO);

        assertEquals("FRIENDSHIP_AVAILABLE", result);
    }

    @Test
    public void testGetFriendshipsPending() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 4);
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        List<User> usersList = List.of(user1, user2);
        Page<User> usersPage = new PageImpl<>(usersList, pageable, usersList.size());

        when(userRepository.existsById(userId)).thenReturn(true);
        when(friendshipRepository.getPendingFriendshipsByUserId(userId, pageable)).thenReturn(usersPage);

        Page<User> result = friendshipService.getFriendshipsPending(userId, pageable);

        assertFalse(result.isEmpty());
        assertEquals(2, result.getTotalElements());
        assertEquals(usersList, result.getContent());

        verify(friendshipRepository).getPendingFriendshipsByUserId(userId, pageable);
        verifyNoMoreInteractions(friendshipRepository);
    }

    @Test
    public void testGetFriendshipsPendingNotFound() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 4);
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserValidationException.class, () -> friendshipService.getFriendshipsPending(userId, pageable));

        verifyNoMoreInteractions(friendshipRepository);
    }

    @Test
    public void testGetFriendshipsAccepted() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        User user1 = new User();
        user1.setId(2L);
        User user2 = new User();
        user2.setId(3L);
        User user3 = new User();
        user1.setId(4L);
        User user4 = new User();
        user2.setId(5L);

        List<User> usersList1 = List.of(user1, user2);
        Page<User> usersPage1 = new PageImpl<>(usersList1, pageable, usersList1.size());

        List<User> usersList2 = List.of(user3, user4);
        Page<User> usersPage2 = new PageImpl<>(usersList2, pageable, usersList2.size());

        when(friendshipRepository.findFriendsToByUserId(userId,pageable)).thenReturn(usersPage1);
        when(friendshipRepository.findFriendsFromByUserId(userId,pageable)).thenReturn(usersPage2);

        Page<User> result = friendshipService.getFriendshipsAccepted(userId, pageable);

        assertFalse(result.isEmpty());
        assertEquals(4, result.getTotalElements());
    }

}
