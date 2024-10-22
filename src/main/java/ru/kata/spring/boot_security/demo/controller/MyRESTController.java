package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.exception_hadling.NoSuchUsrException;
import ru.kata.spring.boot_security.demo.exception_hadling.UsrIncorrectData;
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

    @PostMapping("/users")
    public ResponseEntity<?> addNewUser(@RequestBody User user) {
        boolean isSaved  = userService.saveUser(user);
        if (!isSaved) {
            return new ResponseEntity<>("User already exists", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return user;
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        User user = userService.findUserById(id);
        if (user.getUsername() == null) {
            throw new NoSuchUsrException("User with id " + id + " not found");
        }
        userService.deleteUser(id);
        return "User with ID = " + id + " was deleted";
    }
}
