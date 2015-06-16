package API;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Create extends HttpServlet {
    private Connection myConn;
    public Create(Connection myConnect) { myConn = myConnect; }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        try {
            Statement myStmt = myConn.createStatement();
            response.setContentType("application/json;charset=utf-8");
            JSONObject jsonResponse = new JSONObject();

            try {
                myStmt.executeUpdate("DROP TABLE IF EXISTS postlikes");
                myStmt.executeUpdate("DROP TABLE IF EXISTS threadlikes");
                myStmt.executeUpdate("DROP TABLE IF EXISTS posts");
                myStmt.executeUpdate("DROP TABLE IF EXISTS subscribes");
                myStmt.executeUpdate("DROP TABLE IF EXISTS threads");
                myStmt.executeUpdate("DROP TABLE IF EXISTS forums");
                myStmt.executeUpdate("DROP TABLE IF EXISTS follows");
                myStmt.executeUpdate("DROP TABLE IF EXISTS users");
                myStmt.executeUpdate("CREATE TABLE users (" +
                        "id int NOT NULL AUTO_INCREMENT," +
                        "username CHAR(30)," +
                        "about TEXT," +
                        "name CHAR(80)," +
                        "email CHAR(30) NOT NULL UNIQUE," +
                        "isAnonymous BOOLEAN," +
                        "PRIMARY KEY(id));");
                myStmt.executeUpdate("CREATE TABLE forums (" +
                        "id int NOT NULL AUTO_INCREMENT," +
                        "name CHAR(80) UNIQUE," +
                        "short_name CHAR(40) UNIQUE," +
                        "userId int NOT NULL," +
                        "PRIMARY KEY(id)," +
                        "FOREIGN KEY (userId) REFERENCES users(id));");
                myStmt.executeUpdate("CREATE TABLE follows (" +
                        "id int NOT NULL AUTO_INCREMENT," +
                        "id1 int," +
                        "id2 int," +
                        "PRIMARY KEY(id)," +
                        "FOREIGN KEY (id1) REFERENCES users(id)," +
                        "FOREIGN KEY (id2) REFERENCES users(id)," +
                        "UNIQUE (id1, id2));");
                myStmt.executeUpdate("CREATE TABLE threads (" +
                        "id int NOT NULL AUTO_INCREMENT," +
                        "forum int NOT NULL," +
                        "title CHAR(80)," +
                        "isClosed BOOLEAN," +
                        "isDeleted BOOLEAN DEFAULT FALSE," +
                        "user int NOT NULL," +
                        "date datetime," +
                        "message TEXT," +
                        "slug CHAR(40)," +
                        "PRIMARY KEY(id)," +
                        "FOREIGN KEY (forum) REFERENCES forums(id)," +
                        "FOREIGN KEY (user) REFERENCES users(id)" +
                        ");");
                myStmt.executeUpdate("CREATE TABLE subscribes (" +
                        "id int NOT NULL AUTO_INCREMENT," +
                        "user int," +
                        "thread int," +
                        "PRIMARY KEY(id)," +
                        "FOREIGN KEY (user) REFERENCES users(id)," +
                        "FOREIGN KEY (thread) REFERENCES threads(id)," +
                        "UNIQUE (user, thread) )");
                myStmt.executeUpdate("CREATE TABLE threadlikes (" +
                        "id int NOT NULL AUTO_INCREMENT," +
                        "thread int NOT NULL," +
                        "tlike BOOLEAN," +
                        "PRIMARY KEY(id)," +
                        "FOREIGN KEY (thread) REFERENCES threads(id) );");
                myStmt.executeUpdate("CREATE TABLE posts (" +
                        "id int NOT NULL AUTO_INCREMENT," +
                        "forum int NOT NULL," +
                        "thread INT NOT NULL," +
                        "user int NOT NULL," +
                        "message TEXT," +
                        "isApproved BOOLEAN DEFAULT FALSE," +
                        "isDeleted BOOLEAN DEFAULT FALSE," +
                        "isEdited BOOLEAN DEFAULT FALSE," +
                        "isHighlighted BOOLEAN DEFAULT FALSE," +
                        "isSpam BOOLEAN DEFAULT FALSE," +
                        "date datetime," +
                        "parent CHAR(40)," +
                        "PRIMARY KEY(id)," +
                        "FOREIGN KEY (forum) REFERENCES forums(id)," +
                        "FOREIGN KEY (user) REFERENCES users(id)," +
                        "FOREIGN KEY (thread) REFERENCES threads(id));");
                myStmt.executeUpdate("CREATE TABLE postlikes (" +
                        "id int NOT NULL AUTO_INCREMENT," +
                        "post int NOT NULL," +
                        "tlike BOOLEAN," +
                        "PRIMARY KEY(id)," +
                        "FOREIGN KEY (post) REFERENCES posts(id));");
                myStmt.close();

                System.out.println("Successfully created");
                jsonResponse.put("code", 0);
                jsonResponse.put("response", "OK");
            } catch (Exception e) {
                System.out.println("Could not create" + e.getMessage());
            }
            response.getWriter().println(jsonResponse);
        } catch (SQLException e) {
            System.out.println("error");
        }
    }
}
