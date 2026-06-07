package app.dto;

import jakarta.validation.constraints.*;

public record UpdateUserRequest(
        @Email
        String email,

        String name,

        @Min(14)
        @Max(100)
        Integer age
) {}
