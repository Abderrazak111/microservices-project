-- ================================================================
-- Initialisation base de données comment_db
-- ================================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Table: commentaires
CREATE TABLE IF NOT EXISTS commentaires (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    incident_id     BIGINT      NOT NULL,
    auteur_id       UUID        NOT NULL,
    auteur_nom      VARCHAR(255),
    contenu         TEXT        NOT NULL,
    statut          VARCHAR(30) NOT NULL DEFAULT 'ACTIF'
                        CHECK (statut IN ('ACTIF', 'MODIFIE', 'SUPPRIME')),
    date_creation   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP
);

-- Table: pieces_jointes
CREATE TABLE IF NOT EXISTS pieces_jointes (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    commentaire_id  UUID        NOT NULL REFERENCES commentaires(id) ON DELETE CASCADE,
    nom_fichier     VARCHAR(255) NOT NULL,
    type_contenu    VARCHAR(100),
    taille          BIGINT,
    minio_object_key VARCHAR(500) NOT NULL,
    url_acces       VARCHAR(500),
    date_upload     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index
CREATE INDEX IF NOT EXISTS idx_commentaires_incident    ON commentaires(incident_id);
CREATE INDEX IF NOT EXISTS idx_commentaires_auteur      ON commentaires(auteur_id);
CREATE INDEX IF NOT EXISTS idx_pieces_jointes_comment   ON pieces_jointes(commentaire_id);
