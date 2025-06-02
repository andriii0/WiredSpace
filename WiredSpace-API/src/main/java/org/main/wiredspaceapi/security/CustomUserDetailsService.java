package org.main.wiredspaceapi.security;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.AdminService;
import org.main.wiredspaceapi.business.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;
    private final AdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userService.findByEmail(email)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .roles(user.getRole().name())
                        .build())
                .or(() -> adminService.findAdminByEmail(email)
                        .map(admin -> org.springframework.security.core.userdetails.User
                                .withUsername(admin.getEmail())
                                .password(admin.getPassword())
                                .roles(admin.getRole().name())
                                .build()))
                .orElseThrow(() -> new UsernameNotFoundException("User or Admin not found with email: " + email));
    }
}
