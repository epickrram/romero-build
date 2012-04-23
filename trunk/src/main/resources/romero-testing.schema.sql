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
    primary key (job_run_identifier, start_timestamp, test_suite, test_case)
);
