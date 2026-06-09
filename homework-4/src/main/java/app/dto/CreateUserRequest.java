package app.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
