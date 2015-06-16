package API.post;

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

public class PostList extends HttpServlet {
    private Connection myConn;
    public PostList(Connection myConnect) { myConn = myConnect; }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        String forum = request.getParameter("forum");
        String thread = request.getParameter("thread");
        String limit = request.getParameter("limit");
        String order = request.getParameter("order");
        String since = request.getParameter("since");
        String id = "";


        try{
            try {
                if ( forum==null && thread==null) throw new JSONException("Arguments failed");
                List<Object> threads = new ArrayList<>();
                try {
                    String sqlQuery =
                            "SELECT p.id, forum, thread,  message, isApproved, " +
                                    "isDeleted, isEdited, isHighlighted, isSpam, " +
                                    "date, parent, email,  short_name FROM posts p " +
                                    "JOIN users u ON p.user=u.id " +
                                    "LEFT JOIN forums f ON p.forum=f.id "+
                                    "LEFT JOIN postlikes pl ON p.id = pl.post WHERE ";
                    if (forum != null) sqlQuery += "short_name='" + forum+"' ";
                    if (thread != null) sqlQuery += "thread=" + thread+" ";
                    if (since != null) sqlQuery += " and p.date>='"+since+"' ";
                    sqlQuery += " GROUP BY p.date " + (order != null && order.equals("asc")?"ASC":"DESC");
                    if (limit != null) sqlQuery += " LIMIT " + limit;
                    //System.out.println(sqlQuery);
                    Statement myStmt = myConn.createStatement();
                    ResultSet myRes = myStmt.executeQuery(sqlQuery);
                    jsonResponse.put("code", 0);
                    while ( myRes != null && myRes.next()) {
                        Map<String, Object> responseMap =  new HashMap<>();
                        responseMap.put("date", myRes.getString("date").substring(0, 19));
                        responseMap.put("dislikes", 0/*Integer.valueOf(myRes.getString("dislikes"))*/);
                        responseMap.put("likes", 0/*Integer.valueOf(myRes.getString("likes"))*/);
                        responseMap.put("id", Integer.valueOf(myRes.getString("id")));
                        responseMap.put("isApproved", myRes.getString("isApproved").equals("0")?false:true);
                        responseMap.put("isHighlighted", myRes.getString("isHighlighted").equals("0")?false:true);
                        responseMap.put("isSpam", myRes.getString("isSpam").equals("0")?false:true);
                        responseMap.put("isEdited", myRes.getString("isEdited").equals("0")?false:true);
                        responseMap.put("isDeleted", myRes.getString("isDeleted").equals("0")?false:true);
                        responseMap.put("points", 0);
                        responseMap.put("message", myRes.getString("message"));
                        responseMap.put("user", myRes.getString("email"));
                        responseMap.put("forum", myRes.getString("short_name"));
                        responseMap.put("parent",
                                (myRes.getString("parent") == null)?
                                        JSONObject.NULL:Integer.valueOf(myRes.getString("parent")));
                        responseMap.put("thread", Integer.valueOf(myRes.getString("thread")));
                        threads.add(responseMap);
                    }
                    myStmt.close();

                    if (threads != null)jsonResponse.put("response", threads);
                            else throw new SQLException("ResultSet is null");
                } catch (SQLException e) {
                    jsonResponse.put("code", 1);
                    jsonResponse.put("response", "Object is not exist");
                    System.out.println(e.getMessage());
                }
            } catch (JSONException e) {
                jsonResponse.put("code", 2);
                jsonResponse.put("response", "Arguments are not correct");
                System.out.println(e.getMessage());
            }
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
        response.getWriter().println(jsonResponse);
    }
}
