package org.main.wiredspaceapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.main.wiredspaceapi.controller.dto.friendship.FriendshipRequestDTO;
import org.main.wiredspaceapi.controller.dto.user.UserCreateDTO;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class FriendshipIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String registerUser(String name, String email, String password, UserRole role) throws Exception {
        UserCreateDTO dto = new UserCreateDTO(name, email, password, role);
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
        return email;
    }

    private String login(String email, String password) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        String json = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(json).get("token").asText();
    }

    private UUID extractUserIdFromToken(String token) throws Exception {
        String[] parts = token.split("\\.");
        String payload = new String(Base64.getDecoder().decode(parts[1]));
        return UUID.fromString(objectMapper.readTree(payload).get("userId").asText());
    }

    @Test
    void testFriendshipFlow() throws Exception {
        String user1Email = registerUser("Alice", "alice@example.com", "pass123", UserRole.STANDARD_USER);
        String user2Email = registerUser("Bob", "bob@example.com", "pass123", UserRole.STANDARD_USER);

        String token1 = login(user1Email, "pass123");
        String token2 = login(user2Email, "pass123");

        UUID user2Id = extractUserIdFromToken(token2);

        FriendshipRequestDTO request = new FriendshipRequestDTO(user2Id, false);
        String friendshipJson = mockMvc.perform(post("/api/friendships")
                        .header("Authorization", "Bearer " + token1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accepted").value(false))
                .andReturn().getResponse().getContentAsString();

        UUID friendshipId = UUID.fromString(objectMapper.readTree(friendshipJson).get("id").asText());

        mockMvc.perform(get("/api/friendships/status")
                        .header("Authorization", "Bearer " + token1)
                        .param("friendId", user2Id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("sent"));

        mockMvc.perform(put("/api/friendships/" + friendshipId + "/accept")
                        .header("Authorization", "Bearer " + token2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accepted").value(true));

        mockMvc.perform(get("/api/friendships/me")
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/api/friendships/me")
                        .header("Authorization", "Bearer " + token2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(delete("/api/friendships/" + friendshipId)
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/friendships/me")
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
