-- ================================================================
-- Initialisation base de données service_user_db
-- ================================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Table: users
CREATE TABLE IF NOT EXISTS users (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    keycloak_id         UUID NOT NULL UNIQUE,
    nom                 VARCHAR(100),
    prenom              VARCHAR(100),
    email               VARCHAR(255) NOT NULL UNIQUE,
    avatar_url          VARCHAR(500),
    departement         VARCHAR(100),
    poste               VARCHAR(100),
    date_creation       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    derniere_connexion  TIMESTAMP
);

-- Index
CREATE INDEX IF NOT EXISTS idx_users_email       ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_keycloak_id ON users(keycloak_id);
