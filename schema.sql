CREATE DATABASE IF NOT EXISTS finguard CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE finguard;
CREATE TABLE IF NOT EXISTS transactions (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, account_id VARCHAR(80) NOT NULL,
 reference_id VARCHAR(120) NOT NULL UNIQUE, amount DECIMAL(19,4) NOT NULL CHECK(amount >= 0),
 currency CHAR(3) NOT NULL, type ENUM('CREDIT','DEBIT') NOT NULL, merchant VARCHAR(160) NOT NULL,
 transaction_time TIMESTAMP NOT NULL, status VARCHAR(24) NOT NULL, risk_score INT NOT NULL,
 risk_level VARCHAR(10) NOT NULL, risk_reasons TEXT NOT NULL,
 INDEX idx_account(account_id), INDEX idx_time(transaction_time), INDEX idx_risk(risk_level)
);
CREATE TABLE IF NOT EXISTS audit_logs (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, event_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 event_type VARCHAR(60) NOT NULL, transaction_id BIGINT NULL, account_id VARCHAR(80), message VARCHAR(1000) NOT NULL,
 INDEX idx_audit_time(event_time), CONSTRAINT fk_audit_tx FOREIGN KEY(transaction_id) REFERENCES transactions(id) ON DELETE SET NULL
);
CREATE TABLE IF NOT EXISTS risk_alerts (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, transaction_id BIGINT NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 risk_score INT NOT NULL, risk_level VARCHAR(10) NOT NULL, reasons TEXT NOT NULL,
 CONSTRAINT fk_alert_tx FOREIGN KEY(transaction_id) REFERENCES transactions(id) ON DELETE CASCADE, INDEX idx_alert_level(risk_level)
);
