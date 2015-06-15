/*use test;
show tables from test;
DROP TABLE users;

TRUNCATE TABLE users; */


CREATE TABLE
    users (
		id int NOT NULL AUTO_INCREMENT,
		username CHAR(30),
        about CHAR(30),
        name CHAR(20),
        email CHAR(30) NOT NULL,
        isAnonymous BOOLEAN,
        PRIMARY KEY(id),
        CONSTRAINT conEmail UNIQUE(email)
);
    
select * from users;

INSERT INTO users (username, about, name, email, isAnonymous) VALUES ('user1','The Best','Miki','hoh@mi.com', 0);
INSERT INTO users (username, about, name, email, isAnonymous) VALUES ('John','I like','Born','as@fd.com', 1);
INSERT INTO users (username, about, name, email, isAnonymous) VALUES ('Gorn','Nope','Toul','hhhh@ya.com', 0);
INSERT INTO users (username, about, name, email, isAnonymous) VALUES ('Boni','The Best','Robert','golmh@gog.com', 1);
INSERT INTO users (username, about, name, email, isAnonymous) VALUES ('user1','Ok','Stive','hoh@google.com', 0);