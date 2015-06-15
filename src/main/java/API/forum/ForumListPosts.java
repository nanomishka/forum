package API.forum;

import API.post.Post;
import API.thread.*;
import API.thread.Thread;
import API.user.User;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForumListPosts extends HttpServlet {
    private Connection myConn;
    public ForumListPosts(Connection myConnect) { myConn = myConnect; }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        String short_name = request.getParameter("forum");
        String limit = request.getParameter("limit");
        String order = request.getParameter("order");
        String since = request.getParameter("since");
        String related[] = request.getParameterValues("related");
        Boolean reluser=false, relforum=false, relthread=false;
        if (related != null)
            for (int i=0; i< related.length; i++){
                if (related[i].equals("user")) reluser=true;
                if (related[i].equals("forum")) relforum=true;
                if (related[i].equals("thread")) relthread=true;
            }
        String id = "";


        try {
            if (short_name == null) throw new JSONException("Arguments failed");

            String sqlQuery = "SELECT DISTINCT p.id as id, p.user, p.forum, thread " +
                    "FROM forums f JOIN posts p ON p.forum=f.id " +
                    "WHERE short_name='"+ short_name + "'";
            if (since != null) sqlQuery += " and p.date>='"+since+"' ";
            sqlQuery += " ORDER BY p.date " + (order != null && order.equals("asc")?"ASC":"DESC");
            if (limit != null) sqlQuery += " LIMIT " + limit;

            Statement myStmt = myConn.createStatement();
            ResultSet myRes = myStmt.executeQuery(sqlQuery);
            if (myRes == null) throw new SQLException("ResultSet is null");

            List<Object> posts = new ArrayList<>();
            while (myRes.next()) {
                Map<String, Object> post =
                        new Post(myConn, Integer.valueOf(myRes.getString("id"))).getDetails();
                if (reluser) post.put("user",
                        new User(myConn, Integer.valueOf(myRes.getString("user"))).getDetails());
                if (relforum) post.put("forum",
                        new Forum(myConn, Integer.valueOf(myRes.getString("forum"))).getDetails());
                if (relthread) post.put("thread",
                        new Thread(myConn, Integer.valueOf(myRes.getString("thread"))).getDetails());
                posts.add(post);
            }
            myRes.close();
            myStmt.close();

            jsonResponse.put("code", 0);
            jsonResponse.put("response", posts);

        } catch (SQLException | JSONException e) {
            try {
                if (e instanceof SQLException) {
                    jsonResponse.put("code", 1);
                    jsonResponse.put("response", "Object is not exist");
                } else {
                    jsonResponse.put("code", 2);
                    jsonResponse.put("response", "Arguments are not correct");
                }
            } catch (JSONException ex) {}

            System.out.println("Error: " + e.getMessage());
        }
        response.getWriter().println(jsonResponse);
    }
}