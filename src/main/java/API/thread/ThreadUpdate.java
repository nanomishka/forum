package API.thread;

import API.GETdata;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nano on 18.03.2015.
 */
public class ThreadUpdate extends HttpServlet {
    private Connection myConn;
    public ThreadUpdate(Connection myConnect) { myConn = myConnect; }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Query: Thread update");
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        JSONObject jsonData = GETdata.getInstance().getData(request);

        try {
            PreparedStatement myStmt = myConn.prepareStatement("SELECT * FROM threads WHERE id = ?");
            myStmt.setString(1, jsonData.getString("thread"));
            ResultSet myRes = myStmt.executeQuery();

            if ( !myRes.first() ) {
                myRes.close();
                throw new SQLException("Result is null");
            }
            myRes.close();

            myStmt = myConn.prepareStatement("UPDATE threads SET message = ?, slug = ? WHERE id = ?");
            myStmt.setString(1, jsonData.getString("message"));
            myStmt.setString(2, jsonData.getString("slug"));
            myStmt.setInt(3, Integer.valueOf(jsonData.getString("thread")));
            myStmt.executeUpdate();
            myStmt.close();

            jsonResponse.put("code", 0);
            jsonResponse.put("response", new Thread(myConn, Integer.valueOf(jsonData.getString("thread"))).getDetails());

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
