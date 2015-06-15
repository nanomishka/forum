package API.forum;

import API.GETdata;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

/**
 * Created by nano on 18.03.2015.
 */
public class ForumCreate extends HttpServlet {
    private Connection myConn;
    public ForumCreate(Connection myConnect) { myConn = myConnect; }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        JSONObject jsonData = GETdata.getInstance().getData(request);

        try{
            Forum forum = new Forum(-1,
                    jsonData.getString("name"),
                    jsonData.getString("short_name"),
                    jsonData.getString("user"));

            PreparedStatement myStmt = myConn.prepareStatement("INSERT INTO forums " +
                            "SELECT null, ?, ?, id FROM users WHERE email = ?;", Statement.RETURN_GENERATED_KEYS);
            myStmt.setString(1, forum.name);
            myStmt.setString(2, forum.shortName);
            myStmt.setString(3, forum.user);
            myStmt.executeUpdate();

            ResultSet RSid = myStmt.getGeneratedKeys();
            try {
                RSid.first();
                int id = RSid.getInt(1);
                forum.setId(id);
            } catch (NullPointerException e) {
            } finally { RSid.close(); }
            myStmt.close();

            jsonResponse.put("code", 0);
            jsonResponse.put("response", forum.getDetails());

        } catch (JSONException | SQLException e) {
            try {
                if (e instanceof SQLException) {
                    jsonResponse.put("code", 3);
                    jsonResponse.put("response", "Object is exist");
                } else {
                    jsonResponse.put("code", 2);
                    jsonResponse.put("response", "JSON is not correct");
                }
            } catch (JSONException ex) {};

            System.out.println("Error: " + e.getMessage());
        }
        response.getWriter().println(jsonResponse);
    }
}
