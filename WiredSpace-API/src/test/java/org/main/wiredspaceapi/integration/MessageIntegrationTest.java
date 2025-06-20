package org.main.wiredspaceapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.main.wiredspaceapi.controller.dto.message.MessageDTO;
import org.main.wiredspaceapi.controller.dto.user.UserCreateDTO;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class MessageIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String register(String name, String email, String password) throws Exception {
        UserCreateDTO dto = new UserCreateDTO(name, email, password, UserRole.STANDARD_USER);

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
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(json).get("token").asText();
    }

    @Test
    void sendAndRetrievePrivateMessage() throws Exception {
        String senderEmail = "sender@example.com";
        String receiverEmail = "receiver@example.com";

        register("Sender", senderEmail, "pass123");
        register("Receiver", receiverEmail, "pass123");

        String token = login(senderEmail, "pass123");

        MessageDTO msg = new MessageDTO();
        msg.setTo(receiverEmail);
        msg.setText("Hello from integration test!");

        mockMvc.perform(post("/api/messages/private")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(msg)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value("Hello from integration test!"))
                .andExpect(jsonPath("$.from").value(senderEmail));
    }

    @Test
    void getMessagesBetweenUsers_shouldReturnConversation() throws Exception {
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";

        register("User1", email1, "pass123");
        register("User2", email2, "pass123");

        String token1 = login(email1, "pass123");

        MessageDTO msg = new MessageDTO();
        msg.setTo(email2);
        msg.setText("Message between users");

        mockMvc.perform(post("/api/messages/private")
                        .header("Authorization", "Bearer " + token1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(msg)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/messages/conversation/" + email2)
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].text").value("Message between users"));
    }

    @Test
    void getChatPreviews_shouldReturnLatestMessages() throws Exception {
        String email1 = "preview1@example.com";
        String email2 = "preview2@example.com";

        register("Preview One", email1, "pass123");
        register("Preview Two", email2, "pass123");

        String token1 = login(email1, "pass123");

        MessageDTO message = new MessageDTO();
        message.setTo(email2);
        message.setText("Latest message");

        mockMvc.perform(post("/api/messages/private")
                        .header("Authorization", "Bearer " + token1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(message)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/messages/chats")
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].message.text").value("Latest message"));
    }
}
