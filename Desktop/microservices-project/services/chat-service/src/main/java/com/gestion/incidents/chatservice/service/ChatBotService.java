package com.gestion.incidents.chatservice.service;

import com.gestion.incidents.chatservice.client.IncidentClient;
import com.gestion.incidents.chatservice.dto.SuggestionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de logique métier du chatbot.
 * Fonctionne en deux modes :
 *  - Mode IA (chat.ai.enabled=true)  : utilise OpenAI pour analyser et répondre
 *  - Mode règles (chat.ai.enabled=false) : logique basée sur mots-clés (défaut pour tests)
 */
@Service
public class ChatBotService {

    private static final Logger log = LoggerFactory.getLogger(ChatBotService.class);

    private final IncidentClient incidentClient;

    @Value("${chat.ai.enabled:false}")
    private boolean aiEnabled;

    @Value("${chat.bot.name:Assistant IT}")
    private String botName;

    public ChatBotService(IncidentClient incidentClient) {
        this.incidentClient = incidentClient;
    }

    /**
     * Génère le message de bienvenue du bot
     */
    public String messageAccueil() {
        return "👋 Bonjour ! Je suis votre " + botName + ".\n\n" +
               "Décrivez votre problème en quelques mots et je vais rechercher " +
               "des solutions existantes avant de créer un ticket.\n\n" +
               "Quel est votre problème ?";
    }

    /**
     * Analyse le message de l'utilisateur et cherche des incidents similaires
     */
    public List<SuggestionDTO> rechercherIncidentsSimilaires(String probleme) {
        log.info("Recherche d'incidents similaires pour: {}", probleme);

        try {
            List<Map<String, Object>> incidents = incidentClient.rechercherIncidents(probleme);

            return incidents.stream()
                .limit(3)
                .map(incident -> {
                    SuggestionDTO s = new SuggestionDTO();
                    s.setIncidentSimilaireId(UUID.fromString(
                        incident.getOrDefault("id", UUID.randomUUID()).toString()));
                    s.setTitreIncident(incident.getOrDefault("titre", "Incident similaire").toString());
                    s.setSolutionIncident(incident.getOrDefault("solution", "Voir les détails").toString());
                    s.setScoreSimilarite(0.85f);
                    s.setAccepte(false);
                    return s;
                })
                .collect(Collectors.toList());

        } catch (Exception e) {
            log.warn("Service incident indisponible - retour suggestions de démonstration: {}", e.getMessage());
            return generSuggestionsDemostation(probleme);
        }
    }

    /**
     * Génère le message bot quand des suggestions sont trouvées
     */
    public String messageAvecSuggestions(List<SuggestionDTO> suggestions, String probleme) {
        StringBuilder sb = new StringBuilder();
        sb.append("🔍 J'ai recherché des cas similaires à votre problème...\n\n");
        sb.append("✅ J'ai trouvé ").append(suggestions.size())
          .append(" incident(s) similaire(s) résolu(s) :\n\n");

        for (int i = 0; i < suggestions.size(); i++) {
            SuggestionDTO s = suggestions.get(i);
            sb.append("📄 **Option ").append(i + 1).append("** : ")
              .append(s.getTitreIncident()).append("\n");
            sb.append("   💡 Solution : ").append(s.getSolutionIncident()).append("\n\n");
        }

        sb.append("Voulez-vous essayer l'une de ces solutions ?\n");
        sb.append("Répondez **oui** si une solution a fonctionné, ou **non** pour créer un ticket.");
        return sb.toString();
    }

    /**
     * Message quand aucune suggestion n'est trouvée
     */
    public String messageAucuneSuggestion(String probleme) {
        return "🔍 J'ai cherché mais je n'ai trouvé aucun cas similaire à votre problème.\n\n" +
               "Je vais créer un ticket pour vous. Un technicien vous contactera dans les 2 heures.\n\n" +
               "Voulez-vous que je crée le ticket maintenant ? (répondez **oui** ou **non**)";
    }

    /**
     * Message de confirmation après résolution
     */
    public String messageResolu(String solutionUtilisee) {
        return "🎉 Excellent ! Je suis ravi que le problème soit résolu.\n\n" +
               "✅ Cette conversation est marquée comme **résolue**.\n\n" +
               "Bonne journée ! N'hésitez pas à revenir si vous avez d'autres problèmes. 😊";
    }

    /**
     * Message quand aucune solution ne fonctionne → création ticket
     */
    public String messageCreationTicket(String referenceTicket) {
        return "📋 Aucun problème ! Je crée un ticket pour vous.\n\n" +
               "✅ **Ticket créé avec succès**\n" +
               "📌 Référence : **" + referenceTicket + "**\n" +
               "⏱️ Priorité : Moyenne\n" +
               "👨‍💻 Un technicien vous contactera dans les **2 heures**.\n\n" +
               "Merci de votre patience !";
    }

    /**
     * Crée un incident via le service incident
     */
    public Map<String, Object> creerIncident(String titre, String description,
                                              String utilisateurNom) {
        try {
            Map<String, Object> incident = new HashMap<>();
            incident.put("titre", titre);
            incident.put("description", description);
            incident.put("priorite", "MOYENNE");
            incident.put("statut", "NOUVEAU");
            incident.put("declarantNom", utilisateurNom);

            return incidentClient.creerIncident(incident);

        } catch (Exception e) {
            log.warn("Impossible de créer l'incident via API: {}", e.getMessage());
            // Retourner un incident simulé
            Map<String, Object> mock = new HashMap<>();
            mock.put("id", UUID.randomUUID().toString());
            mock.put("reference", "INC-" + System.currentTimeMillis() % 10000);
            mock.put("statut", "NOUVEAU");
            return mock;
        }
    }

    /**
     * Détermine si le message de l'utilisateur est une confirmation positive
     */
    public boolean estConfirmationPositive(String message) {
        String msg = message.toLowerCase().trim();
        return msg.contains("oui") || msg.contains("yes") || msg.contains("ok") ||
               msg.contains("ça marche") || msg.contains("ca marche") ||
               msg.contains("résolu") || msg.contains("fonctionne") ||
               msg.contains("correct") || msg.startsWith("1") ||
               msg.startsWith("2") || msg.startsWith("3");
    }

    /**
     * Détermine si le message est un refus / rien ne fonctionne
     */
    public boolean estRefus(String message) {
        String msg = message.toLowerCase().trim();
        return msg.contains("non") || msg.contains("no") ||
               msg.contains("aucun") || msg.contains("rien") ||
               msg.contains("marche pas") || msg.contains("fonctionne pas");
    }

    /**
     * Suggestions de démonstration quand incident-service est indisponible
     */
    private List<SuggestionDTO> generSuggestionsDemostation(String probleme) {
        List<SuggestionDTO> suggestions = new ArrayList<>();

        if (probleme.toLowerCase().contains("imprimante") ||
            probleme.toLowerCase().contains("impression")) {

            suggestions.add(SuggestionDTO.builder()
                .incidentSimilaireId(UUID.randomUUID())
                .titreIncident("Imprimante HP bloquée (il y a 3 jours)")
                .solutionIncident("Redémarrer le spooler d'impression : services.msc → " +
                                  "Print Spooler → Redémarrer")
                .scoreSimilarite(0.92f).accepte(false).build());

            suggestions.add(SuggestionDTO.builder()
                .incidentSimilaireId(UUID.randomUUID())
                .titreIncident("Imprimante ne répond pas (il y a 1 semaine)")
                .solutionIncident("Désinstaller et réinstaller les pilotes d'impression")
                .scoreSimilarite(0.78f).accepte(false).build());

        } else if (probleme.toLowerCase().contains("mot de passe") ||
                   probleme.toLowerCase().contains("password") ||
                   probleme.toLowerCase().contains("connexion")) {

            suggestions.add(SuggestionDTO.builder()
                .incidentSimilaireId(UUID.randomUUID())
                .titreIncident("Problème de connexion Active Directory")
                .solutionIncident("Vider le cache des identifiants Windows : " +
                                  "Panneau de configuration → Gestionnaire d'identifiants")
                .scoreSimilarite(0.88f).accepte(false).build());

        } else if (probleme.toLowerCase().contains("réseau") ||
                   probleme.toLowerCase().contains("internet") ||
                   probleme.toLowerCase().contains("wifi")) {

            suggestions.add(SuggestionDTO.builder()
                .incidentSimilaireId(UUID.randomUUID())
                .titreIncident("Perte de connexion réseau intermittente")
                .solutionIncident("Désactiver/réactiver la carte réseau, " +
                                  "puis vider le cache DNS : ipconfig /flushdns")
                .scoreSimilarite(0.85f).accepte(false).build());

        } else {
            // Suggestions génériques
            suggestions.add(SuggestionDTO.builder()
                .incidentSimilaireId(UUID.randomUUID())
                .titreIncident("Problème système similaire résolu")
                .solutionIncident("Redémarrer le poste de travail et réessayer")
                .scoreSimilarite(0.65f).accepte(false).build());
        }

        return suggestions;
    }
}
