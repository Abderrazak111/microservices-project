-- ================================================================
-- Initialisation base de données incidents_db
-- ================================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Table: statuts
CREATE TABLE IF NOT EXISTS statuts (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(50)  NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL
);

-- Données initiales des statuts
INSERT INTO statuts (code, libelle) VALUES
    ('NOUVEAU',  'Nouveau'),
    ('ASSIGNE',  'Assigné'),
    ('EN_COURS', 'En cours'),
    ('RESOLU',   'Résolu'),
    ('FERME',    'Fermé')
ON CONFLICT (code) DO NOTHING;

-- Table: incidents
CREATE TABLE IF NOT EXISTS incidents (
    id          BIGSERIAL PRIMARY KEY,
    titre       VARCHAR(255) NOT NULL,
    description TEXT,
    statut_id   BIGINT REFERENCES statuts(id),
    priorite    VARCHAR(20) CHECK (priorite IN ('BASSE', 'NORMALE', 'HAUTE', 'CRITIQUE')),
    capture_url VARCHAR(500),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index
CREATE INDEX IF NOT EXISTS idx_incidents_statut    ON incidents(statut_id);
CREATE INDEX IF NOT EXISTS idx_incidents_priorite  ON incidents(priorite);
CREATE INDEX IF NOT EXISTS idx_incidents_created   ON incidents(created_at);
