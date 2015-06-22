package API.user;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

import API.GETdata;
import org.json.JSONException;
import org.json.JSONObject;

public class UserCreate extends HttpServlet {
    private Connection myConn;
    public UserCreate(Connection myConnect){ myConn = myConnect; }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        System.out.println(this.getClass());
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        JSONObject jsonData = GETdata.getInstance().getData(request);

        try{
            if (!jsonData.has("isAnonymous")) jsonData.put("isAnonymous", "false");
            User user = new User(-1,
                    jsonData.getString("username"),
                    jsonData.getString("about"),
                    jsonData.getString("name"),
                    jsonData.getString("email"),
                    jsonData.getString("isAnonymous"));

            PreparedStatement myStmt = myConn.prepareStatement("INSERT INTO users VALUES (null, ?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            myStmt.setString(1, user.username);
            myStmt.setString(2, user.about);
            myStmt.setString(3, user.name);
            myStmt.setString(4, user.email);
            myStmt.setBoolean(5, user.isAnonymous);
            myStmt.executeUpdate();

            ResultSet RSid = myStmt.getGeneratedKeys();
            try {
                RSid.first();
                int id = RSid.getInt(1);
                user.setId(id);
            } catch (NullPointerException e) {
            } finally { RSid.close(); }
            myStmt.close();

            jsonResponse.put("code", 0);
            jsonResponse.put("response", user.getDetails());
        } catch (JSONException | SQLException e) {
            try {
                if (e instanceof SQLException) {
                    jsonResponse.put("code", 5);
                    jsonResponse.put("response", "User with email: " + jsonData.getString("email") + " is exist");
                } else {
                    jsonResponse.put("code", 2);
                    jsonResponse.put("response", "JSON is not correct");
                }
            } catch (JSONException ex) {};

            //System.out.println("Error: " + e.getMessage());
        }

        response.getWriter().println(jsonResponse);
    }
}