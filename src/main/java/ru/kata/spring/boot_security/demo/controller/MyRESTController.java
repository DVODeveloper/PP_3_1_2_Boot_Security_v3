package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.exception_hadling.NoSuchUsrException;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MyRESTController {

    private final UserService userService;

    @Autowired
    public MyRESTController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/users")
    public List<User> getAllUsers() {
        List<User> allUsers = userService.allUsers();
        return allUsers;
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        if (user.getUsername() == null) {
            throw new NoSuchUsrException("User with id " + id + " not found");
        }
        return user;
    }

    @GetMapping("/users/me")
    public User getAuthenticatedUser(Authentication authentication) {
        String username = authentication.getName();
        return userService.findUserByUsername(username);
    }

    @PostMapping("/users")
    public ResponseEntity<?> addNewUser(@RequestBody User user) {
        try {
            userService.addNewUser(user.getUsername(),
                    user.getPassword(),
                    user.getPasswordConfirm(),
                    user.getRoles().iterator().next().getName());
        } catch (NoSuchUsrException e) {
            return new ResponseEntity<>("User already exists", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }


    @PutMapping("/users")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        if (user.getId() == null) {
            return new ResponseEntity<>("User ID must not be null", HttpStatus.BAD_REQUEST);  // Проверка наличия ID
        }
        try {
            userService.updateUser(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (NoSuchUsrException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User with ID = " + id + " was deleted");
        } catch (NoSuchUsrException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with id " + id + " not found");
        }
    }
}
