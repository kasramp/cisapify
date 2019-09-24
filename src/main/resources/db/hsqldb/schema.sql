DROP TABLE users IF EXISTS;

CREATE TABLE users (
  id INTEGER IDENTITY PRIMARY KEY,
  email_address VARCHAR(512) NOT NULL,
  password VARCHAR(1024) NOT NULL,
  enabled BOOLEAN DEFAULT FALSE NOT NULL,
  gitlab_token VARCHAR(256),
  gitlab_repository_name VARCHAR(512),
  dropbox_token VARCHAR(128),
  CONSTRAINT uc_user_email_address UNIQUE(email_address)
);
CREATE INDEX idx_user_email_address ON users (email_address);

DROP TABLE songs IF EXISTS;

CREATE TABLE songs (
  id INTEGER IDENTITY PRIMARY KEY,
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
        ON UPDATE CASCADE
);
CREATE INDEX idx_song_file_name ON songs (file_name);

DROP TABLE roles IF EXISTS;

CREATE TABLE roles (
  id INTEGER IDENTITY PRIMARY KEY,
  role VARCHAR(128) NOT NULL,

  CONSTRAINT uc_role_role_name UNIQUE (role)
);
CREATE INDEX idx_role_role_name on roles (role);

DROP TABLE user_role IF EXISTS;

CREATE TABLE user_role (
  user_id INTEGER NOT NULL,
  role_id INTEGER NOT NULL,
  PRIMARY KEY (user_id, role_id),

  CONSTRAINT fk_user_role_user_id FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_user_role_role_id FOREIGN KEY (role_id) REFERENCES roles (id)
);

DROP TABLE playlists IF EXISTS;

CREATE TABLE playlists (
  id INTEGER IDENTITY PRIMARY KEY,
  name VARCHAR(1024) NOT NULL,
  user_id INTEGER NOT NULL,

  CONSTRAINT uc_playlist_name UNIQUE(name),
  FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

DROP TABLE playlist_song IF EXISTS;

CREATE TABLE playlist_song (
  playlist_id INTEGER NOT NULL,
  song_id INTEGER NOT NULL,

  CONSTRAINT fk_playlist_song_playlist_id FOREIGN KEY (playlist_id) REFERENCES playlists (id),
  CONSTRAINT fk_playlist_song_song_id FOREIGN KEY (song_id) REFERENCES songs (id)
);

DROP TABLE system_settings IF EXISTS;

CREATE TABLE system_settings (
    id INTEGER IDENTITY PRIMARY KEY,
    name VARCHAR(1024) NOT NULL,
    value VARCHAR(1024) NOT NULL,
    description VARCHAR(10240) NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uc_system_settings_name UNIQUE (name)
);
CREATE INDEX idx_system_settings_name ON system_settings (name);