package org.main.wiredspaceapi.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.UserService;
import org.main.wiredspaceapi.controller.dto.user.PagedUserResponse;
import org.main.wiredspaceapi.controller.mapper.UserMapper;
import org.main.wiredspaceapi.controller.dto.user.UserCreateDTO;
import org.main.wiredspaceapi.controller.dto.user.UserDTO;
import org.main.wiredspaceapi.controller.dto.user.UserUpdateDTO;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
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
                .toList();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> updateCurrentUser(
            @Valid @RequestBody UserUpdateDTO dto
    ) {
        Optional<User> updated = userService.updateUserById(
                authenticatedUserProvider.getCurrentUserId(),
                dto.getName(),
                dto.getEmail(),
                dto.getPassword()
        );


        return updated
                .map(user -> ResponseEntity.ok(userMapper.userToUserDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<Void> deleteCurrentUser() {
        userService.deleteUserByEmail(authenticatedUserProvider.getCurrentUserEmail());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<PagedUserResponse> searchUsers(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<UserDTO> users = userService.searchUsers(query, offset, limit)
                .stream()
                .map(userMapper::userToUserDTO)
                .toList();

        long total = userService.countSearchUsers(query);

        return ResponseEntity.ok(new PagedUserResponse(users, total));
    }
}
