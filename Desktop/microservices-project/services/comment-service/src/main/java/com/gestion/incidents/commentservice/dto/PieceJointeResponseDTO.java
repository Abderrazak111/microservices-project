package com.gestion.incidents.commentservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class PieceJointeResponseDTO {
    private UUID id;
    private String nomFichier;
    private String typeContenu;
    private Long tailleOctets;
    private String urlTelechargement;
    private LocalDateTime dateUpload;

    public PieceJointeResponseDTO() {}
    public UUID getId() { return id; }
    public String getNomFichier() { return nomFichier; }
    public String getTypeContenu() { return typeContenu; }
    public Long getTailleOctets() { return tailleOctets; }
    public String getUrlTelechargement() { return urlTelechargement; }
    public LocalDateTime getDateUpload() { return dateUpload; }
    public void setId(UUID v) { this.id = v; }
    public void setNomFichier(String v) { this.nomFichier = v; }
    public void setTypeContenu(String v) { this.typeContenu = v; }
    public void setTailleOctets(Long v) { this.tailleOctets = v; }
    public void setUrlTelechargement(String v) { this.urlTelechargement = v; }
    public void setDateUpload(LocalDateTime v) { this.dateUpload = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final PieceJointeResponseDTO o = new PieceJointeResponseDTO();
        public Builder id(UUID v) { o.id = v; return this; }
        public Builder nomFichier(String v) { o.nomFichier = v; return this; }
        public Builder typeContenu(String v) { o.typeContenu = v; return this; }
        public Builder tailleOctets(Long v) { o.tailleOctets = v; return this; }
        public Builder urlTelechargement(String v) { o.urlTelechargement = v; return this; }
        public Builder dateUpload(LocalDateTime v) { o.dateUpload = v; return this; }
        public PieceJointeResponseDTO build() { return o; }
    }
}
