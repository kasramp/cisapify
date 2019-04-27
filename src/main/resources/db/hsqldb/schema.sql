DROP TABLE vet_specialties IF EXISTS;
DROP TABLE vets IF EXISTS;
DROP TABLE specialties IF EXISTS;
DROP TABLE visits IF EXISTS;
DROP TABLE pets IF EXISTS;
DROP TABLE types IF EXISTS;
DROP TABLE owners IF EXISTS;


CREATE TABLE vets (
  id         INTEGER IDENTITY PRIMARY KEY,
  first_name VARCHAR(30),
  last_name  VARCHAR(30)
);
CREATE INDEX vets_last_name ON vets (last_name);

CREATE TABLE specialties (
  id   INTEGER IDENTITY PRIMARY KEY,
  name VARCHAR(80)
);
CREATE INDEX specialties_name ON specialties (name);

CREATE TABLE vet_specialties (
  vet_id       INTEGER NOT NULL,
  specialty_id INTEGER NOT NULL
);
ALTER TABLE vet_specialties ADD CONSTRAINT fk_vet_specialties_vets FOREIGN KEY (vet_id) REFERENCES vets (id);
ALTER TABLE vet_specialties ADD CONSTRAINT fk_vet_specialties_specialties FOREIGN KEY (specialty_id) REFERENCES specialties (id);

CREATE TABLE types (
  id   INTEGER IDENTITY PRIMARY KEY,
  name VARCHAR(80)
);
CREATE INDEX types_name ON types (name);

CREATE TABLE owners (
  id         INTEGER IDENTITY PRIMARY KEY,
  first_name VARCHAR(30),
  last_name  VARCHAR_IGNORECASE(30),
  address    VARCHAR(255),
  city       VARCHAR(80),
  telephone  VARCHAR(20)
);
CREATE INDEX owners_last_name ON owners (last_name);

CREATE TABLE pets (
  id         INTEGER IDENTITY PRIMARY KEY,
  name       VARCHAR(30),
  birth_date DATE,
  type_id    INTEGER NOT NULL,
  owner_id   INTEGER NOT NULL
);
ALTER TABLE pets ADD CONSTRAINT fk_pets_owners FOREIGN KEY (owner_id) REFERENCES owners (id);
ALTER TABLE pets ADD CONSTRAINT fk_pets_types FOREIGN KEY (type_id) REFERENCES types (id);
CREATE INDEX pets_name ON pets (name);

CREATE TABLE visits (
  id          INTEGER IDENTITY PRIMARY KEY,
  pet_id      INTEGER NOT NULL,
  visit_date  DATE,
  description VARCHAR(255)
);
ALTER TABLE visits ADD CONSTRAINT fk_visits_pets FOREIGN KEY (pet_id) REFERENCES pets (id);
CREATE INDEX visits_pet_id ON visits (pet_id);


-- CISAPIFY

DROP TABLE users IF EXISTS;

CREATE TABLE users (
  id INTEGER IDENTITY PRIMARY KEY,
  email_address VARCHAR(512) NOT NULL,
  password VARCHAR(1024) NOT NULL,
  enabled BOOLEAN DEFAULT FALSE NOT NULL,
  gitlab_token VARCHAR(256),
  gitlab_repository_name VARCHAR(512),
  CONSTRAINT uc_user_email_address UNIQUE(email_address)
);
CREATE INDEX idx_user_email_address ON users (email_address);

DROP TABLE songs IF EXISTS;

CREATE TABLE songs (
  id INTEGER IDENTITY PRIMARY KEY,
  display_name VARCHAR(1024) NOT NULL,
  file_name VARCHAR(128) NOT NULL,
  uri VARCHAR(1024) NOT NULL,
  user_id INTEGER NOT NULL,

  CONSTRAINT uc_song_uri UNIQUE(uri),
  CONSTRAINT uc_song_file_name UNIQUE(file_name),
  FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
CREATE INDEX idx_song_file_name ON songs (file_name);

CREATE TABLE roles (
  id INTEGER IDENTITY PRIMARY KEY,
  role VARCHAR(128) NOT NULL,

  CONSTRAINT uc_role_role_name UNIQUE (role)
);
CREATE INDEX idx_role_role_name on roles (role);

CREATE TABLE user_role (
  user_id INTEGER NOT NULL,
  role_id INTEGER NOT NULL,
  PRIMARY KEY (user_id, role_id),

  CONSTRAINT fk_user_role_user_id FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_user_role_role_id FOREIGN KEY (role_id) REFERENCES roles (id)
);

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

CREATE TABLE playlist_song (
  playlist_id INTEGER NOT NULL,
  song_id INTEGER NOT NULL,

  CONSTRAINT fk_playlist_song_playlist_id FOREIGN KEY (playlist_id) REFERENCES playlists (id),
  CONSTRAINT fk_playlist_song_song_id FOREIGN KEY (song_id) REFERENCES songs (id)
);
-- END CISAPIFY