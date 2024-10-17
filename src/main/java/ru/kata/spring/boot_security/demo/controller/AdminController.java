package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Controller
public class AdminController {

    private final UserService userService;

    private final RoleRepository roleRepository;

    public AdminController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/admin")
    public String getAdminPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User currentUser = userService.findUserByUsername(principal.getName());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("allUsers", userService.allUsers());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("activeTab", "usersTable");
        return "admin";
    }

    @GetMapping("/admin/gt/{userId}")
    public String gtUser(@PathVariable("userId") Long userId, Model model) {
        model.addAttribute("allUsers", userService.usergtList(userId));
        return "admin";
    }

    @PostMapping("/admin/deleteUser")
    public String deleteUserById(@RequestParam Long userId) {
        userService.deleteUser(userId);
        return "redirect:/admin";
    }

    @GetMapping("/admin/edit/{id}")
    @ResponseBody
    public User getUserForEdit(@PathVariable("id") Long id) {
        return userService.findUserById(id);
    }

    @PostMapping("/admin/editUser")
    public String editUser(@RequestParam("id") Long id,
                           @RequestParam("username") String username,
                           @RequestParam(value = "password", required = false) String password,
                           @RequestParam("role") String role) {

        User user = userService.findUserById(id);
        user.setUsername(username);

        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(userService.encodePassword(password));
        }

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(role);
        if (userRole != null) {
            roles.add(userRole);
        } else {
            throw new IllegalArgumentException("Role not found: " + role);
        }
        user.setRoles(roles);

        userService.updateUser(user);

        return "redirect:/admin";
    }





    @PostMapping("/admin/addUser")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          @RequestParam String role,
                          Model model,
                          Principal principal) {
        // Проверка совпадения паролей
        if (!password.equals(confirmPassword)) {
            model.addAttribute("passwordError", "Пароли не совпадают");
            model.addAttribute("activeTab", "newUser");
            return getAdminPage(model, principal);  // Возврат на страницу админа с ошибкой
        }

        if (userService.findUserByUsername(username) != null) {
            model.addAttribute("usernameError", "Пользователь с таким именем уже существует");
            model.addAttribute("activeTab", "newUser");
            return getAdminPage(model, principal);  // Возврат на страницу с ошибкой
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);  // Пароль будет зашифрован в сервисе

        Role userRole = roleRepository.findByName(role);
        if (userRole == null) {
            model.addAttribute("roleError", "Role not found");
            return getAdminPage(model, principal);
        }
        newUser.setRoles(Collections.singleton(userRole));

        if (!userService.saveUser(newUser)) {
            model.addAttribute("usernameError", "User with this username already exists");
            return getAdminPage(model, principal);  // Возврат на страницу с ошибкой
        }

        return "redirect:/admin";  // Перенаправление на страницу админа после успешного добавления
    }


}
