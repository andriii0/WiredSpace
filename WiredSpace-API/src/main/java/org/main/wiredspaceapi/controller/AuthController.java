package org.main.wiredspaceapi.controller;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.security.token.AccessToken;
import org.main.wiredspaceapi.security.token.TokenEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenEncoder accessTokenEncoder;

    @PostMapping
    public ResponseEntity<?> authenticate(@RequestParam String email, @RequestParam String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            String subject = authentication.getName();
            Object userIdObj = authentication.getDetails();

            AccessToken token = AccessToken.builder()
                    .subject(subject)
                    .accountId((userIdObj instanceof String) ? java.util.UUID.fromString((String) userIdObj)
                            : (java.util.UUID) userIdObj)
                    .build();

            String jwt = accessTokenEncoder.encode(token);

            return ResponseEntity.ok(jwt);
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }
}
