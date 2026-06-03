package service;

import dao.UserDao;
import dto.request.UserCreateRequest;
import dto.request.UserUpdateRequest;
import dto.response.Page;
import dto.response.UserResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import model.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void createUser(@NonNull UserCreateRequest request) {
        User user = new User();
        user.setEmail(request.email().toLowerCase());
        user.setPassword(request.password());
        user.setAge(request.age());
        user.setCreated(Instant.now());
        userDao.save(user);
    }

    @Override
    public Optional<UserResponse> getUserById(@NonNull Long id) {
        Optional<User> userOpt = userDao.findById(id);

        return userOpt.map(user -> new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getAge(),
                user.getCreated()
        ));
    }

    @Override
    public Optional<UserResponse> getUserByEmail(@NonNull String email) {
        Optional<User> userEntity = userDao.findByEmail(email);

        return userEntity.map(user ->
                new UserResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getAge(),
                        user.getCreated()
                )
        );
    }


    @Override
    public Page<UserResponse> getAllUsers() {
        return getAllUsers(0, 10);
    }

    public Page<UserResponse> getAllUsers(int page, int size) {
        long totalElements = userDao.countAllUsers();

        int totalPages = (int) Math.ceil((double) totalElements / size);

        page = Math.max(0, Math.min(page, totalPages > 0 ? totalPages - 1 : 0));

        List<User> users = userDao.findUsersWithPagination(page, size);

        List<UserResponse> userResponses = users.stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getAge(),
                        user.getCreated()
                ))
                .toList();

        return new Page<>(
                userResponses,
                totalPages,
                totalElements
        );
    }

    @Override
    public void updateUser(@NonNull UserUpdateRequest request) {
        Optional<User> userEntity = userDao.findById(request.id());

        if (userEntity.isPresent()) {
            User user = userEntity.get();

            if (!request.email().isEmpty()) user.setEmail(request.email());
            if (!request.password().isEmpty()) user.setPassword(request.password());
            user.setAge(request.age());

            userDao.update(user);
        } else {
            log.warn("User not found with id {}", request.id());
        }
    }

    @Override
    public void deleteUser(@NonNull Long id) {
        Optional<User> userEntity = userDao.findById(id);
        if (userEntity.isPresent()) {
            userDao.delete(userEntity.get());
        } else {
            log.warn("User not found with id {}", id);
        }
    }
}
