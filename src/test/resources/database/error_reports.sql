DROP TABLE IF EXISTS error_reports;

CREATE TABLE error_reports (
    record_status_id INT(11),
    MESSAGE VARCHAR(1024) DEFAULT NULL,
    URL varchar(1024) DEFAULT NULL,
    STACKTRACE text,
    STATUS_CODE INT(11) DEFAULT NULL
);

CREATE INDEX error_reports_index ON error_reports (STATUS_CODE);
