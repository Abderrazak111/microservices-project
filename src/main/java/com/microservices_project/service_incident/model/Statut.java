package com.microservices_project.service_incident.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "statuts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;    // ex: "NOUVEAU", "ASSIGNE"
    private String libelle; // ex: "Nouveau", "Assigné"
}
