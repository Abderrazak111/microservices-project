# Microservices — Gestion des Incidents

Plateforme de gestion d'incidents IT construite en architecture microservices avec Spring Boot, sécurisée par Keycloak et utilisant MinIO pour le stockage des captures d'écran.

---

## Architecture

```
                          ┌─────────────┐
                          │  Keycloak   │  :8080  Identity Provider
                          └──────┬──────┘
                                 │  JWT Tokens
                    ┌────────────▼────────────┐
                    │     API Gateway         │  :8880
                    │   (JWT Validation)      │
                    └────┬──────┬──────┬──────┘
                         │      │      │
          ┌──────────────┘      │      └──────────────────┐
          │                     │                          │
   ┌──────▼──────┐    ┌─────────▼──────┐    ┌────────────▼──────┐
   │  service-   │    │  service-user  │    │  comment-service  │
   │  incident   │    │    :8081       │    │      :8083        │
   │   :8082     │    │  (user-db)     │    │  (comment-db)     │
   │(incident-db)│    └────────────────┘    │  (MinIO uploads)  │
   │(MinIO shots)│                          └───────────────────┘
   └──────┬──────┘
          │
   ┌──────▼──────┐
   │ chat-service│  :8085  (WebSocket + Chatbot)
   │ (chat-db)   │
   └─────────────┘

Infrastructure:
  Eureka      :8761   Service Discovery
  Config Srv  :8888   Centralized Configuration
  MinIO       :9000   Object Storage
  MinIO UI    :9001   MinIO Web Console
```

---

## Services & Ports

| Service          | Port | Description                            |
|-----------------|------|----------------------------------------|
| API Gateway     | 8880 | Point d'entrée unique, validation JWT  |
| service-incident| 8082 | CRUD incidents + upload captures MinIO |
| service-user    | 8081 | Gestion utilisateurs                   |
| comment-service | 8083 | Commentaires + pièces jointes          |
| chat-service    | 8085 | Chatbot IT via WebSocket               |
| Eureka          | 8761 | Service Discovery                      |
| Config Server   | 8888 | Configuration centralisée              |
| Keycloak        | 8080 | IAM — Auth / Autorisation              |
| MinIO API       | 9000 | Stockage objets S3                     |
| MinIO Console   | 9001 | Interface web MinIO                    |

---

## Bases de données PostgreSQL

| Service          | Port hôte | Base de données   | User     | Password |
|-----------------|-----------|-------------------|----------|----------|
| service-incident| 5432      | incidents_db      | postgres | ABDO0505 |
| service-user    | 5433      | service_user_db   | postgres | ABDO0505 |
| comment-service | 5434      | comment_db        | postgres | postgres |
| chat-service    | 5435      | chat_db           | postgres | postgres |

---

## Démarrage rapide

### Prérequis
- Docker Desktop >= 24
- Docker Compose >= 2.20
- 8 Go de RAM recommandé

### Lancer toute la plateforme

```bash
docker-compose up -d

# Suivre les logs
docker-compose logs -f

# Etat des services
docker-compose ps
```

### Arrêt

```bash
docker-compose down          # sans supprimer les volumes
docker-compose down -v       # reset complet
```

---

## Keycloak — Authentification

- **URL** : http://localhost:8080
- **Admin** : admin / admin
- **Realm** : incidents-realm

### Utilisateurs préconfigurés

| Username    | Password | Rôles           |
|------------|---------|-----------------|
| admin      | admin123 | ADMIN, USER     |
| technicien1| tech123  | TECHNICIEN, USER|
| user1      | user123  | USER            |

### Obtenir un token JWT

```bash
TOKEN=$(curl -s -X POST \
  "http://localhost:8080/realms/incidents-realm/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=incidents-gateway&client_secret=incidents-gateway-secret" \
  -d "username=admin&password=admin123&grant_type=password" \
  | jq -r '.access_token')
```

### Exemples d'appels API

```bash
# Lister les incidents
curl -H "Authorization: Bearer $TOKEN" http://localhost:8880/api/incidents

# Créer un incident
curl -X POST -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"titre":"Panne réseau","description":"Coupure","priorite":"HAUTE"}' \
  http://localhost:8880/api/incidents

# Uploader une capture d'écran
curl -X POST -H "Authorization: Bearer $TOKEN" \
  -F "file=@capture.png" \
  http://localhost:8880/api/incidents/1/capture
```

---

## MinIO — Stockage

- **Console** : http://localhost:9001  (minioadmin / minioadmin)
- **Buckets** : `incidents`, `commentaires-pieces-jointes`

---

## Structure du projet

```
microservices-project/
├── docker-compose.yml
├── .env.example
├── README.md
├── keycloak/
│   └── incidents-realm.json
├── config-repo/
│   ├── gateway-service.properties
│   ├── service-incident.properties
│   ├── service-user.properties
│   ├── comment-service.properties
│   └── chat-service.properties
└── services/
    ├── eureka-service/
    ├── config-server/
    ├── gateway_service/
    ├── service-incident/
    ├── service-user/
    ├── comment-service/
    └── chat-service/
```
