package org.main.wiredspaceapi.controller;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.UserService;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.dto.UserDTO;
import org.main.wiredspaceapi.dto.UserCreateDTO;
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

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        User user = userService.createUser(userCreateDTO.getName(), userCreateDTO.getPassword(), userCreateDTO.getRole());
        return ResponseEntity.ok(new UserDTO(user.getId(), user.getName(), user.getRole()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userService.getUserById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getRole());
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers().stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getRole()))
                .collect(Collectors.toList());
            return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserCreateDTO userCreateDTO) {
        User updatedUser = userService.updateUser(id, userCreateDTO.getName(), userCreateDTO.getPassword(), userCreateDTO.getRole());
        return ResponseEntity.ok(new UserDTO(updatedUser.getId(), updatedUser.getName(), updatedUser.getRole()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
