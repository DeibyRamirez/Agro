# Contratos API

- Base: `/api/v1`
- Auth: `Authorization: Bearer <accessToken>`
- Errores JSON: `{ "statusCode", "message", "error" }`
- Fechas: ISO 8601 (`YYYY-MM-DD` o datetime UTC)
- Paginación (futuro): `?page=1&limit=20`

## Sync

- `POST /sync/push` body: `{ "changes": [{ "entity", "operation", "localId", "payload", "clientUpdatedAt" }] }`
- `GET /sync/pull?since=<iso>` → entidades modificadas desde `since`
