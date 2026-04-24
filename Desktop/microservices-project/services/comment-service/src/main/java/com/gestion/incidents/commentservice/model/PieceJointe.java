package com.gestion.incidents.commentservice.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pieces_jointes")
public class PieceJointe {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentaire_id", nullable = false)
    private Commentaire commentaire;

    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

    @Column(name = "type_contenu")
    private String typeContenu;

    @Column(name = "taille_octets")
    private Long tailleOctets;

    @Column(name = "minio_bucket")
    private String minioBucket;

    @Column(name = "minio_objet_key")
    private String minioObjetKey;

    @Column(name = "url_telechargement")
    private String urlTelechargement;

    @CreationTimestamp
    @Column(name = "date_upload", updatable = false)
    private LocalDateTime dateUpload;

    public PieceJointe() {}
    public UUID getId() { return id; }
    public Commentaire getCommentaire() { return commentaire; }
    public String getNomFichier() { return nomFichier; }
    public String getTypeContenu() { return typeContenu; }
    public Long getTailleOctets() { return tailleOctets; }
    public String getMinioBucket() { return minioBucket; }
    public String getMinioObjetKey() { return minioObjetKey; }
    public String getUrlTelechargement() { return urlTelechargement; }
    public LocalDateTime getDateUpload() { return dateUpload; }
    public void setId(UUID id) { this.id = id; }
    public void setCommentaire(Commentaire c) { this.commentaire = c; }
    public void setNomFichier(String v) { this.nomFichier = v; }
    public void setTypeContenu(String v) { this.typeContenu = v; }
    public void setTailleOctets(Long v) { this.tailleOctets = v; }
    public void setMinioBucket(String v) { this.minioBucket = v; }
    public void setMinioObjetKey(String v) { this.minioObjetKey = v; }
    public void setUrlTelechargement(String v) { this.urlTelechargement = v; }
    public void setDateUpload(LocalDateTime v) { this.dateUpload = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final PieceJointe o = new PieceJointe();
        public Builder commentaire(Commentaire v) { o.commentaire = v; return this; }
        public Builder nomFichier(String v) { o.nomFichier = v; return this; }
        public Builder typeContenu(String v) { o.typeContenu = v; return this; }
        public Builder tailleOctets(Long v) { o.tailleOctets = v; return this; }
        public Builder minioBucket(String v) { o.minioBucket = v; return this; }
        public Builder minioObjetKey(String v) { o.minioObjetKey = v; return this; }
        public Builder urlTelechargement(String v) { o.urlTelechargement = v; return this; }
        public PieceJointe build() { return o; }
    }
}
