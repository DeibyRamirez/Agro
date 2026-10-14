# AgroControl (monorepo)

Gestión agropecuaria: **Android** (offline-first) + **NestJS** + **PostgreSQL** (Docker).

## Estructura

| Carpeta | Descripción |
|---------|-------------|
| `app/` | Cliente Android (Kotlin, Room, Retrofit) |
| `backend/` | API NestJS + Prisma |
| `infra/` | `docker-compose.yml` PostgreSQL |
| `docs/` | Arquitectura, patrones, agentes |

Documentación raíz: [todo.md](todo.md), [Memoria.md](Memoria.md), [Design.md](Design.md).

## Inicio rápido

```bash
# Base de datos
docker compose -f infra/docker-compose.yml up -d

# API (PostgreSQL debe estar arriba; migrar ANTES del seed)
cd backend
cp .env.example .env
npm install
npm run db:setup
npm run start:dev
```

Swagger: http://localhost:3000/api/docs  
Usuario demo: `demo@agrocontrol.local` / `demo1234`

## Android

Abrir `app/` en Android Studio, sincronizar Gradle, ejecutar en emulador (API → `10.0.2.2:3000`).

Smoke test: [docs/architecture/e2e-smoke.md](docs/architecture/e2e-smoke.md).

## Tests backend

```bash
cd backend && npm run test && npm run test:e2e
```
