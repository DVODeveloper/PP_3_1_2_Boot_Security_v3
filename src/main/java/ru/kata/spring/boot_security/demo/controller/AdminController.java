package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String getAdminPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User currentUser = userService.findUserByUsername(principal.getName());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("allUsers", userService.allUsers());
        model.addAttribute("roles", userService.findAllRoles());
        model.addAttribute("activeTab", "usersTable");
        return "admin";
    }

    @PostMapping("/deleteUser")
    public String deleteUserById(@RequestParam Long userId) {
        userService.deleteUser(userId);
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    @ResponseBody
    public User getUserForEdit(@PathVariable("id") Long id) {
        return userService.findUserById(id);
    }

    @PostMapping("/editUser")
    public String editUser(@ModelAttribute("user") User userForm, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", userService.findAllRoles());
            return "admin";
        }

        userService.updateUser(userForm);

        return "redirect:/admin";
    }

    @PostMapping("/addUser")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          @RequestParam String role,
                          Model model,
                          Principal principal) {

        try {
            userService.addNewUser(username, password, confirmPassword, role);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("activeTab", "newUser");
            return getAdminPage(model, principal);
        }

        return "redirect:/admin";
    }



}
