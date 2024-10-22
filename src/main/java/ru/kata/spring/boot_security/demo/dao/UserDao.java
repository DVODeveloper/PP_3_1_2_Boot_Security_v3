package ru.kata.spring.boot_security.demo.dao;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;

import java.util.List;

public interface UserDao {

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    User findUserById(Long userId);

    User findUserByUsername(String username);

    List<User> allUsers();

    void addNewUser(String username, String password, String confirmPassword, String roleName);

    void deleteUser(Long userId);

    void updateUser(User userForm);

    List<Role> findAllRoles();


}
