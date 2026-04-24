package com.microservices_project.service_user.controller;

import com.microservices_project.service_user.model.Users;
import com.microservices_project.service_user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public Users create(@RequestBody Users user) {
        return service.createUser(user);
    }

    @GetMapping
    public List<Users> all() {
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public Users byId(@PathVariable UUID id) {
        return service.getUserById(id);
    }

    @PutMapping("/{id}")
    public Users update(@PathVariable UUID id, @RequestBody Users user) {
        return service.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.deleteUser(id);
    }
}