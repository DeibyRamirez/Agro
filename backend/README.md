# AgroControl Backend

NestJS API + Prisma + PostgreSQL.

## Setup

```bash
docker compose -f ../infra/docker-compose.yml up -d
cp .env.example .env
npm install
npm run db:setup
npm run start:dev
```

`db:setup` ejecuta en orden: `prisma generate` → `migrate deploy` (crea tablas) → `db seed` (datos demo). No ejecutes solo `db seed` sin migrar antes.

Swagger: http://localhost:3000/api/docs

## Tests

```bash
npm run test
npm run test:e2e
```

Demo user: `demo@agrocontrol.local` / `demo1234`
