DROP TABLE subscribes;
CREATE TABLE
    subscribes (
		id int NOT NULL AUTO_INCREMENT,
		user int,
        thread int,
        PRIMARY KEY(id),
        FOREIGN KEY (user) REFERENCES users(id),
        FOREIGN KEY (thread) REFERENCES threads(id),
        UNIQUE (user, thread)
);

select * from subscribes;

SELECT thread FROM subscribes  WHERE user=4;

