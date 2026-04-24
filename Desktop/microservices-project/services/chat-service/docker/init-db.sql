-- ================================================================
-- Initialisation base de données chat_db
-- ================================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Table: chat_conversations
CREATE TABLE IF NOT EXISTS chat_conversations (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    utilisateur_id      UUID NOT NULL,
    utilisateur_nom     VARCHAR(255),
    date_creation       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    statut              VARCHAR(30) NOT NULL DEFAULT 'EN_COURS'
                            CHECK (statut IN ('EN_COURS', 'RESOLU', 'INCIDENT_CREE')),
    probleme_resume     TEXT,
    incident_cree_id    UUID
);

-- Table: chat_messages
CREATE TABLE IF NOT EXISTS chat_messages (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    conversation_id     UUID NOT NULL REFERENCES chat_conversations(id) ON DELETE CASCADE,
    expediteur          VARCHAR(10) NOT NULL CHECK (expediteur IN ('USER', 'BOT')),
    message             TEXT NOT NULL,
    timestamp           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: suggestions
CREATE TABLE IF NOT EXISTS suggestions (
    id                      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    conversation_id         UUID NOT NULL REFERENCES chat_conversations(id) ON DELETE CASCADE,
    incident_similaire_id   UUID,
    titre_incident          VARCHAR(500),
    solution_incident       TEXT,
    score_similarite        FLOAT,
    accepte                 BOOLEAN DEFAULT FALSE
);

-- Index
CREATE INDEX IF NOT EXISTS idx_conversations_utilisateur ON chat_conversations(utilisateur_id);
CREATE INDEX IF NOT EXISTS idx_messages_conversation    ON chat_messages(conversation_id);
CREATE INDEX IF NOT EXISTS idx_suggestions_conversation ON suggestions(conversation_id);
