package org.main.wiredspaceapi.controller;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.AdminService;
import org.main.wiredspaceapi.business.UserService;
import org.main.wiredspaceapi.controller.dto.args.LoginRequest;
import org.main.wiredspaceapi.controller.exceptions.InvalidCredentialsException;
import org.main.wiredspaceapi.controller.exceptions.UserNotFoundException;
import org.main.wiredspaceapi.domain.Admin;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.security.token.AccessToken;
import org.main.wiredspaceapi.security.token.TokenEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenEncoder accessTokenEncoder;
    private final UserService userService;
    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<Object> authenticate(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        Optional<User> userOpt = userService.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return ResponseEntity.ok(createResponse(user.getEmail(), user.getId(), user.getRoleAsString(), user.getName()));
        }

        Optional<Admin> adminOpt = adminService.findAdminByEmail(request.getEmail());
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            return ResponseEntity.ok(createResponse(admin.getEmail(), admin.getId(), admin.getRoleAsString(), admin.getName()));
        }

        throw new UserNotFoundException("Account not found");
    }

    private Map<String, Object> createResponse(String email, UUID id, String role, String name) {
        String token = createJwt(email, id, role);
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("name", name);
        return response;
    }

    private String createJwt(String subject, UUID accountId, String role) {
        AccessToken token = AccessToken.builder()
                .subject(subject)
                .accountId(accountId)
                .role(role)
                .build();
        return accessTokenEncoder.encode(token);
    }
}
