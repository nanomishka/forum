package API.thread;

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
public class ThreadCreate extends HttpServlet {
    private Connection myConn;
    public ThreadCreate(Connection myConnect) { myConn = myConnect; }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        JSONObject jsonData = GETdata.getInstance().getData(request);

        try{
            Thread thread = new Thread(-1,
                    jsonData.getString("forum"),
                    jsonData.getString("user"),
                    jsonData.getString("title"),
                    jsonData.getString("message"),
                    jsonData.getString("date"),
                    jsonData.getString("slug"),
                    jsonData.getString("isClosed"),
                    jsonData.getString("isClosed"));

            PreparedStatement myStmt = myConn.prepareStatement("INSERT INTO threads " +
                    "SELECT null, " +
                    "(SELECT id FROM forums WHERE short_name = ?) as forum, ?, ?, ?," +
                    "(SELECT id FROM users WHERE email = ?) as user, ?, ?, ? FROM DUAL;",
                    Statement.RETURN_GENERATED_KEYS);
            myStmt.setString(1, thread.forum);
            myStmt.setString(2, thread.title);
            myStmt.setBoolean(3, thread.isClosed);
            myStmt.setBoolean(4, thread.isDeleted);
            myStmt.setString(5, thread.user);
            myStmt.setString(6, thread.date);
            myStmt.setString(7, thread.message);
            myStmt.setString(8, thread.slug);
            myStmt.executeUpdate();

            ResultSet RSid = myStmt.getGeneratedKeys();
            try {
                RSid.first();
                thread.setId(RSid.getInt(1));
            } catch (NullPointerException e) {
            } finally { RSid.close(); }
            myStmt.close();

            jsonResponse.put("code", 0);
            jsonResponse.put("response", thread.getDetails());

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
