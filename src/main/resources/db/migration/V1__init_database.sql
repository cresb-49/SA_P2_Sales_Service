
CREATE TABLE IF NOT EXISTS snacks (
  id             UUID PRIMARY KEY,
  cinema_id      UUID         NOT NULL,
  name           VARCHAR(150) NOT NULL,
  price          NUMERIC(19,2) NOT NULL,
  external_image BOOLEAN      NOT NULL,
  image_url      TEXT         NOT NULL,
  active         BOOLEAN      NOT NULL,
  created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,

  -- Reglas útiles
  CONSTRAINT chk_snacks_price_nonneg CHECK (price >= 0)
);

-- (Opcional pero recomendado) Evita duplicar nombres por cine
ALTER TABLE snacks
  ADD CONSTRAINT uq_snacks_cinema_name UNIQUE (cinema_id, name);

-- Índices útiles para filtros comunes
CREATE INDEX IF NOT EXISTS idx_snacks_cinema_id  ON snacks (cinema_id);
CREATE INDEX IF NOT EXISTS idx_snacks_active     ON snacks (active);
CREATE INDEX IF NOT EXISTS idx_snacks_created_at ON snacks (created_at DESC);
