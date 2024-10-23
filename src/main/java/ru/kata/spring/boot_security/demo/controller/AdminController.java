package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String userList(Model model) {
        model.addAttribute("allUsers", userService.getAllUsers());
        return "admin";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());
        model.addAttribute("allRoles", userService.getAllRoles());
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", userService.getAllRoles());
            return "registration";
        }
        String errorMessage = userService.validateAndSaveUser(userForm);
        if (errorMessage != null) {
            model.addAttribute("allRoles", userService.getAllRoles());
            model.addAttribute("error", errorMessage);
            return "registration";
        }
        userService.saveUser(userForm);

        return "redirect:/admin";
    }

    @PostMapping("")
    public String deleteUser(@RequestParam(required = true) Long userId,
                             @RequestParam(required = true) String action) {
        if (action.equals("delete")) {
            userService.deleteUser(userId);
        }
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", userService.findUserById(id));
        model.addAttribute("allRoles", userService.getAllRoles());
        return "edit_user";
    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute("user") User userForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", userService.getAllRoles());
            return "edit_user";
        }

        String errorMessage = userService.updateUser(userForm);
        if (errorMessage != null) {
            model.addAttribute("error", errorMessage);
            model.addAttribute("allRoles", userService.getAllRoles());
            return "edit_user";
        }

        return "redirect:/admin";
    }
}

