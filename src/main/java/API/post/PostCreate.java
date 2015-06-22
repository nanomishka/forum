package API.post;

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
public class PostCreate extends HttpServlet {
    private Connection myConn;
    public PostCreate(Connection myConnect) { myConn = myConnect; }


    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        System.out.println(this.getClass());
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        JSONObject jsonData = GETdata.getInstance().getData(request);

        try{
            if (!jsonData.has("parent")) jsonData.put("parent", "null");
            Post post = new Post(-1,
                    jsonData.getString("forum"),
                    jsonData.getString("user"),
                    jsonData.getString("message"),
                    jsonData.getString("date"),
                    jsonData.getString("thread"),
                    jsonData.getString("isDeleted"),
                    jsonData.getString("isApproved"),
                    jsonData.getString("isEdited"),
                    jsonData.getString("isHighlighted"),
                    jsonData.getString("isSpam"),
                    jsonData.getString("parent"));


            PreparedStatement myStmt = myConn.prepareStatement("INSERT INTO posts " +
                    "SELECT null, (SELECT id FROM forums WHERE short_name = ?)," +
                    "(SELECT id FROM threads WHERE id = ?), (SELECT id FROM users WHERE email = ?)," +
                    "?, ?, ?, ?, ?, ?, ?, ? FROM DUAL;",
                    Statement.RETURN_GENERATED_KEYS);
            myStmt.setString(1, post.forum);
            myStmt.setInt(2, post.thread);
            myStmt.setString(3, post.user);
            myStmt.setString(4, post.message);
            myStmt.setBoolean(5, post.isApproved);
            myStmt.setBoolean(6, post.isDeleted);
            myStmt.setBoolean(7, post.isEdited);
            myStmt.setBoolean(8, post.isHighlighted);
            myStmt.setBoolean(9, post.isSpam);
            myStmt.setString(10, post.date);
            myStmt.setString(11, post.parent);
            myStmt.executeUpdate();

            ResultSet RSid = myStmt.getGeneratedKeys();
            try {
                RSid.first();
                post.setId(RSid.getInt(1));
            } catch (NullPointerException e) {
            } finally { RSid.close(); }
            myStmt.close();

            jsonResponse.put("code", 0);
            jsonResponse.put("response", post.getDetails());

        } catch (JSONException | SQLException e) {
            try {
                if (e instanceof SQLException) {
                    jsonResponse.put("code", 3);
                    jsonResponse.put("response", "Wrong query");
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
