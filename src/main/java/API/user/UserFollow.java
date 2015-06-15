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

public class UserFollow extends HttpServlet {
    private Connection myConn;
    public UserFollow(Connection myConnect) { myConn = myConnect; }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        JSONObject jsonData = GETdata.getInstance().getData(request);
        String id1, id2;
        id1 = id2 = null;

        try{
            PreparedStatement myStmt = myConn.prepareStatement("SELECT id FROM users WHERE email = ?;");
            myStmt.setString(1, jsonData.getString("follower"));
            ResultSet myRes = myStmt.executeQuery();
            if (myRes != null && myRes.first()) id1 = myRes.getString("id");
            myRes.close();

            myStmt.setString(1, jsonData.getString("followee"));
            myRes = myStmt.executeQuery();
            if (myRes != null && myRes.first()) id2 = myRes.getString("id");
            myRes.close();
            myStmt.close();

            if ( id1 == null || id2 == null ) throw new SQLException();
            else {
                myStmt = myConn.prepareStatement("INSERT INTO follows VALUE (null, ?, ?);");
                myStmt.setInt(1, Integer.valueOf(id1));
                myStmt.setInt(2, Integer.valueOf(id2));
                myStmt.executeUpdate();

                jsonResponse.put("code", 0);
                jsonResponse.put("response", new User(myConn, jsonData.getString("follower")).getDetails());
            }
        } catch (SQLException | JSONException  e) {
            try {
                if (e instanceof SQLException) {
                    jsonResponse.put("code", 1);
                    jsonResponse.put("response", "Object is not exist");
                } else {
                    jsonResponse.put("code", 2);
                    jsonResponse.put("response", "Arguments are not correct");
                }
            } catch (JSONException ex) {};

            System.out.println("Error: " + e.getMessage());
        }
        response.getWriter().println(jsonResponse);
    }
}
