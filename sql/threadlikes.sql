DROP TABLE threadlikes;
CREATE TABLE
    threadlikes (
		id int NOT NULL AUTO_INCREMENT,
        thread int NOT NULL,
        tlike BOOLEAN,
        PRIMARY KEY(id),
        FOREIGN KEY (thread) REFERENCES threads(id)
);

select * from threadlikes;

SELECT t.id, t.forum, title, t.isClosed, t.isDeleted, t.date, t.message, t.user, slug, short_name, email, COALESCE(count(distinct p.id),0) as posts,COALESCE(count( distinct th.id),0) as points,COALESCE(sum(th.tlike),0) as likes FROM threads t JOIN  forums f on t.forum=f.id JOIN  users u on t.user=u.id LEFT JOIN threadlikes th ON th.thread = t.id LEFT JOIN posts p ON p.thread=t.id WHERE t.id=4 GROUP by t.id;

SELECT COALESCE(sum(tlike),0) as likes, COALESCE(count(*)-sum(tlike),0) as dislikes FROM threadlikes WHERE thread=2;