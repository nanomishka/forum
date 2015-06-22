package API.forum;

import API.user.User;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForumDetails extends HttpServlet {
    private Connection myConn;
    public ForumDetails(Connection myConnect) { myConn = myConnect; }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        System.out.println(this.getClass());
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        String short_name = request.getParameter("forum");
        String related = request.getParameter("related");

        try {
            if (short_name == null) throw new JSONException("Arguments failed");

            PreparedStatement myStmt = myConn.prepareStatement("SELECT f.id, f.name, short_name, email " +
                    "FROM forums f JOIN users u ON u.id = f.userID WHERE short_name = ?;");
            myStmt.setString(1, short_name);
            ResultSet myRes = myStmt.executeQuery();

            if (!myRes.first()) {
                myRes.close();
                throw new SQLException("Result is null");
            }

            Forum forum = new Forum(Integer.valueOf(myRes.getString("id")),
                    myRes.getString("name"),
                    myRes.getString("short_name"),
                    myRes.getString("email"));

            Map<String, Object> responseMap;
            responseMap = forum.getDetails();
            if (related != null && related.equals("user"))
                responseMap.put("user", new User(myConn, myRes.getString("email")).getDetails());
            myRes.close();
            myStmt.close();

            jsonResponse.put("code", 0);
            jsonResponse.put("response", responseMap);

        } catch (SQLException | JSONException e) {
            try {
                if (e instanceof SQLException) {
                    jsonResponse.put("code", 1);
                    jsonResponse.put("response", "Object is not exist");
                } else {
                    jsonResponse.put("code", 2);
                    jsonResponse.put("response", "Arguments are not correct");
                }
            } catch (JSONException ex) {}

            System.out.println("Error: " + e.getMessage());
        }
        response.getWriter().println(jsonResponse);
    }
}