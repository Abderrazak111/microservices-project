package com.gestion.incidents.commentservice.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentaireMiseAJourDTO {
    @NotBlank(message = "Le contenu ne peut pas être vide")
    private String contenu;
    private Boolean estSolution;
    private Boolean estInterne;

    public CommentaireMiseAJourDTO() {}
    public String getContenu() { return contenu; }
    public Boolean getEstSolution() { return estSolution; }
    public Boolean getEstInterne() { return estInterne; }
    public void setContenu(String v) { this.contenu = v; }
    public void setEstSolution(Boolean v) { this.estSolution = v; }
    public void setEstInterne(Boolean v) { this.estInterne = v; }
}
