package com.gestion.incidents.commentservice.controller;

import com.gestion.incidents.commentservice.dto.*;
import com.gestion.incidents.commentservice.service.CommentaireService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/commentaires")
@CrossOrigin(origins = "*")
public class CommentaireController {

    private static final Logger log = LoggerFactory.getLogger(CommentaireController.class);
    private final CommentaireService service;

    public CommentaireController(CommentaireService service) {
        this.service = service;
    }

    /** POST /api/commentaires — Créer un commentaire */
    @PostMapping
    public ResponseEntity<CommentaireResponseDTO> creer(
            @Valid @RequestBody CommentaireRequestDTO dto) {
        log.info("POST /api/commentaires - incidentId={}", dto.getIncidentId());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.creer(dto));
    }

    /** GET /api/commentaires/incident/{id} — Lister les commentaires d'un incident */
    @GetMapping("/incident/{incidentId}")
    public ResponseEntity<Page<CommentaireResponseDTO>> lister(
            @PathVariable UUID incidentId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCreation") String tri,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
            ? Sort.by(tri).descending() : Sort.by(tri).ascending();
        return ResponseEntity.ok(
            service.listerParIncident(incidentId, PageRequest.of(page, size, sort)));
    }

    /** GET /api/commentaires/{id} — Obtenir un commentaire */
    @GetMapping("/{id}")
    public ResponseEntity<CommentaireResponseDTO> obtenir(@PathVariable UUID id) {
        return ResponseEntity.ok(service.obtenirParId(id));
    }

    /** PUT /api/commentaires/{id} — Modifier un commentaire */
    @PutMapping("/{id}")
    public ResponseEntity<CommentaireResponseDTO> modifier(
            @PathVariable UUID id,
            @Valid @RequestBody CommentaireMiseAJourDTO dto) {
        return ResponseEntity.ok(service.modifier(id, dto));
    }

    /** DELETE /api/commentaires/{id} — Supprimer un commentaire */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable UUID id) {
        service.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    /** GET /api/commentaires/incident/{id}/compter */
    @GetMapping("/incident/{incidentId}/compter")
    public ResponseEntity<Long> compter(@PathVariable UUID incidentId) {
        return ResponseEntity.ok(service.compter(incidentId));
    }

    /** GET /api/commentaires/incident/{id}/solutions */
    @GetMapping("/incident/{incidentId}/solutions")
    public ResponseEntity<List<CommentaireResponseDTO>> solutions(@PathVariable UUID incidentId) {
        return ResponseEntity.ok(service.obtenirSolutions(incidentId));
    }

    /** POST /api/commentaires/{id}/pieces-jointes — Upload fichier */
    @PostMapping(value = "/{id}/pieces-jointes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PieceJointeResponseDTO> ajouterFichier(
            @PathVariable UUID id,
            @RequestParam("fichier") MultipartFile fichier) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(service.ajouterPieceJointe(id, fichier));
    }

    /** DELETE /api/commentaires/pieces-jointes/{id} */
    @DeleteMapping("/pieces-jointes/{pieceJointeId}")
    public ResponseEntity<Void> supprimerFichier(@PathVariable UUID pieceJointeId) throws Exception {
        service.supprimerPieceJointe(pieceJointeId);
        return ResponseEntity.noContent().build();
    }
}
