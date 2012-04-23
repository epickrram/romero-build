CREATE TABLE test_case_result (
    job_run_identifier VARCHAR(255) NOT NULL,
    start_timestamp BIGINT NOT NULL,
    duration_millis INT NOT NULL,
    test_suite VARCHAR(255) NOT NULL,
    test_case VARCHAR(255) NOT NULL,
    status VARCHAR(16) NOT NULL,
    stdout TEXT,
    stderr TEXT,
    stack_trace TEXT,
    primary key (job_run_identifier, start_timestamp)
);

CREATE INDEX idx_test_suite_name ON test_case_result(test_suite);
CREATE INDEX idx_test_case_name ON test_case_result(test_case);