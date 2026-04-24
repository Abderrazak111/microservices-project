package com.gestion.incidents.commentservice.dto;

import com.gestion.incidents.commentservice.model.StatutCommentaire;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CommentaireResponseDTO {
    private UUID id;
    private UUID incidentId;
    private UUID auteurId;
    private String auteurNom;
    private String auteurEmail;
    private String contenu;
    private StatutCommentaire statut;
    private UUID commentaireParentId;
    private List<CommentaireResponseDTO> reponses;
    private List<PieceJointeResponseDTO> piecesJointes;
    private Boolean estSolution;
    private Boolean estInterne;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    public CommentaireResponseDTO() {}
    public UUID getId() { return id; }
    public UUID getIncidentId() { return incidentId; }
    public UUID getAuteurId() { return auteurId; }
    public String getAuteurNom() { return auteurNom; }
    public String getAuteurEmail() { return auteurEmail; }
    public String getContenu() { return contenu; }
    public StatutCommentaire getStatut() { return statut; }
    public UUID getCommentaireParentId() { return commentaireParentId; }
    public List<CommentaireResponseDTO> getReponses() { return reponses; }
    public List<PieceJointeResponseDTO> getPiecesJointes() { return piecesJointes; }
    public Boolean getEstSolution() { return estSolution; }
    public Boolean getEstInterne() { return estInterne; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDateModification() { return dateModification; }
    public void setId(UUID v) { this.id = v; }
    public void setIncidentId(UUID v) { this.incidentId = v; }
    public void setAuteurId(UUID v) { this.auteurId = v; }
    public void setAuteurNom(String v) { this.auteurNom = v; }
    public void setAuteurEmail(String v) { this.auteurEmail = v; }
    public void setContenu(String v) { this.contenu = v; }
    public void setStatut(StatutCommentaire v) { this.statut = v; }
    public void setCommentaireParentId(UUID v) { this.commentaireParentId = v; }
    public void setReponses(List<CommentaireResponseDTO> v) { this.reponses = v; }
    public void setPiecesJointes(List<PieceJointeResponseDTO> v) { this.piecesJointes = v; }
    public void setEstSolution(Boolean v) { this.estSolution = v; }
    public void setEstInterne(Boolean v) { this.estInterne = v; }
    public void setDateCreation(LocalDateTime v) { this.dateCreation = v; }
    public void setDateModification(LocalDateTime v) { this.dateModification = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final CommentaireResponseDTO o = new CommentaireResponseDTO();
        public Builder id(UUID v) { o.id = v; return this; }
        public Builder incidentId(UUID v) { o.incidentId = v; return this; }
        public Builder auteurId(UUID v) { o.auteurId = v; return this; }
        public Builder auteurNom(String v) { o.auteurNom = v; return this; }
        public Builder auteurEmail(String v) { o.auteurEmail = v; return this; }
        public Builder contenu(String v) { o.contenu = v; return this; }
        public Builder statut(StatutCommentaire v) { o.statut = v; return this; }
        public Builder commentaireParentId(UUID v) { o.commentaireParentId = v; return this; }
        public Builder reponses(List<CommentaireResponseDTO> v) { o.reponses = v; return this; }
        public Builder piecesJointes(List<PieceJointeResponseDTO> v) { o.piecesJointes = v; return this; }
        public Builder estSolution(Boolean v) { o.estSolution = v; return this; }
        public Builder estInterne(Boolean v) { o.estInterne = v; return this; }
        public Builder dateCreation(LocalDateTime v) { o.dateCreation = v; return this; }
        public Builder dateModification(LocalDateTime v) { o.dateModification = v; return this; }
        public CommentaireResponseDTO build() { return o; }
    }
}
