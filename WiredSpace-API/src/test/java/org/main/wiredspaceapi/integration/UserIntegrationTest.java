package org.main.wiredspaceapi.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.main.wiredspaceapi.controller.dto.user.UserCreateDTO;
import org.main.wiredspaceapi.controller.dto.user.UserUpdateDTO;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class UserIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String generateUniqueEmail() {
        return "user+" + UUID.randomUUID() + "@example.com";
    }

    private String registerUser(String name, String email, String password) throws Exception {
        UserCreateDTO dto = new UserCreateDTO(name, email, password, UserRole.STANDARD_USER);

        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asText();
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        MvcResult loginResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("token").asText();
    }

    @Test
    void createUser_shouldReturnOkAndUserData() throws Exception {
        String email = generateUniqueEmail();
        UserCreateDTO dto = new UserCreateDTO("Test User", email, "password123", UserRole.STANDARD_USER);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void loginUser_shouldReturnToken() throws Exception {
        String email = generateUniqueEmail();
        registerUser("Login User", email, "pass123");

        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("email", email);
        loginBody.put("password", "pass123");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.name").value("Login User"));
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        String email = generateUniqueEmail();
        String userId = registerUser("Get User", email, "getpass123");
        String token = loginAndGetToken(email, "getpass123");

        mockMvc.perform(get("/api/users/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.name").value("Get User"));
    }

    @Test
    void updateUser_shouldReturnUpdatedData() throws Exception {
        String email = generateUniqueEmail();
        registerUser("Old Name", email, "123456");
        String token = loginAndGetToken(email, "123456");

        UserUpdateDTO updateDTO = new UserUpdateDTO("New Name", null, null);

        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void deleteUser_shouldRemoveUser() throws Exception {
        String email = generateUniqueEmail();
        String userId = registerUser("Delete Me", email, "pass123");
        String token = loginAndGetToken(email, "pass123");

        mockMvc.perform(delete("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_withExistingEmail_shouldReturnError() throws Exception {
        String email = generateUniqueEmail();
        registerUser("Original", email, "123456");

        UserCreateDTO duplicate = new UserCreateDTO("Another", email, "otherpass", UserRole.STANDARD_USER);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getUserById_shouldReturn404ForNonExistingUser() throws Exception {
        String email = generateUniqueEmail();
        registerUser("Test", email, "password123");
        String token = loginAndGetToken(email, "password123");

        mockMvc.perform(get("/api/users/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchUsers_shouldReturnPagedResults() throws Exception {
        String email1 = generateUniqueEmail(); // Anna One
        String email2 = generateUniqueEmail(); // Anna Two
        String email3 = generateUniqueEmail(); // Bob

        registerUser("Anna One", email1, "pass123");
        registerUser("Anna Two", email2, "pass123");
        registerUser("Bob Three", email3, "pass123");

        String token = loginAndGetToken(email3, "pass123");

        mockMvc.perform(get("/api/users/search")
                        .param("query", "Anna")
                        .param("offset", "0")
                        .param("limit", "2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users.length()").value(2))
                .andExpect(jsonPath("$.total").value(2));
    }

    @Test
    void updateUser_withEmailConflict_shouldReturnError() throws Exception {
        String emailA = generateUniqueEmail();
        String emailB = generateUniqueEmail();

        registerUser("User A", emailA, "123456");
        registerUser("User B", emailB, "654321");

        String token = loginAndGetToken(emailB, "654321");

        UserUpdateDTO dto = new UserUpdateDTO("NewName", emailA, null);

        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }
}
