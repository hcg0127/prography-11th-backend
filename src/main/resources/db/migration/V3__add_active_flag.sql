-- cohort column
ALTER TABLE COHORTS
    ADD COLUMN active BOOLEAN DEFAULT false NOT NULL;

-- generation
UPDATE COHORTS
SET active = true
WHERE generation = 11;