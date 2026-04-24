package com.gestion.incidents.commentservice.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "commentaires", indexes = {
    @Index(name = "idx_incident_id", columnList = "incident_id"),
    @Index(name = "idx_auteur_id", columnList = "auteur_id")
})
public class Commentaire {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "incident_id", nullable = false)
    private UUID incidentId;

    @Column(name = "auteur_id", nullable = false)
    private UUID auteurId;

    @Column(name = "auteur_nom")
    private String auteurNom;

    @Column(name = "auteur_email")
    private String auteurEmail;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCommentaire statut = StatutCommentaire.ACTIF;

    @Column(name = "commentaire_parent_id")
    private UUID commentaireParentId;

    @OneToMany(mappedBy = "commentaire", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PieceJointe> piecesJointes = new ArrayList<>();

    @Column(name = "est_solution")
    private Boolean estSolution = false;

    @Column(name = "est_interne")
    private Boolean estInterne = false;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    public Commentaire() {}
    public UUID getId() { return id; }
    public UUID getIncidentId() { return incidentId; }
    public UUID getAuteurId() { return auteurId; }
    public String getAuteurNom() { return auteurNom; }
    public String getAuteurEmail() { return auteurEmail; }
    public String getContenu() { return contenu; }
    public StatutCommentaire getStatut() { return statut; }
    public UUID getCommentaireParentId() { return commentaireParentId; }
    public List<PieceJointe> getPiecesJointes() { return piecesJointes; }
    public Boolean getEstSolution() { return estSolution; }
    public Boolean getEstInterne() { return estInterne; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDateModification() { return dateModification; }
    public void setId(UUID id) { this.id = id; }
    public void setIncidentId(UUID v) { this.incidentId = v; }
    public void setAuteurId(UUID v) { this.auteurId = v; }
    public void setAuteurNom(String v) { this.auteurNom = v; }
    public void setAuteurEmail(String v) { this.auteurEmail = v; }
    public void setContenu(String v) { this.contenu = v; }
    public void setStatut(StatutCommentaire v) { this.statut = v; }
    public void setCommentaireParentId(UUID v) { this.commentaireParentId = v; }
    public void setPiecesJointes(List<PieceJointe> v) { this.piecesJointes = v; }
    public void setEstSolution(Boolean v) { this.estSolution = v; }
    public void setEstInterne(Boolean v) { this.estInterne = v; }
    public void setDateCreation(LocalDateTime v) { this.dateCreation = v; }
    public void setDateModification(LocalDateTime v) { this.dateModification = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Commentaire o = new Commentaire();
        public Builder incidentId(UUID v) { o.incidentId = v; return this; }
        public Builder auteurId(UUID v) { o.auteurId = v; return this; }
        public Builder auteurNom(String v) { o.auteurNom = v; return this; }
        public Builder auteurEmail(String v) { o.auteurEmail = v; return this; }
        public Builder contenu(String v) { o.contenu = v; return this; }
        public Builder statut(StatutCommentaire v) { o.statut = v; return this; }
        public Builder commentaireParentId(UUID v) { o.commentaireParentId = v; return this; }
        public Builder estSolution(Boolean v) { o.estSolution = v; return this; }
        public Builder estInterne(Boolean v) { o.estInterne = v; return this; }
        public Commentaire build() { return o; }
    }
}
