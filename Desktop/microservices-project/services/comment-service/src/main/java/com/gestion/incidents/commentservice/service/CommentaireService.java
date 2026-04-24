package com.gestion.incidents.commentservice.service;

import com.gestion.incidents.commentservice.client.IncidentClient;
import com.gestion.incidents.commentservice.dto.*;
import com.gestion.incidents.commentservice.exception.CommentaireNotFoundException;
import com.gestion.incidents.commentservice.model.Commentaire;
import com.gestion.incidents.commentservice.model.PieceJointe;
import com.gestion.incidents.commentservice.model.StatutCommentaire;
import com.gestion.incidents.commentservice.repository.CommentaireRepository;
import com.gestion.incidents.commentservice.repository.PieceJointeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentaireService {

    private static final Logger log = LoggerFactory.getLogger(CommentaireService.class);

    private final CommentaireRepository commentaireRepo;
    private final PieceJointeRepository pieceJointeRepo;
    private final MinioService minioService;
    private final IncidentClient incidentClient;

    public CommentaireService(CommentaireRepository commentaireRepo,
                               PieceJointeRepository pieceJointeRepo,
                               MinioService minioService,
                               IncidentClient incidentClient) {
        this.commentaireRepo = commentaireRepo;
        this.pieceJointeRepo = pieceJointeRepo;
        this.minioService = minioService;
        this.incidentClient = incidentClient;
    }

    // ── Créer ────────────────────────────────────────────────────
    public CommentaireResponseDTO creer(CommentaireRequestDTO dto) {
        // Vérifier parent si fourni
        if (dto.getCommentaireParentId() != null) {
            commentaireRepo.findById(dto.getCommentaireParentId())
                .orElseThrow(() -> new CommentaireNotFoundException(
                    "Commentaire parent introuvable: " + dto.getCommentaireParentId()));
        }

        Commentaire c = Commentaire.builder()
            .incidentId(dto.getIncidentId())
            .auteurId(UUID.fromString("00000000-0000-0000-0000-000000000001")) // userId fixe pour tests
            .auteurNom("Utilisateur Test")
            .auteurEmail("test@example.com")
            .contenu(dto.getContenu())
            .commentaireParentId(dto.getCommentaireParentId())
            .estSolution(dto.getEstSolution() != null ? dto.getEstSolution() : false)
            .estInterne(dto.getEstInterne() != null ? dto.getEstInterne() : false)
            .statut(StatutCommentaire.ACTIF)
            .build();

        Commentaire saved = commentaireRepo.save(c);
        log.info("Commentaire créé - ID: {}", saved.getId());
        return toDTO(saved);
    }

    // ── Lister par incident ───────────────────────────────────────
    @Transactional(readOnly = true)
    public Page<CommentaireResponseDTO> listerParIncident(UUID incidentId, Pageable pageable) {
        return commentaireRepo
            .findByIncidentIdAndCommentaireParentIdIsNullAndStatut(
                incidentId, StatutCommentaire.ACTIF, pageable)
            .map(this::toDTO);
    }

    // ── Obtenir par ID ────────────────────────────────────────────
    @Transactional(readOnly = true)
    public CommentaireResponseDTO obtenirParId(UUID id) {
        return toDTO(commentaireRepo.findById(id)
            .orElseThrow(() -> new CommentaireNotFoundException("Commentaire introuvable: " + id)));
    }

    // ── Modifier ──────────────────────────────────────────────────
    public CommentaireResponseDTO modifier(UUID id, CommentaireMiseAJourDTO dto) {
        Commentaire c = commentaireRepo.findById(id)
            .orElseThrow(() -> new CommentaireNotFoundException("Commentaire introuvable: " + id));
        c.setContenu(dto.getContenu());
        if (dto.getEstSolution() != null) c.setEstSolution(dto.getEstSolution());
        if (dto.getEstInterne() != null) c.setEstInterne(dto.getEstInterne());
        log.info("Commentaire modifié - ID: {}", id);
        return toDTO(commentaireRepo.save(c));
    }

    // ── Supprimer (soft delete) ───────────────────────────────────
    public void supprimer(UUID id) {
        Commentaire c = commentaireRepo.findById(id)
            .orElseThrow(() -> new CommentaireNotFoundException("Commentaire introuvable: " + id));
        c.setStatut(StatutCommentaire.SUPPRIME);
        commentaireRepo.save(c);
        log.info("Commentaire supprimé - ID: {}", id);
    }

    // ── Compter ───────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public long compter(UUID incidentId) {
        return commentaireRepo.countByIncidentIdAndStatut(incidentId, StatutCommentaire.ACTIF);
    }

    // ── Solutions ─────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CommentaireResponseDTO> obtenirSolutions(UUID incidentId) {
        return commentaireRepo.findSolutionsByIncidentId(incidentId)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Pièce jointe ──────────────────────────────────────────────
    public PieceJointeResponseDTO ajouterPieceJointe(UUID commentaireId,
                                                      MultipartFile fichier) throws Exception {
        Commentaire c = commentaireRepo.findById(commentaireId)
            .orElseThrow(() -> new CommentaireNotFoundException("Commentaire introuvable: " + commentaireId));

        String key = minioService.uploadFichier(fichier, commentaireId);
        String url = minioService.genererUrlPresignee(key);

        PieceJointe pj = PieceJointe.builder()
            .commentaire(c)
            .nomFichier(fichier.getOriginalFilename())
            .typeContenu(fichier.getContentType())
            .tailleOctets(fichier.getSize())
            .minioBucket(minioService.getNomBucket())
            .minioObjetKey(key)
            .urlTelechargement(url)
            .build();

        return toPJDTO(pieceJointeRepo.save(pj));
    }

    public void supprimerPieceJointe(UUID pieceJointeId) throws Exception {
        PieceJointe pj = pieceJointeRepo.findById(pieceJointeId)
            .orElseThrow(() -> new CommentaireNotFoundException("Pièce jointe introuvable: " + pieceJointeId));
        minioService.supprimerFichier(pj.getMinioObjetKey());
        pieceJointeRepo.delete(pj);
    }

    // ── Conversions ───────────────────────────────────────────────
    private CommentaireResponseDTO toDTO(Commentaire c) {
        List<CommentaireResponseDTO> reponses = commentaireRepo
            .findByCommentaireParentIdAndStatut(c.getId(), StatutCommentaire.ACTIF)
            .stream().map(this::toDTO).collect(Collectors.toList());

        List<PieceJointeResponseDTO> pj = c.getPiecesJointes()
            .stream().map(this::toPJDTO).collect(Collectors.toList());

        return CommentaireResponseDTO.builder()
            .id(c.getId()).incidentId(c.getIncidentId())
            .auteurId(c.getAuteurId()).auteurNom(c.getAuteurNom()).auteurEmail(c.getAuteurEmail())
            .contenu(c.getContenu()).statut(c.getStatut())
            .commentaireParentId(c.getCommentaireParentId())
            .reponses(reponses).piecesJointes(pj)
            .estSolution(c.getEstSolution()).estInterne(c.getEstInterne())
            .dateCreation(c.getDateCreation()).dateModification(c.getDateModification())
            .build();
    }

    private PieceJointeResponseDTO toPJDTO(PieceJointe pj) {
        return PieceJointeResponseDTO.builder()
            .id(pj.getId()).nomFichier(pj.getNomFichier())
            .typeContenu(pj.getTypeContenu()).tailleOctets(pj.getTailleOctets())
            .urlTelechargement(pj.getUrlTelechargement()).dateUpload(pj.getDateUpload())
            .build();
    }
}
