package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    @Autowired
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
    public String editUser(@ModelAttribute("user") User userForm, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleRepository.findAll());
            return "admin";
        }

        User user = userService.findUserById(userForm.getId());
        user.setUsername(userForm.getUsername());

        if (userForm.getPassword() != null && !userForm.getPassword().trim().isEmpty()) {
            if (!userForm.getPassword().equals(user.getPassword())) {
                user.setPassword(userService.encodePassword(userForm.getPassword()));

                System.out.println("Пароль user: " + user.getPassword());
                System.out.println("Пароль userForm: " + userForm.getPassword());
            }
        }

        if (userForm.getRoles() != null && !userForm.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (Role role : userForm.getRoles()) {
                Role dbRole = roleRepository.findById(role.getId()).orElse(null);
                if (dbRole != null) {
                    roles.add(dbRole);
                } else {
                    throw new RuntimeException("Role not found");
                }
            }
            user.setRoles(roles);
        }

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
        if (!password.equals(confirmPassword)) {
            model.addAttribute("passwordError", "Пароли не совпадают");
            model.addAttribute("activeTab", "newUser");
            return getAdminPage(model, principal);
        }

        if (userService.findUserByUsername(username) != null) {
            model.addAttribute("usernameError", "Пользователь с таким именем уже существует");
            model.addAttribute("activeTab", "newUser");
            return getAdminPage(model, principal);
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);

        Role userRole = roleRepository.findByName(role);
        if (userRole == null) {
            model.addAttribute("roleError", "Role not found");
            return getAdminPage(model, principal);
        }
        newUser.setRoles(Collections.singleton(userRole));

        if (!userService.saveUser(newUser)) {
            model.addAttribute("usernameError", "User with this username already exists");
            return getAdminPage(model, principal);
        }

        return "redirect:/admin";
    }


}
