CREATE DATABASE IF NOT EXISTS cisapify;

ALTER DATABASE cisapify
  DEFAULT CHARACTER SET utf8
  DEFAULT COLLATE utf8_general_ci;

USE cisapify;

CREATE TABLE IF NOT EXISTS vets (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(30),
  last_name VARCHAR(30),
  INDEX(last_name)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS specialties (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(80),
  INDEX(name)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS vet_specialties (
  vet_id INT(4) UNSIGNED NOT NULL,
  specialty_id INT(4) UNSIGNED NOT NULL,
  FOREIGN KEY (vet_id) REFERENCES vets(id),
  FOREIGN KEY (specialty_id) REFERENCES specialties(id),
  UNIQUE (vet_id,specialty_id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS types (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(80),
  INDEX(name)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS owners (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(30),
  last_name VARCHAR(30),
  address VARCHAR(255),
  city VARCHAR(80),
  telephone VARCHAR(20),
  INDEX(last_name)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS pets (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(30),
  birth_date DATE,
  type_id INT(4) UNSIGNED NOT NULL,
  owner_id INT(4) UNSIGNED NOT NULL,
  INDEX(name),
  FOREIGN KEY (owner_id) REFERENCES owners(id),
  FOREIGN KEY (type_id) REFERENCES types(id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS visits (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  pet_id INT(4) UNSIGNED NOT NULL,
  visit_date DATE,
  description VARCHAR(255),
  FOREIGN KEY (pet_id) REFERENCES pets(id)
) engine=InnoDB;


-- CISAPIFY

CREATE TABLE IF NOT EXISTS users (
  id INTEGER AUTO_INCREMENT PRIMARY KEY,
  email_address VARCHAR(512) NOT NULL,
  password VARCHAR(1024) NOT NULL,
  enabled BOOLEAN DEFAULT FALSE NOT NULL,
  gitlab_token VARCHAR(1024),
  gitlab_repository_name VARCHAR(512),
  CONSTRAINT uc_user_email_address UNIQUE(email_address),
  INDEX idx_user_email_address (email_address)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS songs (
  id INTEGER AUTO_INCREMENT PRIMARY KEY,
  display_name VARCHAR(1024) NOT NULL,
  file_name VARCHAR(128) NOT NULL,
  uri VARCHAR(1024) NOT NULL,
  source VARCHAR(128) DEFAULT 'LOCAL_SIMPLE_FILESYSTEM' NOT NULL,
  user_id INTEGER NOT NULL,

  CONSTRAINT uc_song_uri UNIQUE(uri),
  CONSTRAINT uc_song_file_name UNIQUE(file_name),
  FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
  INDEX idx_song_file_name (file_name)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS roles (
  id INTEGER AUTO_INCREMENT PRIMARY KEY,
  role VARCHAR(128) NOT NULL,

  CONSTRAINT uc_role_role_name UNIQUE (role),
  INDEX idx_role_role_name (role)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS user_role (
  user_id INTEGER NOT NULL,
  role_id INTEGER NOT NULL,
  PRIMARY KEY (user_id, role_id),

  CONSTRAINT fk_user_role_user_id FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_user_role_role_id FOREIGN KEY (role_id) REFERENCES roles (id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS playlists (
  id INTEGER AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(1024) NOT NULL,
  user_id INTEGER NOT NULL,

  CONSTRAINT uc_playlist_name UNIQUE(name),
  FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS playlist_song (
  playlist_id INTEGER NOT NULL,
  song_id INTEGER NOT NULL,

  CONSTRAINT fk_playlist_song_playlist_id FOREIGN KEY (playlist_id) REFERENCES playlists (id),
  CONSTRAINT fk_playlist_song_song_id FOREIGN KEY (song_id) REFERENCES songs (id)
) engine=InnoDB;
-- END CISAPIFY