package app.dto;

import java.time.Instant;

public record UserResponse (String email, String name, Integer age) {}
