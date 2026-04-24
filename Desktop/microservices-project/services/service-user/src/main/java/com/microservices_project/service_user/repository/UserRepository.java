package com.microservices_project.service_user.repository;

import com.microservices_project.service_user.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<Users, UUID> {

    Optional<Users> findByEmail(String email);
    Optional<Users> findByKeycloakId(UUID keycloakId);
}