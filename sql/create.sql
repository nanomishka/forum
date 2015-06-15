CREATE TABLE users (
    id int NOT NULL AUTO_INCREMENT,
    username CHAR(30),
    about TEXT,
    name CHAR(20),
    email CHAR(30) NOT NULL UNIQUE,
    isAnonymous BOOLEAN,
    PRIMARY KEY(id)
);

CREATE TABLE forums (
    id int NOT NULL AUTO_INCREMENT,
    name CHAR(80) UNIQUE,
    short_name CHAR(40) UNIQUE,
    userId int NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY (userId) REFERENCES users(id)
);

CREATE TABLE follows (
    id int NOT NULL AUTO_INCREMENT,
    id1 int,
    id2 int,
    PRIMARY KEY(id),
    FOREIGN KEY (id1) REFERENCES users(id),
    FOREIGN KEY (id2) REFERENCES users(id),
    UNIQUE (id1, id2)
);

        
CREATE TABLE threads (
    id int NOT NULL AUTO_INCREMENT,
    forum int NOT NULL,
    title CHAR(80),
    isClosed BOOLEAN,
    isDeleted BOOLEAN DEFAULT FALSE,
    user int NOT NULL,
    date datetime,
    message TEXT,
    slug CHAR(40),
    PRIMARY KEY(id),
    FOREIGN KEY (forum) REFERENCES forums(id),
    FOREIGN KEY (user) REFERENCES users(id)
);

CREATE TABLE subscribes (
    id int NOT NULL AUTO_INCREMENT,
    user int,
    thread int,
    PRIMARY KEY(id),
    FOREIGN KEY (user) REFERENCES users(id),
    FOREIGN KEY (thread) REFERENCES threads(id),
    UNIQUE (user, thread)
);

        
CREATE TABLE threadlikes (
    id int NOT NULL AUTO_INCREMENT,
    thread int NOT NULL,
    tlike BOOLEAN,
    PRIMARY KEY(id),
    FOREIGN KEY (thread) REFERENCES threads(id)
);
            
CREATE TABLE posts (
    id int NOT NULL AUTO_INCREMENT,
    forum int NOT NULL,
    thread INT NOT NULL,
    user int NOT NULL,
    message TEXT,
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

CREATE TABLE postlikes (
    id int NOT NULL AUTO_INCREMENT,
    post int NOT NULL,
    tlike BOOLEAN,
    PRIMARY KEY(id),    