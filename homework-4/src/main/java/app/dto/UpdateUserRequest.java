package app.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateUserRequest(
        @Email
        String email,

        String name,

        @Min(14)
        @Max(100)
        Integer age
) {}
