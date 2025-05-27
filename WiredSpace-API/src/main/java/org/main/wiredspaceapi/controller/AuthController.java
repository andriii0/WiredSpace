package org.main.wiredspaceapi.controller;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.AdminService;
import org.main.wiredspaceapi.business.UserService;
import org.main.wiredspaceapi.domain.Admin;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.security.token.AccessToken;
import org.main.wiredspaceapi.security.token.TokenEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<Object> authenticate(@RequestParam String email,
                                          @RequestParam String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                User u = userOpt.get();
                String token = createJwt(u.getEmail(), u.getId(), u.getRoleAsString());
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("role", u.getRoleAsString());
                response.put("name", u.getName());
                return ResponseEntity.ok(response);
            }

            Optional<Admin> adminOpt = adminService.findAdminByEmail(email);
            if (adminOpt.isPresent()) {
                Admin a = adminOpt.get();
                String token = createJwt(a.getEmail(), a.getId(), a.getRoleAsString());
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("role", a.getRoleAsString());
                response.put("name", a.getName());
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Account not found");

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
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
