package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public boolean saveUser(User userForm) {
        if (userRepository.findByUsername(userForm.getUsername()) != null) {
            return false;
        }
        Set<Role> roles = new HashSet<>();
        for (Role role : userForm.getRoles()) {
            Role dbRole = roleRepository.findById(role.getId()).orElse(null);
            if (dbRole != null) {
                roles.add(role);
            }
        }
        User newUser = new User();
        newUser.setUsername(userForm.getUsername());
        newUser.setPassword(bCryptPasswordEncoder.encode(userForm.getPassword()));
        newUser.setRoles(roles);
        userRepository.save(newUser);

        return true;
    }

    @Override
    public String validateAndSaveUser(User userForm) {
        if (userForm.getPasswordConfirm() == null || !userForm.getPassword().equals(userForm.getPasswordConfirm())) {
            return "Пароли не совпадают";
        }

        if (!saveUser(userForm)) {
            return "Пользователь с таким именем уже существует";
        }

        return null;
    }

    @Override
    public String updateUser(User userForm) {
        User user = findUserById(userForm.getId());
        user.setUsername(userForm.getUsername());

        if (userForm.getPassword() != null && !userForm.getPassword().isEmpty()) {
            if (userForm.getPasswordConfirm() == null || !userForm.getPassword().equals(userForm.getPasswordConfirm())) {
                return "Пароли не совпадают";
            }
            user.setPassword(bCryptPasswordEncoder.encode(userForm.getPassword()));
        }

        Set<Role> roles = new HashSet<>();
        for (Role role : userForm.getRoles()) {
            Role dbRole = roleRepository.findById(role.getId()).orElse(null);
            if (dbRole != null) {
                roles.add(dbRole);
            } else {
                return "Роль не найдена: " + role.getId();
            }
        }
        user.setRoles(roles);
        userRepository.save(user);
        return null;
    }

    @Override
    public void deleteUser(Long userId) {
        User user = findUserById(userId);
        user.getRoles().clear();
        userRepository.save(user);
        userRepository.delete(user);
    }
}
