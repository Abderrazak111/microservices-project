package com.microservices_project.service_incident.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "incidents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;

    @ManyToOne
    @JoinColumn(name = "statut_id")
    private Statut statut;   // relation vers la classe Statut

    private String priorite;
    private String captureUrl;
}
