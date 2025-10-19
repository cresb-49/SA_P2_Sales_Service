-- 1) Agregar columnas con DEFAULT 0 para no romper inserciones/consultas existentes
ALTER TABLE sales
  ADD COLUMN IF NOT EXISTS claimed_amount    NUMERIC(18,2) DEFAULT 0,
  ADD COLUMN IF NOT EXISTS discounted_amount NUMERIC(18,2) DEFAULT 0;

-- 2) Asegurar que los registros ya existentes tengan 0
UPDATE sales
   SET claimed_amount    = COALESCE(claimed_amount, 0),
       discounted_amount = COALESCE(discounted_amount, 0)
 WHERE claimed_amount IS NULL
    OR discounted_amount IS NULL;

-- 3) Forzar NOT NULL (según tu entidad)
ALTER TABLE sales
  ALTER COLUMN claimed_amount    SET NOT NULL,
  ALTER COLUMN discounted_amount SET NOT NULL;