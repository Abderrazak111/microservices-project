package com.microservices_project.service_user.service;

import com.microservices_project.service_user.model.Users;
import com.microservices_project.service_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // GET ALL
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    // GET BY ID
    public Users getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // CREATE
    public Users saveUser(Users user) {
        return userRepository.save(user);
    }

    // UPDATE
    public Users updateUser(Long id, Users user) {
        Users existing = getUserById(id); // on utilise getUserById

        existing.setUsername(user.getUsername());
        existing.setEmail(user.getEmail());
        existing.setPassword(user.getPassword());

        return userRepository.save(existing);
    }

    // DELETE
    public void deleteUser(Long id) {
        Users existing = getUserById(id); // vérifie que l'utilisateur existe
        userRepository.delete(existing);
    }
}