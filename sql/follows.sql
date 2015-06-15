CREATE TABLE
    follows (
		id int NOT NULL AUTO_INCREMENT,
		id1 int,
        id2 int,
        PRIMARY KEY(id),
        FOREIGN KEY (id1) REFERENCES Users(id),
        FOREIGN KEY (id2) REFERENCES Users(id),
        UNIQUE (id1, id2)
);

select * from follows;

SELECT email FROM follows f JOIN users u ON f.id1 = u.id WHERE f.id2=1  ORDER BY email DESC;

SELECT id FROM users WHERE email='example@mail.ru';

Truncate follows;

