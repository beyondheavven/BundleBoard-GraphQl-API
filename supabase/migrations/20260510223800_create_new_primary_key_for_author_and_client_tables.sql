--Author
ALTER TABLE authors ADD COLUMN id bigserial;
ALTER TABLE collections DROP CONSTRAINT collections_authors;
ALTER TABLE authors DROP CONSTRAINT authors_pk;
ALTER TABLE authors ADD CONSTRAINT authors_pk PRIMARY KEY (id);
ALTER TABLE authors ADD CONSTRAINT authors_users_id_unique UNIQUE (users_id);

UPDATE collections c
SET authors_id = a.id
    FROM authors a
WHERE c.authors_id = a.users_id;

ALTER TABLE collections ADD CONSTRAINT collections_authors
    FOREIGN KEY (authors_id) REFERENCES authors (id);

--Client
ALTER TABLE clients ADD COLUMN id bigserial;
ALTER TABLE carts DROP CONSTRAINT carts_clients;
ALTER TABLE purchases DROP CONSTRAINT purchases_clients;
ALTER TABLE clients DROP CONSTRAINT clients_pk;
ALTER TABLE clients ADD CONSTRAINT clients_pk PRIMARY KEY (id);
ALTER TABLE clients ADD CONSTRAINT clients_users_id_unique UNIQUE (users_id);

UPDATE carts ct
SET clients_id = cl.id
    FROM clients cl
WHERE ct.clients_id = cl.users_id;

UPDATE purchases p
SET clients_id = cl.id
    FROM clients cl
WHERE p.clients_id = cl.users_id;

ALTER TABLE carts ADD CONSTRAINT carts_clients
    FOREIGN KEY (clients_id) REFERENCES clients (id);

ALTER TABLE purchases ADD CONSTRAINT purchases_clients
    FOREIGN KEY (clients_id) REFERENCES clients (id);