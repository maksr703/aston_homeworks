package dto.request;

public record UserUpdateRequest(Long id, String email, String password, Integer age) {}
