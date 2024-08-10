CREATE TABLE person
(
    id integer NOT NULL,
    name character varying,
    company_id integer references company(id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);

INSERT INTO person (id, name, company_id) VALUES
(1, 'Alice', 1),
(2, 'Bob', 1),
(3, 'Charlie', 2),
(4, 'David', 2),
(5, 'Eve', 3),
(6, 'Frank', 4),
(7, 'Grace', 4),
(8, 'Hannah', 5), 
(9, 'Isaac', 5); 