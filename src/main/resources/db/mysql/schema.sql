CREATE DATABASE IF NOT EXISTS cisapify;

ALTER DATABASE cisapify
  DEFAULT CHARACTER SET utf8
  DEFAULT COLLATE utf8_general_ci;

USE cisapify;

CREATE TABLE IF NOT EXISTS users (
  id INTEGER AUTO_INCREMENT PRIMARY KEY,
  email_address VARCHAR(512) NOT NULL,
  password VARCHAR(1024) NOT NULL,
  enabled BOOLEAN DEFAULT FALSE NOT NULL,
  gitlab_token VARCHAR(1024),
  gitlab_repository_name VARCHAR(512),
  dropbox_token VARCHAR(128),
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

CREATE TABLE IF NOT EXISTS system_settings (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(1024) NOT NULL,
    value VARCHAR(1024) NOT NULL,
    description LONGTEXT NOT NULL,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uc_system_settings_name UNIQUE (name)
    INDEX idx_system_settings_name (name)
) engine=InnoDB;