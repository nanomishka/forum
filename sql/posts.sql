DROP TABLE posts;
CREATE TABLE
    posts (
		id int NOT NULL AUTO_INCREMENT,
        forum int NOT NULL,
        thread INT NOT NULL,
        user int NOT NULL,
        message CHAR(120),
        isApproved BOOLEAN DEFAULT FALSE,
        isDeleted BOOLEAN DEFAULT FALSE,
        isEdited BOOLEAN DEFAULT FALSE,
        isHighlighted BOOLEAN DEFAULT FALSE,
		isSpam BOOLEAN DEFAULT FALSE,
		date datetime,
        parent CHAR(40),
        PRIMARY KEY(id),
        FOREIGN KEY (forum) REFERENCES forums(id),
        FOREIGN KEY (user) REFERENCES users(id),
        FOREIGN KEY (thread) REFERENCES threads(id)
);

select * from posts;

SELECT p.id, forum, thread, user, message, isApproved, isDeleted, isEdited, isHighlighted, isSpam, date, parent, email,  short_name FROM posts p
JOIN users u ON p.user=u.id
JOIN forums f ON p.forum=f.id
WHERE p.id=1;

SELECT DISTINCT p.id as id FROM forums f JOIN posts p ON p.forum=f.id WHERE short_name='forumwithsufficientlylargename' and p.date>='2014-01-02 00:00:00'  ORDER BY p.date ASC LIMIT 2;