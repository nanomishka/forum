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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nano on 18.03.2015.
 */
public class PostRestore extends HttpServlet {
    private Connection myConn;
    public PostRestore(Connection myConnect) { myConn = myConnect; }

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

                    String sqlQuery = "UPDATE posts SET isDeleted=false WHERE id="+jsonObject.getString("post");
                    System.out.println(sqlQuery);
                    myStmt.executeUpdate(sqlQuery);
                    myStmt.close();

                    jsonResponse.put("code", 0);
                    responseMap.put("post", Integer.valueOf(jsonObject.getString("post")));
                    jsonResponse.put("response", responseMap);

                } catch (SQLException e) {
                    if (e.getErrorCode() == 0) {
                        jsonResponse.put("code", 3);
                        jsonResponse.put("response", "Thread is not exist");
                    }
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
