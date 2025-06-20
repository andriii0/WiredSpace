package org.main.wiredspaceapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.main.wiredspaceapi.controller.dto.post.PostCreateDTO;
import org.main.wiredspaceapi.controller.dto.post.ReportDTO;
import org.main.wiredspaceapi.controller.dto.user.UserCreateDTO;
import org.main.wiredspaceapi.domain.enums.AdminRole;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.main.wiredspaceapi.persistence.AdminRepository;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.entity.AdminEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class ReportIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private AdminRepository adminRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private PostRepository postRepository;

    private final String superAdminEmail = "superadmin@example.com";
    private final String superAdminPassword = "pass123";

    @BeforeEach
    void setupSuperAdmin() {
        if (adminRepository.findByEmail(superAdminEmail).isEmpty()) {
            adminRepository.createAdmin(
                    "Super Admin",
                    superAdminEmail,
                    passwordEncoder.encode(superAdminPassword),
                    AdminRole.ADMIN
            );
        }
    }

    private String registerUser(String name, String email, String password, UserRole role) throws Exception {
        UserCreateDTO dto = new UserCreateDTO(name, email, password, role);
        var response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn()
                .getResponse()
                .getStatus();
        assertTrue(response == 200 || response == 409);
        return email;
    }

    private String registerAdminViaController(String name, String email, String password, AdminRole role, String jwt) throws Exception {
        mockMvc.perform(post("/api/admin/admin/create")
                        .header("Authorization", "Bearer " + jwt)
                        .param("name", name)
                        .param("email", email)
                        .param("password", password)
                        .param("role", role.name()))
                .andExpect(status().isOk());
        assertTrue(adminRepository.findByEmail(email).isPresent());
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

    private Long createPost(String jwt) throws Exception {
        PostCreateDTO post = new PostCreateDTO();
        post.setContent("Test Content");

        String json = mockMvc.perform(post("/api/posts")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(json).get("id").asLong();
    }

    @Test
    void userCanReportPost() throws Exception {
        String userEmail = "user_" + UUID.randomUUID() + "@example.com";
        registerUser("User", userEmail, "pass123", UserRole.STANDARD_USER);
        String userToken = login(userEmail, "pass123");

        Long postId = createPost(userToken);

        ReportDTO report = new ReportDTO();
        report.setReason("Spam or abuse");

        mockMvc.perform(post("/api/reports/" + postId)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report)))
                .andExpect(status().isCreated());
    }

    @Test
    void duplicateReportShouldFail() throws Exception {
        String userEmail = "user_" + UUID.randomUUID() + "@example.com";
        registerUser("User", userEmail, "pass123", UserRole.STANDARD_USER);
        String userToken = login(userEmail, "pass123");

        Long postId = createPost(userToken);

        ReportDTO report = new ReportDTO();
        report.setReason("Inappropriate content");

        mockMvc.perform(post("/api/reports/" + postId)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/reports/" + postId)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report)))
                .andExpect(status().isConflict());
    }

    @Test
    void adminCanViewAndDeleteReports() throws Exception {
        String superToken = login(superAdminEmail, superAdminPassword);

        String adminEmail = "admin_" + UUID.randomUUID() + "@example.com";
        registerAdminViaController("Admin", adminEmail, "pass123", AdminRole.ADMIN, superToken);
        String adminToken = login(adminEmail, "pass123");

        String userEmail = "user_" + UUID.randomUUID() + "@example.com";
        registerUser("User", userEmail, "pass123", UserRole.STANDARD_USER);
        String userToken = login(userEmail, "pass123");

        Long postId = createPost(userToken);

        ReportDTO report = new ReportDTO();
        report.setReason("Harassment");

        mockMvc.perform(post("/api/reports/" + postId)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/reports")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.postId == " + postId + ")]").exists());

        mockMvc.perform(get("/api/reports/post/" + postId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.postId == " + postId + ")]").exists());

        mockMvc.perform(delete("/api/reports/post/" + postId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/reports/post/" + postId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.postId == " + postId + ")]").doesNotExist());
    }

}
