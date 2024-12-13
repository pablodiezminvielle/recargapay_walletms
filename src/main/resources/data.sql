-- Drop the tables if they exist
DROP TABLE IF EXISTS TRANSACTION_RPAY;
DROP TABLE IF EXISTS WALLET;
DROP TABLE IF EXISTS USER_RPAY;

-- Create the users table
CREATE TABLE USER_RPAY (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL
);

-- Create the wallets table
CREATE TABLE WALLET (
    id UUID PRIMARY KEY,
    balance DECIMAL(10, 2),
    user_uid UUID NOT NULL,
    version BIGINT,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_uid) REFERENCES USER_RPAY(id) ON DELETE CASCADE
);

-- Create transaction table
CREATE TABLE TRANSACTION_RPAY (
                             id UUID PRIMARY KEY,
                             wallet_id UUID NOT NULL,
                             amount DECIMAL(19, 2) NOT NULL,
                             timestamp TIMESTAMP NOT NULL,
                             type VARCHAR(20) NOT NULL,
                             FOREIGN KEY (wallet_id) REFERENCES WALLET(id)
);

-- Insert 2 users
INSERT INTO USER_RPAY (id, name, email) VALUES
('1a4f7b78-e774-4e98-97d2-734dd06b2c58', 'John Doe', 'john.doe@example.com'),
('9b1f4ab7-2de7-4174-b71a-ccf1b2d2f1b4', 'Jane Smith', 'jane.smith@example.com');

-- Insert 2 wallets and associate them with users
INSERT INTO WALLET (id, balance, user_uid, version, updated_at) VALUES
('8e5ed43e-7b6b-4339-bf1a-1c8ed2da1234', 150.75, '1a4f7b78-e774-4e98-97d2-734dd06b2c58', 1, CURRENT_TIMESTAMP), -- Wallet for John
('4f0d2c17-ff93-4bfa-bba3-52d2b4c5482e', 200.00, '9b1f4ab7-2de7-4174-b71a-ccf1b2d2f1b4', 1, CURRENT_TIMESTAMP); -- Wallet for Jane

-- Create index on the users table (e.g., on the email field)
CREATE INDEX idx_email ON USER_RPAY(email);

-- Create index on the wallet table for optimizing searches by user_uid
CREATE INDEX idx_user_uid ON WALLET(user_uid);

-- create indexes on transaction table to improve searches by wallet id and timestamp
CREATE INDEX idx_transaction_wallet_id ON TRANSACTION_RPAY(wallet_id);
CREATE INDEX idx_transaction_timestamp ON TRANSACTION_RPAY(timestamp);
