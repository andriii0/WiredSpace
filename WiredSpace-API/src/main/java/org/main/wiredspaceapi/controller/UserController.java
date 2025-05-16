package org.main.wiredspaceapi.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.UserService;
import org.main.wiredspaceapi.controller.converter.UserMapper;
import org.main.wiredspaceapi.controller.dto.user.UserCreateDTO;
import org.main.wiredspaceapi.controller.dto.user.UserDTO;
import org.main.wiredspaceapi.controller.dto.user.UserUpdateDTO;
import org.main.wiredspaceapi.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        User user = userService.createUser(
                userCreateDTO.getName(),
                userCreateDTO.getEmail(),
                userCreateDTO.getPassword(),
                userCreateDTO.getRole()
        );
        return ResponseEntity.ok(userMapper.userToUserDTO(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(userMapper.userToUserDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers().stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> updateCurrentUser(
            @RequestBody UserUpdateDTO dto,
            Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();

        Optional<User> updated = userService.updateUserByEmail(
                email,
                dto.getName(),
                dto.getEmail(),
                dto.getPassword()
        );

        return updated
                .map(user -> ResponseEntity.ok(userMapper.userToUserDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }




//    @PutMapping("/{id}")
//    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @RequestBody UserCreateDTO userCreateDTO) {
//        return userService.updateUser(
//                        id,
//                        userCreateDTO.getName(),
//                        userCreateDTO.getEmail(),
//                        userCreateDTO.getPassword(),
//                        userCreateDTO.getRole()
//                )
//                .map(user -> ResponseEntity.ok(userMapper.userToUserDTO(user)))
//                .orElse(ResponseEntity.notFound().build());
//    }

    @Transactional
    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();
        System.out.println("Deleting user: " + email);

        userService.deleteUserByEmail(email);

        return ResponseEntity.ok().build();
    }

}
