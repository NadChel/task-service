DO '
BEGIN
   CREATE TYPE task_status AS ENUM (''TO_DO'', ''IN_PROGRESS'', ''DONE'');
EXCEPTION WHEN duplicate_object THEN NULL;
END;
' LANGUAGE PLPGSQL;

CREATE TABLE IF NOT EXISTS "user"
(
    id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name  VARCHAR(200) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS task
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    status      TASK_STATUS NOT NULL DEFAULT 'TO_DO',
    assignee_id UUID,

    CONSTRAINT fk_task_assignee FOREIGN KEY (assignee_id)
        REFERENCES "user" (id)
        ON DELETE SET NULL
);