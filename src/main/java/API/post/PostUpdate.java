package API.post;

import API.GETdata;
import API.thread.*;
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

/**
 * Created by nano on 18.03.2015.
 */
public class PostUpdate extends HttpServlet {
    private Connection myConn;
    public PostUpdate(Connection myConnect) { myConn = myConnect; }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        System.out.println(this.getClass());
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        JSONObject jsonData = GETdata.getInstance().getData(request);
        int id;

        try {
            id = Integer.valueOf(jsonData.getString("post"));
            PreparedStatement myStmt = myConn.prepareStatement("SELECT * FROM posts WHERE id = ?");
            myStmt.setInt(1, id);
            ResultSet myRes = myStmt.executeQuery();

            if ( !myRes.first() ) {
                myRes.close();
                myStmt.close();
                throw new SQLException("Result is null");
            }
            myRes.close();

            myStmt = myConn.prepareStatement("UPDATE posts SET message = ? WHERE id = ?");
            myStmt.setString(1, jsonData.getString("message"));
            myStmt.setInt(2, id);
            myStmt.executeUpdate();
            myStmt.close();

            jsonResponse.put("code", 0);
            jsonResponse.put("response", new Post(myConn, id).getDetails());

        } catch (JSONException | SQLException e) {
            try {
                if (e instanceof SQLException) {
                    jsonResponse.put("code", 3);
                    jsonResponse.put("response", "Query is not valid");
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
