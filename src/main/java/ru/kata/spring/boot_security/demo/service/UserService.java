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
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public boolean saveUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return false;
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        Set<Role> roles = new HashSet<>();
        for (Role roleId : user.getRoles()) {
            Role dbRole = roleRepository.findById(roleId.getId()).orElse(null);
            if (dbRole != null) {
                roles.add(dbRole);
            } else {
                throw new IllegalArgumentException("Role " + roleId.getName() + " not found");
            }
        }
        user.setRoles(roles);
        userRepository.save(user);

        return true;
    }


    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.getRoles().clear();
        userRepository.save(user);
        userRepository.delete(user);
    }

    public List<User> usergtList(Long idMin) {
        return userRepository.findAll();
    }

    public void updateUser(User userForm) {
        Set<Role> roles = new HashSet<>();
        for (Role role : userForm.getRoles()) {
            Role dbRole = roleRepository.findById(role.getId()).orElse(null);
            if (dbRole != null) {
                roles.add(dbRole);
            }
        }

        if (userForm.getPassword() != null && !userForm.getPassword().isEmpty()) {
            userForm.setPassword(bCryptPasswordEncoder.encode(userForm.getPassword()));
        }
        userForm.setRoles(roles);
        userRepository.save(userForm);
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
