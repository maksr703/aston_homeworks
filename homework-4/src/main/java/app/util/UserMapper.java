package app.util;

import app.dto.UserResponse;
import app.model.User;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getEmail(),
                user.getName(),
                user.getAge()
        );
    }
}
