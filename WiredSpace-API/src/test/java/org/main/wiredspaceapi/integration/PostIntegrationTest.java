package org.main.wiredspaceapi.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.main.wiredspaceapi.controller.dto.post.CommentDTO;
import org.main.wiredspaceapi.controller.dto.post.PostCreateDTO;
import org.main.wiredspaceapi.controller.dto.user.UserCreateDTO;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class PostIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String token;
    private String userId;
    private Long postId;
    private Long commentId;

    @BeforeEach
    void setup() throws Exception {
        String email = "postuser+" + UUID.randomUUID() + "@example.com";
        userId = registerUser("Poster", email, "postpass");
        token = loginAndGetToken(email, "postpass");
    }

    private String registerUser(String name, String email, String password) throws Exception {
        UserCreateDTO dto = new UserCreateDTO(name, email, password, UserRole.STANDARD_USER);
        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk()).andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        Map<String, String> login = Map.of("email", email, "password", password);
        MvcResult result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk()).andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    void createPost_shouldReturnCreatedPost() throws Exception {
        PostCreateDTO postDTO = new PostCreateDTO();
        postDTO.setContent("Test post");

        MvcResult result = mockMvc.perform(post("/api/posts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Test post"))
                .andReturn();

        postId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void addCommentToPost_shouldReturnCreatedComment() throws Exception {
        long postId = createPost("Post for comment");
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent("Nice!");

        MvcResult result = mockMvc.perform(post("/api/posts/" + postId + "/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Nice!"))
                .andReturn();

        commentId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void likePost_shouldIncreaseLikeCount() throws Exception {
        long postId = createPost("Post to like");

        mockMvc.perform(post("/api/posts/" + postId + "/like")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts/" + postId + "/likes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deleteComment_shouldReturnNoContent() throws Exception {
        long postId = createPost("Post to delete comment from");
        long commentId = addComment(postId, "To be deleted");

        mockMvc.perform(delete("/api/posts/comments/" + commentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePost_shouldReturnNoContent() throws Exception {
        long postId = createPost("Post to delete");

        mockMvc.perform(delete("/api/posts/" + postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    private long createPost(String content) throws Exception {
        PostCreateDTO dto = new PostCreateDTO();
        dto.setContent(content);

        MvcResult result = mockMvc.perform(post("/api/posts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private long addComment(long postId, String comment) throws Exception {
        CommentDTO dto = new CommentDTO();
        dto.setContent(comment);

        MvcResult result = mockMvc.perform(post("/api/posts/" + postId + "/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
}
