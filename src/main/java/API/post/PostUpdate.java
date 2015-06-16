package API.post;

import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        response.setContentType("application/json;charset=utf-8");
        StringBuffer buffer = new StringBuffer();
        String line = null;
        JSONObject jsonResponse = new JSONObject();
        JSONObject jsonObject = null;

        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null)
            buffer.append(line);

        try{
            try {
                jsonObject = new JSONObject(buffer.toString());
                Map<String, Object> responseMap =  new HashMap<>();

                try {
                    Statement myStmt = myConn.createStatement();
                    ResultSet myRes = myStmt.executeQuery("SELECT * FROM posts WHERE id="+jsonObject.getString("post"));
                    if (!myRes.first()) throw new SQLException("Result is null");

                    String sqlQuery = "UPDATE posts SET " +
                            " message = '"+jsonObject.getString("message")+"',"+
                            " WHERE id = '"+jsonObject.getString("post")+"'";

                    //System.out.println(sqlQuery);
                    myStmt.executeUpdate(sqlQuery);

                    sqlQuery =
                            "SELECT p.id, forum, thread, user, message, isApproved, isDeleted, isEdited, " +
                                    "isHighlighted, isSpam, date, parent, email,  short_name FROM posts p\n" +
                                    "JOIN users u ON p.user=u.id " +
                                    "JOIN forums f ON p.forum=f.id " +
                                    "WHERE p.id="+jsonObject.getString("post");
                    //System.out.println(sqlQuery);
                    myRes = myStmt.executeQuery(sqlQuery);
                    if (!myRes.first()) throw new SQLException("Result is null");
                    jsonResponse.put("code", 0);
                    responseMap.put("date", myRes.getString("date").substring(0, 19));

                    responseMap.put("isApproved", myRes.getString("isApproved").equals("0")?false:true);
                    responseMap.put("isDeleted", myRes.getString("isDeleted").equals("0")?false:true);
                    responseMap.put("isEdited",myRes.getString("isEdited").equals("0")?false:true);
                    responseMap.put("isHighlighted", myRes.getString("isHighlighted").equals("0")?false:true);
                    responseMap.put("isSpam", myRes.getString("isSpam").equals("0")?false:true);
                    responseMap.put("parent", myRes.getString("parent") != null?
                            Integer.valueOf(myRes.getString("parent")):null);
                    responseMap.put("message", myRes.getString("message"));
                    responseMap.put("id", Integer.valueOf(myRes.getString("id")));

                    String email = myRes.getString("email");
                    String short_name = myRes.getString("short_name");
                    String thread = myRes.getString("thread");
                    myRes.close();
                    myStmt.close();

                    responseMap.put("user", email);
                    responseMap.put("forum", short_name);
                    responseMap.put("thread", thread);

                    jsonResponse.put("response", responseMap);

                } catch (SQLException e) {
                        jsonResponse.put("code", 3);
                        jsonResponse.put("response", "Query is not valid");
                    System.out.println(e.getMessage());
                }
            } catch (JSONException e) {
                jsonResponse.put("code", 2);
                jsonResponse.put("response", "JSON is not correct");
                System.out.println(e.getMessage());
            }
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
        response.getWriter().println(jsonResponse);
    }
}
