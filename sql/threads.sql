SET SQL_SAFE_UPDATES = 0;
DELETE FROM threads;
TRUNCATE threads;
DROP TABLE threads;
CREATE TABLE
    threads (
		id int NOT NULL AUTO_INCREMENT,
        forum int NOT NULL,
		title CHAR(80) UNIQUE,
        isClosed BOOLEAN,
        isDeleted BOOLEAN,
        user int NOT NULL,
		date datetime,
        message CHAR(120),
        slug CHAR(40),
        PRIMARY KEY(id),
        FOREIGN KEY (forum) REFERENCES forums(id),
        FOREIGN KEY (user) REFERENCES users(id)
);

SELECT * from threads;

SELECT t.id, forum, title, isClosed, isDeleted, date, message, user, slug, short_name, email
, coalesce(sum(tlike),0) as likes
/*, count(*)-sum(tlike) as dislikes */
FROM threads t JOIN  forums f on t.forum=f.id JOIN  users u on t.user=u.id LEFT JOIN threadlikes th ON th.thread = t.id WHERE t.id=3 
/*GROUP by t.id*/
;




SELECT t.id, forum, title, isClosed, isDeleted, 
date, message, user, slug, short_name, email,
COALESCE(sum(tlike),0) as likes, COALESCE((count(*)-sum(tlike)),0) as dislikes 
FROM threads t JOIN  forums f on t.forum=f.id 
JOIN  users u on t.user=u.id 
LEFT JOIN threadlikes th ON th.thread = t.id WHERE  GROUP BY t.id;

