package com.gestion.incidents.commentservice.repository;

import com.gestion.incidents.commentservice.model.Commentaire;
import com.gestion.incidents.commentservice.model.StatutCommentaire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, UUID> {

    Page<Commentaire> findByIncidentIdAndCommentaireParentIdIsNullAndStatut(
        UUID incidentId, StatutCommentaire statut, Pageable pageable);

    long countByIncidentIdAndStatut(UUID incidentId, StatutCommentaire statut);

    List<Commentaire> findByCommentaireParentIdAndStatut(UUID parentId, StatutCommentaire statut);

    @Query("SELECT c FROM Commentaire c WHERE c.incidentId = :id AND c.estSolution = true AND c.statut = 'ACTIF'")
    List<Commentaire> findSolutionsByIncidentId(@Param("id") UUID incidentId);
}
