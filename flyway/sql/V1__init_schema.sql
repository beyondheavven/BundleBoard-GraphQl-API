-- Last modification date: 2026-02-27 11:12:55.821

CREATE TYPE user_role AS ENUM ('admin', 'client', 'author');
CREATE TYPE user_status AS ENUM ('active', 'inactive', 'banned');
CREATE TYPE purchase_status AS ENUM ('pending', 'succeeded', 'failed', 'refunded');
CREATE TYPE media_provider AS ENUM ('local', 'google_drive');
CREATE TYPE media_mime_type AS ENUM ('image/jpeg', 'image/png', 'video/mp4', 'application/zip', 'application/rar', 'application/pdf');
CREATE TYPE media_file_type AS ENUM ('video', 'image', 'archive', 'document');

-- tables
-- Table: users
CREATE TABLE users (
                       id bigserial  NOT NULL,
                       username varchar(64)  UNIQUE NOT NULL,
                       email varchar(255)  UNIQUE NOT NULL,
                       role user_role[]  NOT NULL,
                       password_hash text  NOT NULL,
                       avatar_url text  NOT NULL,
                       status user_status  NOT NULL,
                       last_login_at TIMESTAMP WITH TIME ZONE NOT NULL,
                       created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT users_pk PRIMARY KEY (id)
);

-- Table: authors
CREATE TABLE authors (
                         users_id int8  NOT NULL,
                         bio text  NOT NULL,
                         social_links jsonb  NOT NULL,
                         rating decimal(10,2)  NOT NULL,
                         total_sales int  NOT NULL,
                         stripe_account_id text UNIQUE NOT NULL,
                         CONSTRAINT authors_pk PRIMARY KEY (users_id)
);

-- Table: carts
CREATE TABLE carts (
                       id bigserial  NOT NULL,
                       clients_id int8  NOT NULL,
                       collections_id int8  NOT NULL,
                       quantity int  NOT NULL,
                       CONSTRAINT carts_pk PRIMARY KEY (id)
);

-- Table: clients
CREATE TABLE clients (
                         users_id int8  NOT NULL,
                         newsletter_subscribed boolean  NOT NULL,
                         preferred_language text  NOT NULL,
                         CONSTRAINT clients_pk PRIMARY KEY (users_id)
);

-- Table: collection_tags
CREATE TABLE collection_tags (
                                 tags_id int8  NOT NULL,
                                 collections_id int8  NOT NULL,
                                 CONSTRAINT collection_tags_pk PRIMARY KEY (tags_id,collections_id)
);

-- Table: collections
CREATE TABLE collections (
                             id bigserial  NOT NULL,
                             name varchar(50)  NOT NULL,
                             description text  NOT NULL,
                             authors_id int8  NOT NULL,
                             price decimal(10,2)  NOT NULL,
                             video_tutorial_url text  UNIQUE NOT NULL,
                             project_file_id int8  NOT NULL,
                             preview_image_id int8  NOT NULL,
                             CONSTRAINT collections_pk PRIMARY KEY (id)
);

-- Table: images
CREATE TABLE images (
                        id bigserial  NOT NULL,
                        file_name text  NOT NULL,
                        file_path text  NOT NULL,
                        mime_type media_mime_type  NOT NULL,
                        width int  NOT NULL,
                        height int  NOT NULL,
                        file_size bigint  NOT NULL,
                        CONSTRAINT images_pk PRIMARY KEY (id)
);

-- Table: media_resources
CREATE TABLE media_resources (
                                 id bigserial  NOT NULL,
                                 file_name text  NOT NULL,
                                 file_path text  NOT NULL,
                                 file_type media_file_type NOT NULL,
                                 mime_type media_mime_type  NOT NULL,
                                 provider media_provider  NOT NULL,
                                 file_size bigint  NOT NULL,
                                 CONSTRAINT media_resources_pk PRIMARY KEY (id)
);

-- Table: purchases
CREATE TABLE purchases (
                           id bigserial  NOT NULL,
                           collections_id int8  NOT NULL,
                           clients_id int8  NOT NULL,
                           stripe_payment_intent_id text UNIQUE  NOT NULL,
                           amount decimal(10,2)  NOT NULL,
                           currency varchar(5)  NOT NULL,
                           status purchase_status  NOT NULL,
                           snapshot_price decimal(10,2)  NOT NULL,
                           created_at timestamp WITH TIME ZONE NOT NULL,
                           updated_at timestamp WITH TIME ZONE NOT NULL,
                           CONSTRAINT purchases_pk PRIMARY KEY (id)
);

-- Table: refresh_token
CREATE TABLE refresh_token (
                               id bigserial  NOT NULL,
                               users_id int8  NOT NULL,
                               token text  NOT NULL,
                               issued_at timestamp WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               expiration_time TIMESTAMP WITH TIME ZONE NOT NULL,
                               CONSTRAINT refresh_token_pk PRIMARY KEY (id)
);

-- Table: tags
CREATE TABLE tags (
                      id bigserial  NOT NULL,
                      name text  NOT NULL,
                      CONSTRAINT tags_pk PRIMARY KEY (id)
);


-- foreign keys
-- Reference: Authors_Users (table: authors)
ALTER TABLE authors ADD CONSTRAINT Authors_Users
    FOREIGN KEY (users_id)
        REFERENCES users (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: Clients_Users (table: clients)
ALTER TABLE clients ADD CONSTRAINT Clients_Users
    FOREIGN KEY (users_id)
        REFERENCES users (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: carts_clients (table: carts)
ALTER TABLE carts ADD CONSTRAINT carts_clients
    FOREIGN KEY (clients_id)
        REFERENCES clients (users_id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: carts_collections (table: carts)
ALTER TABLE carts ADD CONSTRAINT carts_collections
    FOREIGN KEY (collections_id)
        REFERENCES collections (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: collection_tags_collections (table: collection_tags)
ALTER TABLE collection_tags ADD CONSTRAINT collection_tags_collections
    FOREIGN KEY (collections_id)
        REFERENCES collections (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: collection_tags_tags (table: collection_tags)
ALTER TABLE collection_tags ADD CONSTRAINT collection_tags_tags
    FOREIGN KEY (tags_id)
        REFERENCES tags (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: collections_authors (table: collections)
ALTER TABLE collections ADD CONSTRAINT collections_authors
    FOREIGN KEY (authors_id)
        REFERENCES authors (users_id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: collections_images (table: collections)
ALTER TABLE collections ADD CONSTRAINT collections_images
    FOREIGN KEY (preview_image_id)
        REFERENCES images (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: collections_media_resources (table: collections)
ALTER TABLE collections ADD CONSTRAINT collections_media_resources
    FOREIGN KEY (project_file_id)
        REFERENCES media_resources (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: purchases_clients (table: purchases)
ALTER TABLE purchases ADD CONSTRAINT purchases_clients
    FOREIGN KEY (clients_id)
        REFERENCES clients (users_id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: purchases_collections (table: purchases)
ALTER TABLE purchases ADD CONSTRAINT purchases_collections
    FOREIGN KEY (collections_id)
        REFERENCES collections (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: refresh_token_Users (table: refresh_token)
ALTER TABLE refresh_token ADD CONSTRAINT refresh_token_Users
    FOREIGN KEY (users_id)
        REFERENCES users (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- End of file.

