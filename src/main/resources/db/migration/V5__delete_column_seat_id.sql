-- Delete the 'seat_id' column from the 'tickets' table if it exists
ALTER TABLE tickets
    DROP COLUMN IF EXISTS seat_id;
-- Delete the index on 'seat_id' if it exists
DROP INDEX IF EXISTS idx_tickets_seat_id;