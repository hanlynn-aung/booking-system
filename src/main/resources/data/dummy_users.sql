INSERT INTO users
(username, email, password, first_name, last_name, phone_number, status, verification_token, verification_token_expiry, created_at, updated_at)
VALUES
-- Verified users
('john_doe', 'john@example.com', '$2a$12$zPJZLFeORoCPbE.pVua7g.QwY64aiN1uLYnH4Iua6Yvv.INmAQLkG', 'John', 'Doe', '0911111111', 'ACTIVE', NULL, NULL, NOW(), NOW()),
('bob_cho', 'bob@example.com', '$2a$12$zPJZLFeORoCPbE.pVua7g.QwY64aiN1uLYnH4Iua6Yvv.INmAQLkG', 'Bob', 'Cho', '0944444444', 'ACTIVE', NULL, NULL, NOW(), NOW()),

-- Unverified users (UUID tokens + 1 hour expiry)
('jane_smith', 'jane@example.com', '$2a$12$zPJZLFeORoCPbE.pVua7g.QwY64aiN1uLYnH4Iua6Yvv.INmAQLkG', 'Jane', 'Smith', '0922222222', 'PENDING_VERIFICATION', 'b9c4a1f0-1234-4abc-9f8e-7d3d17a8a0e5', DATE_ADD(NOW(), INTERVAL 1 HOUR), NOW(), NOW()),
('alice_lee', 'alice@example.com', '$2a$12$zPJZLFeORoCPbE.pVua7g.QwY64aiN1uLYnH4Iua6Yvv.INmAQLkG', 'Alice', 'Lee', '0933333333', 'PENDING_VERIFICATION', '3d75c2b8-5678-40ea-9c0d-1c4a6e8ddf12', DATE_ADD(NOW(), INTERVAL 1 HOUR), NOW(), NOW()),
('eva_lwin', 'eva@example.com', '$2a$12$zPJZLFeORoCPbE.pVua7g.QwY64aiN1uLYnH4Iua6Yvv.INmAQLkG', 'Eva', 'Lwin', '0955555555', 'PENDING_VERIFICATION', 'f22d3b6c-9012-4a7b-a3d1-8cf91a4f3e33', DATE_ADD(NOW(), INTERVAL 1 HOUR), NOW(), NOW());

--all password are password