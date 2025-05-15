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
    public ResponseEntity<?> authenticate(@RequestParam String email,
                                          @RequestParam String password) {
        try {
            // 1) Сам факт пароля проверит Spring через DaoAuthenticationProvider
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            // 2) После этого — ищем сначала в User, потом в Admin
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                User u = userOpt.get();
                return ResponseEntity.ok(createJwt(u.getEmail(), u.getId(), u.getRoleAsString()));
            }

            Optional<Admin> adminOpt = adminService.findAdminByEmail(email);
            if (adminOpt.isPresent()) {
                Admin a = adminOpt.get();
                return ResponseEntity.ok(createJwt(a.getEmail(), a.getId(), a.getRoleAsString()));
            }

            // 3) Если нет ни там, ни там — 404
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
