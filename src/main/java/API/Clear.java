package API;

import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Clear extends HttpServlet {
    private Connection myConn;
    public Clear(Connection myConnect) { myConn = myConnect;}

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        JSONObject jsonResponse = new JSONObject();
        response.setContentType("application/json;charset=utf-8");

        try {
            Statement myStmt = myConn.createStatement();
            myStmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0;");
            myStmt.executeUpdate("TRUNCATE TABLE postlikes");
            myStmt.executeUpdate("TRUNCATE TABLE threadlikes");
            myStmt.executeUpdate("TRUNCATE TABLE posts");
            myStmt.executeUpdate("TRUNCATE TABLE subscribes");
            myStmt.executeUpdate("TRUNCATE TABLE threads");
            myStmt.executeUpdate("TRUNCATE TABLE forums");
            myStmt.executeUpdate("TRUNCATE TABLE follows");
            myStmt.executeUpdate("TRUNCATE TABLE users");
            myStmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1;");
            myStmt.close();

            System.out.println("Successfully truncated");
            jsonResponse.put("code", 0);
            jsonResponse.put("response", "OK");
            response.getWriter().println(jsonResponse);

        } catch (SQLException | JSONException e) {
            System.out.println("Could not truncate: " + e.getMessage());
        }
    }
}
