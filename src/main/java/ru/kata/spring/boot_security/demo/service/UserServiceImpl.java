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

import java.util.*;

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

    public BCryptPasswordEncoder getbCryptPasswordEncoder() {
        return bCryptPasswordEncoder;
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
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> allUsers() {
        return userRepository.findAll();
    }

    @Override
    public void addNewUser(String username, String password, String confirmPassword, String roleName) {
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }

        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(bCryptPasswordEncoder.encode(password));

        Role userRole = roleRepository.findByName(roleName);
        if (userRole == null) {
            throw new IllegalArgumentException("Роль не найдена");
        }

        newUser.setRoles(Collections.singleton(userRole));

        userRepository.save(newUser);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.getRoles().clear();
        userRepository.save(user);
        userRepository.delete(user);
    }

    @Override
    public void updateUser(User userForm) {
        User user = userRepository.findById(userForm.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setUsername(userForm.getUsername());


        if (userForm.getPassword() != null && !userForm.getPassword().trim().isEmpty()) {
            user.setPassword(bCryptPasswordEncoder.encode(userForm.getPassword()));
        }

        if (userForm.getRoles() != null && !userForm.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (Role role : userForm.getRoles()) {
                Role dbRole = roleRepository.findByName(role.getName());
                roles.add(dbRole);
            }
            user.setRoles(roles);
        }
        userRepository.save(user);
    }

    @Override
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }
}
