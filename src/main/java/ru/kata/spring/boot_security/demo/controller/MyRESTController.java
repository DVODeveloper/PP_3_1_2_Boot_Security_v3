package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.exception_handling.UserIncorrectData;
import ru.kata.spring.boot_security.demo.exception_handling.UserNotFoundException;
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
    public List<User> showAllUsers() {

        return userService.allUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {

        User user = userService.findUserById(id);
        if (user == null) {
            throw new UserNotFoundException("There is no user with id " + id);
        }
        return user;
    }

    @PostMapping("/users")
    public ResponseEntity<?> addNewUser(@RequestBody User user) {

        boolean isSaved = userService.saveUser(user);
        if (!isSaved) {
            return new ResponseEntity<>("User already exists", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/users")
    public ResponseEntity<?> updateUser(@RequestBody User user) {

        userService.saveUser(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public String deleteUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        if (user == null) {
            throw new UserNotFoundException("There is no user with id " + id);
        }

        userService.deleteUser(id);
        return "User with id " + id + " was deleted";
    }

}
