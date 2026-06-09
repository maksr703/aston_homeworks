package app.service;

import app.dto.CreateUserRequest;
import app.dto.UpdateUserRequest;
import app.dto.UserResponse;
import app.exception.UserNotFoundException;
import app.util.UserMapper;
import lombok.RequiredArgsConstructor;
import app.model.User;
import org.springframework.stereotype.Service;
import app.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(id));

        return UserMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    public UserResponse create(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "User with email already exists"
            );
        }

        User user = new User();

        user.setEmail(request.email());
        user.setName(request.name());
        user.setAge(request.age());

        User userSaved = userRepository.save(user);

        return UserMapper.toResponse(userSaved);
    }

    public UserResponse update(
            UUID id,
            UpdateUserRequest request
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(id));

        if (request.email() != null) {
            user.setEmail(request.email());
        }

        if (request.name() != null) {
            user.setName(request.name());
        }

        if (request.age() != null) {
            user.setAge(request.age());
        }

        User updated = userRepository.save(user);

        return UserMapper.toResponse(updated);
    }

    public void delete(UUID id) {

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
    }
}
