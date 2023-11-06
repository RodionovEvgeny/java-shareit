CREATE TABLE IF NOT EXISTS users (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS requests (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description VARCHAR(500) NOT NULL,
    requestor_id INT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_requests_requestor_id FOREIGN KEY(requestor_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS items (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(300) NOT NULL,
    owner_id INT NOT NULL,
    available BOOLEAN NOT NULL,
    request_id INT,
    CONSTRAINT fk_items_owner_id FOREIGN KEY(owner_id) REFERENCES users (id),
    CONSTRAINT fk_items_request_id FOREIGN KEY(request_id) REFERENCES requests (id)
);

CREATE TYPE IF NOT EXISTS booking_status AS ENUM ('WAITING', 'APPROVED', 'REJECTED');

CREATE TABLE IF NOT EXISTS bookings (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id INT NOT NULL,
    booker_id INT NOT NULL,
    status booking_status,
    CONSTRAINT fk_bookings_item_id FOREIGN KEY(item_id) REFERENCES items (id),
    CONSTRAINT fk_bookings_booker_id FOREIGN KEY(booker_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text VARCHAR(500) NOT NULL,
    item_id INT NOT NULL,
    author_id INT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_comments_item_id FOREIGN KEY(item_id) REFERENCES items (id),
    CONSTRAINT fk_comments_author_id FOREIGN KEY(author_id) REFERENCES users (id)
);