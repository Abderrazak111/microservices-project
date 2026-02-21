package com.microservices_project.service_user.service;

import com.microservices_project.service_user.model.Users;
import com.microservices_project.service_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public Users createUser(Users user) {
        return repository.save(user);
    }

    public List<Users> getAllUsers() {
        return repository.findAll();
    }

    public Users getUserById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Users updateUser(UUID id, Users updated) {
        Users user = getUserById(id);

        user.setNom(updated.getNom());
        user.setPrenom(updated.getPrenom());
        user.setEmail(updated.getEmail());
        user.setAvatarUrl(updated.getAvatarUrl());
        user.setDepartement(updated.getDepartement());
        user.setPoste(updated.getPoste());

        return repository.save(user);
    }

    public void deleteUser(UUID id) {
        repository.deleteById(id);
    }

    public void updateLastLogin(UUID id) {
        Users user = getUserById(id);
        user.setDerniereConnexion(LocalDateTime.now());
        repository.save(user);
    }
}