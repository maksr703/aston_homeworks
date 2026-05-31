package service;

import dto.request.UserCreateRequest;
import dto.request.UserUpdateRequest;
import dto.response.Page;
import dto.response.UserResponse;

import java.awt.print.Pageable;
import java.util.Optional;

public interface UserService {

    void createUser(UserCreateRequest request);
    void updateUser(UserUpdateRequest request);
    void deleteUser(Long id);

    Page<UserResponse> getAllUsers();
    Page<UserResponse> getAllUsers(int page, int size);
    Optional<UserResponse> getUserById(Long id);
    Optional<UserResponse> getUserByEmail(String email);
}
