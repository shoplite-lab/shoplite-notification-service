CREATE TABLE notifications (
    id         BIGSERIAL PRIMARY KEY,
    -- Idempotency key: Kafka delivers at-least-once, so the same event may arrive twice.
    event_id   VARCHAR(64)  NOT NULL UNIQUE,
    user_id    VARCHAR(64)  NOT NULL,
    channel    VARCHAR(20)  NOT NULL,
    message    VARCHAR(500) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_notifications_user_id ON notifications (user_id, created_at DESC);
