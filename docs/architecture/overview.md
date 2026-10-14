# Arquitectura — overview

## C4 (contenedor)

- **App Android**: UI, ViewModels, Repositories, Room, WorkManager, Retrofit.
- **API NestJS**: REST, JWT, Prisma.
- **PostgreSQL**: datos relacionales (Docker).

## Despliegue local

```bash
docker compose -f infra/docker-compose.yml up -d
cd backend && npm install && npx prisma migrate deploy && npm run start:dev
```

Android Studio: ejecutar módulo `app`; API base `10.0.2.2:3000`.

## Módulos backend

auth, animals, paddocks, production, reproduction, tasks, reports, dashboard, sync.
