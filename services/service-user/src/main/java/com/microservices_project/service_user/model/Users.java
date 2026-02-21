package com.microservices_project.service_user.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "keycloak_id", nullable = false, unique = true)
    private UUID keycloakId;

    private String nom;
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    private String avatarUrl;
    private String departement;
    private String poste;

    private LocalDateTime dateCreation;
    private LocalDateTime derniereConnexion;

    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
    }
}