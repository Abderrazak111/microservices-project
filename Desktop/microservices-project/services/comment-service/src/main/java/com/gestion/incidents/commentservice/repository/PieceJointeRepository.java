package com.gestion.incidents.commentservice.repository;

import com.gestion.incidents.commentservice.model.PieceJointe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface PieceJointeRepository extends JpaRepository<PieceJointe, UUID> {}
