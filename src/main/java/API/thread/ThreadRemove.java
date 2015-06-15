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
public class ThreadRemove extends HttpServlet {
    private Connection myConn;
    public ThreadRemove(Connection myConnect) { myConn = myConnect; }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Query: Thread remove");
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        JSONObject jsonData = GETdata.getInstance().getData(request);

        try{
            PreparedStatement myStmt = myConn.prepareStatement("SELECT * FROM threads WHERE id = ?");
            myStmt.setString(1, jsonData.getString("thread"));
            ResultSet myRes = myStmt.executeQuery();

            if (!myRes.first()) {
                myRes.close();
                throw new SQLException("Result is null");
            }
            myStmt.close();

            myStmt = myConn.prepareStatement("UPDATE threads SET isDeleted = true WHERE id = ?");
            myStmt.setString(1, jsonData.getString("thread"));
            myStmt.executeUpdate();
            myStmt.close();

            myStmt = myConn.prepareStatement("UPDATE posts SET isDeleted = true WHERE thread = ?");
            myStmt.setInt(1, Integer.valueOf(jsonData.getString("thread")));
            myStmt.executeUpdate();
            myStmt.close();

            Map<String, Object> responseMap =  new HashMap<>();
            jsonResponse.put("code", 0);
            responseMap.put("thread", Integer.valueOf(jsonData.getString("thread")));
            jsonResponse.put("response", responseMap);

        } catch (JSONException | SQLException e) {
            try {
                if (e instanceof SQLException) {
                    jsonResponse.put("code", 5);
                    jsonResponse.put("response", "Thread is exist");
                } else {
                    jsonResponse.put("code", 2);
                    jsonResponse.put("response", "JSON is not correct");
                }
            } catch (JSONException ex) {};

            System.out.println(e.getMessage());
        }
        response.getWriter().println(jsonResponse);
    }
}
