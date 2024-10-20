package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.exception_handling.UserNotFoundException;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
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

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID = " + userId));
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public boolean saveUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return false;  // Пользователь уже существует
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));  // Шифруем пароль

        Set<Role> userRoles = new HashSet<>();
        for (Role role: user.getRoles()) {
            Role userRole = roleRepository.findByName(role.getName());
            if (userRole != null) {
                userRoles.add(userRole);
            } else {
                throw new IllegalArgumentException("Role " + role.getName() + " not found");
            }
        }
        user.setRoles(userRoles);

        userRepository.save(user);
        return true;
    }


    public boolean deleteUser(Long userId) {
        try {
            userRepository.deleteById(userId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            System.err.println("Error deleting user with ID: " + userId);
            return false;
        }
    }

    public List<User> usergtList(Long idMin) {
        return userRepository.findAll();
    }

    public void updateUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword())); // Шифрование пароля
        }
        userRepository.save(user);
    }

    public void setUserRole(User user, Role role) {
        if (role != null) {
            user.setRoles(new HashSet<>(Collections.singletonList(role)));
        } else {
            throw new IllegalArgumentException("Role not found");
        }
    }

    public String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

}
