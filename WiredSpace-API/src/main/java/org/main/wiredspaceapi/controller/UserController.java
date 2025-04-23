package org.main.wiredspaceapi.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.UserService;
import org.main.wiredspaceapi.controller.converter.AccountMapper;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.controller.dto.user.UserDTO;
import org.main.wiredspaceapi.controller.dto.user.UserCreateDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AccountMapper accountMapper;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        User user = userService.createUser(userCreateDTO.getName(), userCreateDTO.getPassword(), userCreateDTO.getRole());
        return ResponseEntity.ok(accountMapper.userToUserDTO(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(accountMapper.userToUserDTO(user));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers().stream()
                .map(accountMapper::userToUserDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserCreateDTO userCreateDTO) {
        User updatedUser = userService.updateUser(id, userCreateDTO.getName(), userCreateDTO.getPassword(), userCreateDTO.getRole());
        return ResponseEntity.ok(accountMapper.userToUserDTO(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
