package com.gestion.incidents.chatservice.service;

import com.gestion.incidents.chatservice.dto.*;
import com.gestion.incidents.chatservice.exception.ConversationNotFoundException;
import com.gestion.incidents.chatservice.model.*;
import com.gestion.incidents.chatservice.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConversationService {

    private static final Logger log = LoggerFactory.getLogger(ConversationService.class);

    private final ConversationRepository conversationRepo;
    private final MessageRepository messageRepo;
    private final SuggestionRepository suggestionRepo;
    private final ChatBotService botService;

    public ConversationService(ConversationRepository conversationRepo,
                                MessageRepository messageRepo,
                                SuggestionRepository suggestionRepo,
                                ChatBotService botService) {
        this.conversationRepo = conversationRepo;
        this.messageRepo = messageRepo;
        this.suggestionRepo = suggestionRepo;
        this.botService = botService;
    }

    // ── Démarrer une nouvelle conversation ────────────────────────
    public MessageResponseDTO demarrerConversation(NouvelleConversationDTO dto) {
        // Créer la conversation
        UUID userId = dto.getUtilisateurId() != null
            ? dto.getUtilisateurId()
            : UUID.fromString("00000000-0000-0000-0000-000000000001");

        ChatConversation conv = ChatConversation.builder()
            .utilisateurId(userId)
            .utilisateurNom(dto.getUtilisateurNom() != null ? dto.getUtilisateurNom() : "Utilisateur")
            .statut(StatutConversation.EN_COURS)
            .problemeResume(dto.getMessageInitial())
            .build();

        conv = conversationRepo.save(conv);
        log.info("Nouvelle conversation créée - ID: {}", conv.getId());

        // Sauvegarder le message utilisateur
        sauvegarderMessage(conv, Expediteur.USER, dto.getMessageInitial());

        // Chercher des incidents similaires
        List<SuggestionDTO> suggestions =
            botService.rechercherIncidentsSimilaires(dto.getMessageInitial());

        String reponseBotTexte;
        String action;

        if (!suggestions.isEmpty()) {
            // Sauvegarder les suggestions en base
            final ChatConversation convFinal = conv;
            suggestions.forEach(s -> {
                Suggestion suggestion = Suggestion.builder()
                    .conversation(convFinal)
                    .incidentSimilaireId(s.getIncidentSimilaireId())
                    .titreIncident(s.getTitreIncident())
                    .solutionIncident(s.getSolutionIncident())
                    .scoreSimilarite(s.getScoreSimilarite())
                    .accepte(false)
                    .build();
                Suggestion saved = suggestionRepo.save(suggestion);
                s.setId(saved.getId());
            });

            reponseBotTexte = botService.messageAvecSuggestions(suggestions, dto.getMessageInitial());
            action = "FEEDBACK_SOLUTION";
        } else {
            reponseBotTexte = botService.messageAucuneSuggestion(dto.getMessageInitial());
            action = "CREER_INCIDENT";
        }

        // Sauvegarder le message bot
        ChatMessage msgBot = sauvegarderMessage(conv, Expediteur.BOT, reponseBotTexte);

        return MessageResponseDTO.builder()
            .id(msgBot.getId())
            .conversationId(conv.getId())
            .expediteur(Expediteur.BOT)
            .message(reponseBotTexte)
            .timestamp(msgBot.getTimestamp())
            .suggestions(suggestions)
            .actionRequise(action)
            .build();
    }

    // ── Envoyer un message dans une conversation existante ────────
    public MessageResponseDTO envoyerMessage(MessageDTO dto) {
        ChatConversation conv = conversationRepo.findById(dto.getConversationId())
            .orElseThrow(() -> new ConversationNotFoundException(
                "Conversation introuvable: " + dto.getConversationId()));

        if (conv.getStatut() != StatutConversation.EN_COURS) {
            throw new IllegalStateException("Cette conversation est terminée (statut: " + conv.getStatut() + ")");
        }

        // Sauvegarder le message utilisateur
        sauvegarderMessage(conv, Expediteur.USER, dto.getMessage());

        String reponseBotTexte;
        String action;
        List<SuggestionDTO> suggestions = new ArrayList<>();

        // Vérifier le contexte: y a-t-il des suggestions en attente ?
        List<Suggestion> suggestionsEnAttente = suggestionRepo.findByConversationId(conv.getId())
            .stream().filter(s -> !s.getAccepte()).collect(Collectors.toList());

        if (!suggestionsEnAttente.isEmpty()) {
            // L'utilisateur répond à des suggestions
            if (botService.estConfirmationPositive(dto.getMessage())) {
                // Solution acceptée → marquer comme résolu
                conv.setStatut(StatutConversation.RESOLU);
                conversationRepo.save(conv);
                reponseBotTexte = botService.messageResolu(dto.getMessage());
                action = "RESOLU";
            } else if (botService.estRefus(dto.getMessage())) {
                // Aucune solution → créer un ticket
                Map<String, Object> incident = botService.creerIncident(
                    conv.getProblemeResume(),
                    "Problème signalé par chat: " + conv.getProblemeResume(),
                    conv.getUtilisateurNom()
                );
                String ref = incident.getOrDefault("reference",
                    "INC-" + System.currentTimeMillis() % 100000).toString();
                UUID incidentId = incident.containsKey("id")
                    ? UUID.fromString(incident.get("id").toString()) : UUID.randomUUID();

                conv.setStatut(StatutConversation.INCIDENT_CREE);
                conv.setIncidentCreerId(incidentId);
                conversationRepo.save(conv);

                reponseBotTexte = botService.messageCreationTicket(ref);
                action = "INCIDENT_CREE";
            } else {
                // Réponse ambiguë → redemander
                reponseBotTexte = "Je n'ai pas bien compris. Répondez **oui** si une solution " +
                    "a fonctionné, ou **non** pour que je crée un ticket.";
                action = "FEEDBACK_SOLUTION";
            }
        } else {
            // Pas de suggestions en attente → nouveau problème ou confirmation ticket
            if (botService.estConfirmationPositive(dto.getMessage())) {
                Map<String, Object> incident = botService.creerIncident(
                    conv.getProblemeResume(),
                    dto.getMessage(),
                    conv.getUtilisateurNom()
                );
                String ref = incident.getOrDefault("reference",
                    "INC-" + System.currentTimeMillis() % 100000).toString();

                conv.setStatut(StatutConversation.INCIDENT_CREE);
                conversationRepo.save(conv);
                reponseBotTexte = botService.messageCreationTicket(ref);
                action = "INCIDENT_CREE";
            } else {
                // Chercher de nouvelles suggestions
                List<SuggestionDTO> nouvelles =
                    botService.rechercherIncidentsSimilaires(dto.getMessage());

                if (!nouvelles.isEmpty()) {
                    final ChatConversation convFinal = conv;
                    nouvelles.forEach(s -> {
                        Suggestion suggestion = Suggestion.builder()
                            .conversation(convFinal)
                            .incidentSimilaireId(s.getIncidentSimilaireId())
                            .titreIncident(s.getTitreIncident())
                            .solutionIncident(s.getSolutionIncident())
                            .scoreSimilarite(s.getScoreSimilarite())
                            .accepte(false)
                            .build();
                        Suggestion saved = suggestionRepo.save(suggestion);
                        s.setId(saved.getId());
                    });
                    suggestions = nouvelles;
                    reponseBotTexte = botService.messageAvecSuggestions(nouvelles, dto.getMessage());
                    action = "FEEDBACK_SOLUTION";
                } else {
                    reponseBotTexte = botService.messageAucuneSuggestion(dto.getMessage());
                    action = "CREER_INCIDENT";
                }
            }
        }

        ChatMessage msgBot = sauvegarderMessage(conv, Expediteur.BOT, reponseBotTexte);

        return MessageResponseDTO.builder()
            .id(msgBot.getId())
            .conversationId(conv.getId())
            .expediteur(Expediteur.BOT)
            .message(reponseBotTexte)
            .timestamp(msgBot.getTimestamp())
            .suggestions(suggestions)
            .actionRequise(action)
            .build();
    }

    // ── Obtenir une conversation avec son historique ───────────────
    @Transactional(readOnly = true)
    public ConversationDTO obtenirConversation(UUID conversationId) {
        ChatConversation conv = conversationRepo.findById(conversationId)
            .orElseThrow(() -> new ConversationNotFoundException(
                "Conversation introuvable: " + conversationId));

        List<MessageResponseDTO> messages = conv.getMessages().stream()
            .map(m -> MessageResponseDTO.builder()
                .id(m.getId())
                .conversationId(conv.getId())
                .expediteur(m.getExpediteur())
                .message(m.getMessage())
                .timestamp(m.getTimestamp())
                .build())
            .collect(Collectors.toList());

        return ConversationDTO.builder()
            .id(conv.getId())
            .utilisateurId(conv.getUtilisateurId())
            .utilisateurNom(conv.getUtilisateurNom())
            .dateCreation(conv.getDateCreation())
            .statut(conv.getStatut())
            .problemeResume(conv.getProblemeResume())
            .incidentCreerId(conv.getIncidentCreerId())
            .messages(messages)
            .build();
    }

    // ── Lister les conversations d'un utilisateur ──────────────────
    @Transactional(readOnly = true)
    public List<ConversationDTO> listerConversations(UUID utilisateurId) {
        return conversationRepo
            .findByUtilisateurIdOrderByDateCreationDesc(utilisateurId)
            .stream()
            .map(conv -> ConversationDTO.builder()
                .id(conv.getId())
                .utilisateurId(conv.getUtilisateurId())
                .utilisateurNom(conv.getUtilisateurNom())
                .dateCreation(conv.getDateCreation())
                .statut(conv.getStatut())
                .problemeResume(conv.getProblemeResume())
                .incidentCreerId(conv.getIncidentCreerId())
                .build())
            .collect(Collectors.toList());
    }

    // ── Helper: sauvegarder un message ────────────────────────────
    private ChatMessage sauvegarderMessage(ChatConversation conv,
                                            Expediteur expediteur,
                                            String texte) {
        ChatMessage msg = ChatMessage.builder()
            .conversation(conv)
            .expediteur(expediteur)
            .message(texte)
            .build();
        return messageRepo.save(msg);
    }
}
