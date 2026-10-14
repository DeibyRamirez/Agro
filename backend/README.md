# AgroControl Backend

NestJS API + Prisma + PostgreSQL.

## Setup

```bash
docker compose -f ../infra/docker-compose.yml up -d
cp .env.example .env
npm install
npx prisma migrate deploy
npx prisma db seed
npm run start:dev
```

Swagger: http://localhost:3000/api/docs

## Tests

```bash
npm run test
npm run test:e2e
```

Demo user: `demo@agrocontrol.local` / `demo1234`
