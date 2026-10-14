# Base de datos

## Motor

PostgreSQL 16 en Docker (`infra/docker-compose.yml`).

## ER (resumen)

```mermaid
erDiagram
  Finca ||--o{ User : has
  Finca ||--o{ Animal : has
  Finca ||--o{ Potrero : has
  Finca ||--o{ ProductionRecord : has
  Finca ||--o{ PendingTask : has
  Animal ||--o{ ProductionRecord : optional
  Animal ||--o{ AnimalPotrero : history
  Potrero ||--o{ AnimalPotrero : history
```

## Comandos

```bash
docker compose -f infra/docker-compose.yml up -d
cd backend
cp .env.example .env
npm install
npx prisma migrate dev --name init   # primera vez (genera migración)
# o: npx prisma migrate deploy      # CI / sin cambios de esquema
npx prisma db seed
```

Adminer: http://localhost:8080 (servidor `postgres`, usuario `agro`, BD `agrocontrol`).

## Esquema

Definido en [`backend/prisma/schema.prisma`](../../backend/prisma/schema.prisma).
