CREATE TABLE IF NOT EXISTS tickets (
  id                  UUID PRIMARY KEY,
  sale_line_ticket_id UUID      NOT NULL,
  cinema_function_id  UUID      NOT NULL,
  cinema_id           UUID      NOT NULL,
  cinema_room_id      UUID      NOT NULL,
  seat_id             UUID      NOT NULL,
  movie_id            UUID      NOT NULL,
  used                BOOLEAN   NOT NULL,
  created_at          TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
  updated_at          TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL
);

-- Índices útiles para búsquedas frecuentes
CREATE INDEX IF NOT EXISTS idx_tickets_sale_line_ticket_id ON tickets (sale_line_ticket_id);
CREATE INDEX IF NOT EXISTS idx_tickets_cinema_function_id  ON tickets (cinema_function_id);
CREATE INDEX IF NOT EXISTS idx_tickets_seat_id             ON tickets (seat_id);
