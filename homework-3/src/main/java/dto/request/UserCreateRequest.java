package dto.request;

public record UserCreateRequest(String email, String password, Integer age) {}
