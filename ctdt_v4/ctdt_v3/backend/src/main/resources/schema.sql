CREATE DATABASE IF NOT EXISTS ctdt_db DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;
USE ctdt_db;
CREATE TABLE IF NOT EXISTS project (
    project_id   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    project_name VARCHAR(255)    NOT NULL,
    status       ENUM('ACTIVE','ARCHIVED') NOT NULL DEFAULT 'ACTIVE',
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE IF NOT EXISTS contributor (
    contributor_id   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    project_id       BIGINT UNSIGNED NOT NULL,
    contributor_name VARCHAR(255)    NOT NULL,
    joined_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (contributor_id),
    INDEX idx_contributor_project (project_id),
    CONSTRAINT fk_contributor_project FOREIGN KEY (project_id) REFERENCES project(project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE IF NOT EXISTS contribution_log (
    log_id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    contributor_id    BIGINT UNSIGNED NOT NULL,
    project_id        BIGINT UNSIGNED NOT NULL,
    file_name         VARCHAR(512)    NOT NULL,
    content_size      DECIMAL(18,4)   NOT NULL,
    weight_factor     DECIMAL(5,2)    NOT NULL,
    contribution_type ENUM('ORIGINAL','MODIFIED') NOT NULL DEFAULT 'ORIGINAL',
    retained_flag     TINYINT(1)      NOT NULL DEFAULT 1,
    timestamp         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (log_id),
    INDEX idx_log_contributor (contributor_id),
    INDEX idx_log_project     (project_id),
    CONSTRAINT fk_log_contributor FOREIGN KEY (contributor_id) REFERENCES contributor(contributor_id),
    CONSTRAINT fk_log_project     FOREIGN KEY (project_id)     REFERENCES project(project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE IF NOT EXISTS contribution_result (
    result_id      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    contributor_id BIGINT UNSIGNED NOT NULL,
    project_id     BIGINT UNSIGNED NOT NULL,
    percentage     DECIMAL(5,2)    NOT NULL DEFAULT 0.00,
    last_updated   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (result_id),
    UNIQUE KEY uq_result (contributor_id, project_id),
    INDEX idx_result_project (project_id),
    CONSTRAINT fk_result_contributor FOREIGN KEY (contributor_id) REFERENCES contributor(contributor_id),
    CONSTRAINT fk_result_project     FOREIGN KEY (project_id)     REFERENCES project(project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE IF NOT EXISTS recalculation_history (
    history_id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    project_id           BIGINT UNSIGNED NOT NULL,
    triggered_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_project_weight DECIMAL(18,4) NOT NULL,
    execution_time_ms    BIGINT NOT NULL,
    PRIMARY KEY (history_id),
    INDEX idx_history_project (project_id),
    CONSTRAINT fk_history_project FOREIGN KEY (project_id) REFERENCES project(project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
