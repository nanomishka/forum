INSERT INTO users VALUES (null, 'nano','OUPS','Tred','example@mail.ru', 1);
INSERT INTO users VALUES (null, 'user19','The Best2','Miki3','hoho@mi.com', 0);
INSERT INTO users VALUES (null,'John','I like','Born','as@fd.com', 1);
INSERT INTO users VALUES (null,'Gorn','Nope','Toul','hhhh@ya.com', 0);
INSERT INTO users VALUES (null,'Boni','The Best','Robert','golmh@gog.com', 1);
INSERT INTO users VALUES (null,'user1','Ok','Stive','hoh@google.com', 0);


INSERT INTO forums VALUES (null, "The text", "my", 1);
INSERT INTO forums VALUES (null, "British", "speach", 2);
INSERT INTO forums VALUES (null, "Conclusions", "the best", 1);
INSERT INTO forums VALUES (null, "The Harvard home is crashed", "blue dream", 3);
INSERT INTO forums VALUES (null, "The dreams of live", "dreams", 3);
INSERT INTO forums VALUES (null, "Stop it", "fairs", 2);
INSERT INTO forums VALUES (null, "Forum", "forum", 5);

INSERT INTO follows VALUES (null, 1, 2);
INSERT INTO follows VALUES (null, 1, 5);
INSERT INTO follows VALUES (null, 2, 4);
INSERT INTO follows VALUES (null, 3, 1);
INSERT INTO follows VALUES (null, 2, 1);
INSERT INTO follows VALUES (null, 1, 3);
INSERT INTO follows VALUES (null, 1, 4);
INSERT INTO follows VALUES (null, 5, 1);
INSERT INTO follows VALUES (null, 6, 1);
INSERT INTO follows VALUES (null, 4, 1);

INSERT INTO threads SELECT null, 2 as forum,'Discuss',false,false, 5 as user, '2014-01-01 00:00:01','opopop','undestand words' FROM DUAL;
INSERT INTO threads SELECT null, 1 as forum,'Speaking',true,false, 1 as user, '2014-02-01 00:00:01','hello','jojo' FROM DUAL;
INSERT INTO threads SELECT null, 1 as forum,'Looks',false,true, 1 as user, '2014-06-01 00:00:01','oups','news' FROM DUAL;
INSERT INTO threads SELECT null, 4 as forum,'Draft',false,true, 3 as user, '2014-07-01 00:00:01','bobo','perect' FROM DUAL;
INSERT INTO threads SELECT null, 3 as forum,'Lets',true,false, 2 as user, '2014-08-01 00:00:01','polofo','anybody' FROM DUAL;
INSERT INTO threads SELECT null, 2 as forum,'Sing',false,false, 3 as user, '2014-09-01 00:00:01','krop','loooks' FROM DUAL;
INSERT INTO threads SELECT null, 5 as forum,'About',true,true, 5 as user, '2014-10-01 00:00:01','ololo','mi mi mi music' FROM DUAL;

INSERT INTO subscribes VALUE (null,1,1);
INSERT INTO subscribes VALUE (null,1,5);
INSERT INTO subscribes VALUE (null,2,7);
INSERT INTO subscribes VALUE (null,5,1);
INSERT INTO subscribes VALUE (null,3,4);
INSERT INTO subscribes VALUE (null,3,1);

INSERT INTO threadlikes VALUE (null,1,1);
INSERT INTO threadlikes VALUE (null,2,-1);
INSERT INTO threadlikes VALUE (null,5,1);
INSERT INTO threadlikes VALUE (null,4,1);
INSERT INTO threadlikes VALUE (null,3,-1);
INSERT INTO threadlikes VALUE (null,3,-1);
INSERT INTO threadlikes VALUE (null,3,1);
INSERT INTO threadlikes VALUE (null,4,1);
INSERT INTO threadlikes VALUE (null,7,-1);
INSERT INTO threadlikes VALUE (null,5,-1);
INSERT INTO threadlikes VALUE (null,6,1);
INSERT INTO threadlikes VALUE (null,3,1);
INSERT INTO threadlikes VALUE (null,3,-1);
INSERT INTO threadlikes VALUE (null,1,1);
INSERT INTO threadlikes VALUE (null,7,1);
INSERT INTO threadlikes VALUE (null,1,-1);
INSERT INTO threadlikes VALUE (null,1,1);
INSERT INTO threadlikes VALUE (null,3,1);
