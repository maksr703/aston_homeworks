package app.controller;

import app.dto.CreateUserRequest;
import app.dto.UpdateUserRequest;
import app.model.User;
import app.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateUser() throws Exception {

        var request = new CreateUserRequest(
                "max@gmail.com",
                "Max",
                30
        );

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("max@gmail.com"))
                .andExpect(jsonPath("$.name").value("Max"))
                .andExpect(jsonPath("$.age").value(30));

        assertEquals(1, userRepository.findAll().size());
    }

    @Test
    void shouldFailCreateUserWhenEmailInvalid() throws Exception {

        var request = new CreateUserRequest(
                "incorrect_email",
                "max",
                30
        );

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailCreateUserWhenNameBlank() throws Exception {

        var request = new CreateUserRequest(
                "max@mail.com",
                "",
                25
        );

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailCreateUserWhenAgeTooLow() throws Exception {

        var request = new CreateUserRequest(
                "max@mail.com",
                "Max",
                10
        );

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllUsers() throws Exception {

        userRepository.save(new User(null, "a@mail.com", "A", 20, Instant.now()));
        userRepository.save(new User(null, "b@mail.com", "B", 30, Instant.now()));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldGetUserById() throws Exception {

        User user = userRepository.save(
                new User(null, "max@mail.com", "max", 30, Instant.now())
        );

        mockMvc.perform(get("/api/v1/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("max@mail.com"));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {

        mockMvc.perform(get("/api/v1/users/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateUser() throws Exception {

        User user = userRepository.save(
                new User(null, "max@mail.com", "max", 30, Instant.now())
        );

        var request = new UpdateUserRequest(
                "new@mail.com",
                "New Name",
                null
        );

        mockMvc.perform(patch("/api/v1/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@mail.com"))
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    void shouldReturnNotFoundOnUpdateIfUserMissing() throws Exception {

        var request = new UpdateUserRequest(
                "new@mail.com",
                "Name",
                30
        );

        mockMvc.perform(patch("/api/v1/users/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteUser() throws Exception {

        User user = userRepository.save(
                new User(null, "max@mail.com", "mAX", 30, Instant.now())
        );

        mockMvc.perform(delete("/api/v1/users/" + user.getId()))
                .andExpect(status().isNoContent());

        assertTrue(userRepository.findById(user.getId()).isEmpty());
    }

    @Test
    void shouldReturnNotFoundWhenDeleteMissingUser() throws Exception {

        mockMvc.perform(delete("/api/v1/users/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
