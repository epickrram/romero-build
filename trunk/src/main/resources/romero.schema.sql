CREATE TABLE job_run (
    id INT NOT NULL AUTO_INCREMENT,
    identifier VARCHAR(255) NOT NULL,
    start_timestamp BIGINT NOT NULL,
    end_timestamp BIGINT NOT NULL,
    primary key (id)
);

CREATE INDEX idx_job_run_identifier ON job_run(identifier);