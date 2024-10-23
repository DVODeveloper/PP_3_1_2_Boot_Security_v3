package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;

import java.util.List;

public interface UserService {

    UserDetails loadUserByUsername(String username);

    User findUserById(Long userId);

    List<User> getAllUsers();

    List<Role> getAllRoles();

    boolean saveUser(User userForm);

    String validateAndSaveUser(User userForm);

    String updateUser(User userForm);

    void deleteUser(Long userId);

}
