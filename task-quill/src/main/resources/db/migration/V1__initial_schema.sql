CREATE SCHEMA IF NOT EXISTS data;

CREATE TABLE IF NOT EXISTS data.test_table
(
    id         BIGSERIAL PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_Name  TEXT NOT NULL
)
