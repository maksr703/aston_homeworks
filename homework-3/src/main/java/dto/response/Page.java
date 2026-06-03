package dto.response;

import java.util.List;

public record Page<T>(List<T> content, int totalPages, long totalElements) {}
