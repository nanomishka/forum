/*DROP TABLE forums*/
CREATE TABLE
    forums (
		id int NOT NULL AUTO_INCREMENT,
		name CHAR(80),
        short_name CHAR(40) UNIQUE,
        userId int NOT NULL,
        PRIMARY KEY(id),
        FOREIGN KEY (userId) REFERENCES Users(id)
);


    
select f.id, f.name, short_name, email from forums f join users u on f.userId = u.id;

