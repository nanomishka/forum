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
public class ThreadSubscribe extends HttpServlet {
    private Connection myConn;
    public ThreadSubscribe(Connection myConnect) { myConn = myConnect; }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        Map<String, Object> responseMap =  new HashMap<>();
        JSONObject jsonData = GETdata.getInstance().getData(request);
        String user = "", thread = "";

        try {
            PreparedStatement myStmt = myConn.prepareStatement("SELECT id FROM users WHERE email = ?");
            myStmt.setString(1, jsonData.getString("user"));
            ResultSet myRes = myStmt.executeQuery();

            if (myRes != null && myRes.first()) {
                user = myRes.getString("id");
            } else {
                myRes.close();
                throw new SQLException("Result is null");
            }
            myRes.close();
            myStmt.close();

            myStmt = myConn.prepareStatement("SELECT id FROM threads WHERE id = ?");
            myStmt.setString(1, jsonData.getString("thread"));
            myRes = myStmt.executeQuery();

            if (myRes != null && myRes.first()) {
                thread = myRes.getString("id");
            } else {
                myRes.close();
                throw new SQLException("Result is null");
            }
            myRes.close();
            myStmt.close();

            if (user.equals("") || thread.equals("")) {
                throw new SQLException();
            } else {
                myStmt.close();
                myStmt = myConn.prepareStatement("INSERT INTO subscribes VALUE (null,?,?)");
                myStmt.setInt(1, Integer.valueOf(user));
                myStmt.setInt(2, Integer.valueOf(thread));
                myStmt.executeUpdate();
            }

            responseMap.put("thread", Integer.valueOf(jsonData.getString("thread")));
            responseMap.put("user", jsonData.getString("user"));
            jsonResponse.put("code", 0);
            jsonResponse.put("response", responseMap);

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

            System.out.println(e.getMessage());
        }
        response.getWriter().println(jsonResponse);
    }
}
