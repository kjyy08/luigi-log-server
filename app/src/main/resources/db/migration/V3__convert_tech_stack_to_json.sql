-- ==========================================
-- Convert tech_stack from TEXT to JSON
-- ==========================================

ALTER TABLE profile ALTER COLUMN tech_stack TYPE JSON USING
    CASE
        WHEN tech_stack IS NULL OR tech_stack = '' THEN '[]'::JSON
        ELSE tech_stack::JSON
    END;
