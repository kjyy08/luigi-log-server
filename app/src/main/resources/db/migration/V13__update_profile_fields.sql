-- V13: Update profile table schema for API 2.1 requirements
-- Add: readme (TEXT), company (VARCHAR), location (VARCHAR)
-- Remove: tech_stack (JSON)

ALTER TABLE profile
    ADD COLUMN readme TEXT,
    ADD COLUMN company VARCHAR(100),
    ADD COLUMN location VARCHAR(100),
    DROP COLUMN tech_stack;
