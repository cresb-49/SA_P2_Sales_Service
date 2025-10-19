CREATE TABLE IF NOT EXISTS sales (
  id           UUID PRIMARY KEY,
  client_id    UUID,
  cinema_id    UUID                  NOT NULL,
  total_amount NUMERIC(18,2)         NOT NULL,
  status       VARCHAR(50)           NOT NULL,
  created_at   TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
  updated_at   TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
  paid_at      TIMESTAMP(6) WITHOUT TIME ZONE
);

-- Índices útiles (ajusta según tus consultas reales)
CREATE INDEX IF NOT EXISTS idx_sales_client_id  ON sales (client_id);
CREATE INDEX IF NOT EXISTS idx_sales_cinema_id  ON sales (cinema_id);
CREATE INDEX IF NOT EXISTS idx_sales_created_at ON sales (created_at);
CREATE INDEX IF NOT EXISTS idx_sales_paid_at    ON sales (paid_at);

------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS sale_line_snacks (
  id          UUID PRIMARY KEY,
  sale_id     UUID              NOT NULL,
  snack_id    UUID              NOT NULL,
  quantity    INTEGER           NOT NULL,
  unit_price  NUMERIC(18,2)     NOT NULL,
  total_price NUMERIC(18,2)     NOT NULL,

  -- Reglas básicas
  CONSTRAINT chk_sls_quantity_pos     CHECK (quantity > 0),
  CONSTRAINT chk_sls_unit_price_pos   CHECK (unit_price >= 0),
  CONSTRAINT chk_sls_total_price_pos  CHECK (total_price >= 0)
);

-- Índices útiles
CREATE INDEX IF NOT EXISTS idx_sls_sale_id  ON sale_line_snacks (sale_id);
CREATE INDEX IF NOT EXISTS idx_sls_snack_id ON sale_line_snacks (snack_id);
CREATE INDEX IF NOT EXISTS idx_sls_sale_snack ON sale_line_snacks (sale_id, snack_id);

-- Llaves foráneas si existen las tablas relacionadas:
ALTER TABLE sale_line_snacks
   ADD CONSTRAINT fk_sls_sale  FOREIGN KEY (sale_id)  REFERENCES sales(id),
   ADD CONSTRAINT fk_sls_snack FOREIGN KEY (snack_id) REFERENCES snacks(id);

------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS sale_line_tickets (
  id           UUID PRIMARY KEY,
  sale_id      UUID                  NOT NULL,
  quantity     INTEGER               NOT NULL,
  unit_price   NUMERIC(18,2)         NOT NULL,
  total_price  NUMERIC(18,2)         NOT NULL,
  status       VARCHAR(50)           NOT NULL,
  created_at   TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
  updated_at   TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,

  -- Reglas básicas
  CONSTRAINT chk_slt_quantity_pos     CHECK (quantity > 0),
  CONSTRAINT chk_slt_unit_price_pos   CHECK (unit_price >= 0),
  CONSTRAINT chk_slt_total_price_pos  CHECK (total_price >= 0)
);

-- Índices útiles
CREATE INDEX IF NOT EXISTS idx_slt_sale_id     ON sale_line_tickets (sale_id);
CREATE INDEX IF NOT EXISTS idx_slt_status      ON sale_line_tickets (status);
CREATE INDEX IF NOT EXISTS idx_slt_created_at  ON sale_line_tickets (created_at);

-- Llaves foráneas si existen las tablas relacionadas:
ALTER TABLE sale_line_tickets
  ADD CONSTRAINT fk_slt_sale  FOREIGN KEY (sale_id) REFERENCES sales(id);