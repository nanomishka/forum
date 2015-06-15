package API.thread;

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
import java.util.*;

public class ThreadList extends HttpServlet {
    private Connection myConn;
    public ThreadList(Connection myConnect) { myConn = myConnect; }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        String email = request.getParameter("user");
        String forum = request.getParameter("forum");
        String limit = request.getParameter("limit");
        String order = request.getParameter("order");
        String since = request.getParameter("since");

        try{
            try {
                if (email==null && forum==null || email!=null && forum!=null) throw new JSONException("Arguments failed");
                List<Object> threads = new ArrayList<>();
                try {
                    String sqlQuery =
                            "SELECT t.id, t.forum, title, t.isClosed, t.isDeleted, t.date, " +
                                    "t.message, t.user, slug, short_name, email," +
                                    "COALESCE(sum(tlike),0) as likes, " +
                                    "COALESCE((count(*)-sum(tlike)),0) as dislikes," +
                                    "count(p.id) as posts FROM threads t " +
                            "JOIN  forums f on t.forum=f.id " +
                            "JOIN  users u on t.user=u.id " +
                            "LEFT JOIN posts p ON p.thread=t.id "+
                            "LEFT JOIN threadlikes th ON th.thread = t.id WHERE ";
                    if (email != null) sqlQuery += "email='" + email+"' ";
                    if (forum != null) sqlQuery += "short_name='" + forum+"'";
                    if (since != null) sqlQuery += " and t.date>='"+since+"' ";
                    sqlQuery += " GROUP BY t.date " + (order != null && order.equals("asc")?"ASC":"DESC");
                    if (limit != null) sqlQuery += " LIMIT " + limit;
                    System.out.println(sqlQuery);
                    Statement myStmt = myConn.createStatement();
                    ResultSet myRes = myStmt.executeQuery(sqlQuery);
                    jsonResponse.put("code", 0);
                    while ( myRes != null && myRes.next()) {
                        Map<String, Object> responseMap =  new HashMap<>();
                        responseMap.put("date", myRes.getString("date").substring(0, 19));
                        responseMap.put("dislikes", Integer.valueOf(myRes.getString("dislikes")));
                        responseMap.put("likes", Integer.valueOf(myRes.getString("likes")));
                        responseMap.put("id", Integer.valueOf(myRes.getString("id")));
                        responseMap.put("isClosed", myRes.getString("isClosed").equals("0")?false:true);
                        responseMap.put("isDeleted", myRes.getString("isDeleted").equals("0")?false:true);
                        responseMap.put("points", 0);
                        responseMap.put("posts", Integer.valueOf(myRes.getString("posts")));
                        responseMap.put("slug", myRes.getString("slug"));
                        responseMap.put("title", myRes.getString("title"));
                        responseMap.put("message", myRes.getString("message"));
                        String short_name = myRes.getString("short_name");
                        responseMap.put("user", myRes.getString("email"));
                        responseMap.put("forum", short_name);
                        threads.add(responseMap);
                    }
                    myRes.close();
                    myStmt.close();

                    jsonResponse.put("response", threads);
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

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {}
}
