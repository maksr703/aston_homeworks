package service;

import dao.UserDao;
import dto.request.UserCreateRequest;
import dto.request.UserUpdateRequest;
import dto.response.Page;
import dto.response.UserResponse;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceUnitTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_ShouldSaveUser() {
        UserCreateRequest request = new UserCreateRequest("test@example.com", "password123", 25);

        userService.createUser(request);

        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    void testGetUserById_WhenUserExists_ShouldReturnUser() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setAge(25);
        user.setCreated(Instant.now());

        when(userDao.findById(userId)).thenReturn(Optional.of(user));

        Optional<UserResponse> result = userService.getUserById(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().id());
        assertEquals("test@example.com", result.get().email());
        assertEquals(25, result.get().age());
    }

    @Test
    void testGetUserById_WhenUserNotExists_ShouldReturnEmpty() {
        Long nonExistentId = 999L;

        when(userDao.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<UserResponse> result = userService.getUserById(nonExistentId);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetUserByEmail_WhenUserExists_ShouldReturnUser() {
        String email = "test@example.com";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setAge(30);
        user.setCreated(Instant.now());

        when(userDao.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<UserResponse> result = userService.getUserByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().email());
        assertEquals(30, result.get().age());
    }

    @Test
    void testGetUserByEmail_WhenUserNotExists_ShouldReturnEmpty() {
        String nonExistentEmail = "nonexistent@example.com";

        when(userDao.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        Optional<UserResponse> result = userService.getUserByEmail(nonExistentEmail);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetAllUsers_ShouldCallDaoMethods() {
        when(userDao.countAllUsers()).thenReturn(0L);
        when(userDao.findUsersWithPagination(0, 10)).thenReturn(List.of());

        Page<UserResponse> result = userService.getAllUsers();

        verify(userDao).countAllUsers();
        verify(userDao).findUsersWithPagination(0, 10);

        assertEquals(0, result.content().size());
    }

    @Test
    void testGetAllUsersWithPagination_ShouldReturnPage() {
        int page = 0;
        int size = 2;

        long totalElements = 5L;

        User user1 = new User(1L, "user1@example.com", "pass", 20, Instant.now());
        User user2 = new User(2L, "user2@example.com", "pass", 25, Instant.now());
        List<User> users = List.of(user1, user2);

        when(userDao.countAllUsers()).thenReturn(totalElements);
        when(userDao.findUsersWithPagination(page, size)).thenReturn(users);

        Page<UserResponse> result = userService.getAllUsers(page, size);

        assertEquals(2, result.content().size());

        assertEquals(totalElements, result.totalElements());
    }

    @Test
    void testUpdateUser_WhenUserExists_ShouldUpdate() {
        UserUpdateRequest request = new UserUpdateRequest(1L, "new@example.com", "newpass", 30);

        User existingUser = new User(1L, "old@example.com", "oldpass", 25, Instant.now());
        when(userDao.findById(1L)).thenReturn(Optional.of(existingUser));

        userService.updateUser(request);

        verify(userDao, times(1)).update(any(User.class));
    }

    @Test
    void testUpdateUser_WhenUserNotExists_ShouldNotThrowException() {
        UserUpdateRequest request = new UserUpdateRequest(999L, "new@example.com", "newpass", 30);

        when(userDao.findById(999L)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> userService.updateUser(request));
    }

    @Test
    void testDeleteUser_WhenUserExists_ShouldDelete() {
        Long userId = 1L;
        User user = new User(userId, "test@example.com", "pass", 25, Instant.now());

        when(userDao.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userDao, times(1)).delete(user);
    }

    @Test
    void testDeleteUser_WhenUserNotExists_ShouldNotThrowException() {
        Long nonExistentId = 999L;

        when(userDao.findById(nonExistentId)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> userService.deleteUser(nonExistentId));
    }
}