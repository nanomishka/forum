DROP TABLE postslikes;
CREATE TABLE
    postlikes (
		id int NOT NULL AUTO_INCREMENT,
        post int NOT NULL,
        tlike BOOLEAN,
        PRIMARY KEY(id),
        FOREIGN KEY (post) REFERENCES posts(id)
);

select * from postlikes;
