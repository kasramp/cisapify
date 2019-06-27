INSERT INTO roles(id, role) VALUES(1, 'ADMIN');
INSERT INTO roles(id, role) VALUES(2, 'USER');

-- password=12345
INSERT INTO users(id, email_address, password, enabled) VALUES(1, 'kasra@madadipouya.com', '$2a$10$MmwhooAP/.8wqEfj5rkeDe7GQVPkIXyiWF19YowMUETxC9HbfDMOq', true);
-- password=password - test user for hsqldb only not production
INSERT INTO users(id, email_address, password, enabled) VALUES(2, 'test@cisapify.com', '$2a$10$Whwanl4ElZik4zzwmLgv2.7hQr2h4lcxZoWnCQVRsfIp8.fBbNcuG', true);


INSERT INTO user_role(user_id, role_id) VALUES(1, 1);
INSERT INTO user_role(user_id, role_id) VALUES(2, 2);
