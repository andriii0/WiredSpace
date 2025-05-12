package org.main.wiredspaceapi.security.auth;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.UserService;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.security.authDTO.AuthenticationRequest;
import org.main.wiredspaceapi.security.authDTO.AuthenticationResponse;
import org.main.wiredspaceapi.security.jwt.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userService.findUserByName(request.username());

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }
}
