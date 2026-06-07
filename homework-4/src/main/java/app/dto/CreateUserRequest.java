package app.dto;

import jakarta.validation.constraints.*;

public record CreateUserRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String name,

        @NotNull
        @Min(14)
        @Max(100)
        Integer age
) { }
