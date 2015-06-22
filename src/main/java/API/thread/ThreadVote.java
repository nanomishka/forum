package API.thread;

import API.GETdata;
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
import java.util.HashMap;
import java.util.Map;

public class ThreadVote extends HttpServlet {
    private Connection myConn;
    public ThreadVote(Connection myConnect) { myConn = myConnect; }

    public void doPost(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        System.out.println(this.getClass());
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        JSONObject jsonData = GETdata.getInstance().getData(request);
        String thread= "";

        try {
            Boolean tvote = jsonData.getString("vote").equals("1")? true : false;

            PreparedStatement myStmt = myConn.prepareStatement("SELECT id FROM threads WHERE id = ?");
            myStmt.setString(1, jsonData.getString("thread"));
            ResultSet myRes = myStmt.executeQuery();

            if (myRes != null && myRes.first()) {
                thread = myRes.getString("id");
            } else {
                myRes.close();
                throw new SQLException("Result is null");
            }
            myRes.close();
            myStmt.close();

            myStmt = myConn.prepareStatement("INSERT INTO threadlikes VALUE (null,?,?)");
            myStmt.setInt(1, Integer.valueOf(jsonData.getString("thread")));
            myStmt.setBoolean(2, tvote);
            myStmt.executeUpdate();

            jsonResponse.put("code", 0);
            jsonResponse.put("response", new Thread(myConn, Integer.valueOf(thread)).getDetails());

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
