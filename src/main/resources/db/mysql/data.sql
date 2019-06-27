INSERT IGNORE INTO roles(id, role) VALUES(1, 'ADMIN');
INSERT IGNORE INTO roles(id, role) VALUES(2, 'USER');

INSERT IGNORE INTO users(id, email_address, password, enabled) VALUES(1, 'kasra@madadipouya.com', '$2a$10$MmwhooAP/.8wqEfj5rkeDe7GQVPkIXyiWF19YowMUETxC9HbfDMOq', true);

INSERT IGNORE INTO user_role(user_id, role_id) VALUES(1, 1);
