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

public class UserUpdate extends HttpServlet {
    private Connection myConn;
    public UserUpdate(Connection myConnect) { myConn = myConnect; }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        System.out.println(this.getClass());
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        JSONObject jsonData = GETdata.getInstance().getData(request);

        try{
            PreparedStatement myStmt = myConn.prepareStatement("UPDATE users SET about = ?, name = ? WHERE email = ?");
            myStmt.setString(1, jsonData.getString("about"));
            myStmt.setString(2, jsonData.getString("name"));
            myStmt.setString(3, jsonData.getString("user"));
            myStmt.executeUpdate();
            myStmt.close();

            jsonResponse.put("code", 0);
            jsonResponse.put("response", new User(myConn, jsonData.getString("user")).getDetails());
        } catch (JSONException | SQLException e) {
            try {
                if (e instanceof SQLException) {
                    jsonResponse.put("code", 1);
                    jsonResponse.put("response", "Object is not exist");
                } else {
                    jsonResponse.put("code", 2);
                    jsonResponse.put("response", "JSON is not correct");
                }
            } catch (JSONException ex) {}

            System.out.println("Error: " + e.getMessage());
        }
        response.getWriter().println(jsonResponse);
    }
}
