package API.user;

import API.post.Post;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserListPosts extends HttpServlet {
    private Connection myConn;
    public UserListPosts(Connection myConnect) { myConn = myConnect; }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        System.out.println(this.getClass());
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        String email = request.getParameter("user");
        String limit = request.getParameter("limit");
        String order = request.getParameter("order");
        String since = request.getParameter("since");


        try {
            if (email == null) throw new JSONException("Arguments failed");

            String sqlQuery = "SELECT p.id FROM posts p " +
                    "JOIN users u ON p.user = u.id " +
                    "WHERE email = '" + email + "'";
            if (since != null) sqlQuery += " and p.date>='" + since + "' ";
            sqlQuery += " GROUP BY p.id  ORDER BY p.date " + (order != null && order.equals("asc") ? "ASC" : "DESC");
            if (limit != null) sqlQuery += " LIMIT " + limit;

            Statement myStmt = myConn.createStatement();
            ResultSet myRes = myStmt.executeQuery(sqlQuery);
            if (myRes == null) throw new SQLException("ResultSet is null");

            List<Object> posts = new ArrayList<>();
            while (myRes.next()) {
                posts.add(new Post(myConn, Integer.valueOf(myRes.getString("id"))).getDetails());
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




//                    String sqlQuery =
//                            "SELECT p.id, forum, thread,  message, isApproved, " +
//                                    "isDeleted, isEdited, isHighlighted, isSpam, " +
//                                    "date, parent, email,  short_name, COALESCE(count(tlike),0) as clike, " +
//                                    "COALESCE(sum(tlike),0) as likes FROM posts p " +
//                                    "JOIN users u ON p.user=u.id " +
//                                    "LEFT JOIN forums f ON p.forum=f.id "+
//                                    "LEFT JOIN postlikes pl ON p.id = pl.post WHERE "+
//                                    "email='" + user +"' ";
//                    if (since != null) sqlQuery += " and p.date>='"+since+"' ";
//                    sqlQuery += " GROUP BY p.id  ORDER BY p.date " + (order != null && order.equals("asc")?"ASC":"DESC");
//                    if (limit != null) sqlQuery += " LIMIT " + limit;
//                    System.out.println(sqlQuery);
//                    ResultSet myRes = myStmt.executeQuery(sqlQuery);
//                    jsonResponse.put("code", 0);
//                    while ( myRes != null && myRes.next()) {
//                        Map<String, Object> responseMap =  new HashMap<>();
//                        Integer idPost = Integer.valueOf(myRes.getString("id"));
//                        responseMap.put("date", myRes.getString("date").substring(0, 19));
//                        responseMap.put("id", idPost);
//                        responseMap.put("isApproved", myRes.getString("isApproved").equals("0")?false:true);
//                        responseMap.put("isHighlighted", myRes.getString("isHighlighted").equals("0")?false:true);
//                        responseMap.put("isSpam", myRes.getString("isSpam").equals("0")?false:true);
//                        responseMap.put("isEdited", myRes.getString("isEdited").equals("0")?false:true);
//                        responseMap.put("isDeleted", myRes.getString("isDeleted").equals("0")?false:true);
//                        responseMap.put("message", myRes.getString("message"));
//                        responseMap.put("user", myRes.getString("email"));
//                        responseMap.put("forum", myRes.getString("short_name"));
//                        responseMap.put("parent", myRes.getString("parent") != null?
//                                Integer.valueOf(myRes.getString("parent")):null);
//                        responseMap.put("thread", Integer.valueOf(myRes.getString("thread")));
//
//                        Integer likes = Integer.valueOf(myRes.getString("likes"));
//                        Integer clike = Integer.valueOf(myRes.getString("clike"));
//
//                        responseMap.put("likes", likes);
//                        responseMap.put("dislikes", clike-likes);
//                        responseMap.put("points", 2*likes-clike);
//                        posts.add(responseMap);
