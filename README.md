# shoplite-notification-service

Consume `order.created` (consumer group `notification-service`), lưu notification. Idempotent theo `eventId` (unique constraint) vì Kafka giao at-least-once. Lỗi: retry 3 lần rồi đẩy sang `order.created-dlt`.

Event mới mang `payload.items[]`; message tóm tắt từng dòng và cắt còn 500 ký tự. Event cũ không có `items` vẫn đọc được (fallback "your items"), nên consumer và producer deploy độc lập được.

| Method | Path | Auth |
|---|---|---|
| GET | `/api/notifications` | JWT |

Env: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `OIDC_ISSUER_URI`, `KAFKA_BOOTSTRAP_SERVERS`.

## Chạy

- Cả stack: `shoplite-gitops/local` → `docker compose up -d --build`.
- Test: `./mvnw verify` (cần Docker cho Testcontainers).
- Image: `docker build -t <name> .` (multi-stage, layered jar, non-root UID 10001).

Health: `/actuator/health/liveness`, `/actuator/health/readiness` · Metrics: `/actuator/prometheus` · Trace/log: OpenTelemetry Java agent (inject từ ngoài, không có trong image).
