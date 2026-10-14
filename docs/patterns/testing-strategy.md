# Estrategia de pruebas

## Backend

- **Unit**: services con Prisma mock.
- **E2E**: Supertest contra app Nest con BD de test (PostgreSQL o sqlite si se añade).
- Comandos: `npm run test`, `npm run test:e2e`, `npm run test:cov`.

## Android

- Unit: validadores y mappers.
- Instrumented: Room DAO, sync outbox (opcional).
- Manual: `docs/architecture/e2e-smoke.md`.
