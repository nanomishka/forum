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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreadListPosts extends HttpServlet {
    private Connection myConn;
    public ThreadListPosts(Connection myConnect) { myConn = myConnect; }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        String thread = request.getParameter("thread");
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

        try{
            try {
                if ( thread==null) throw new JSONException("Arguments failed");
                List<Object> postsID = new ArrayList<>();
                List<Object> posts = new ArrayList<>();

                try {
                    String sqlQuery =
                            "SELECT DISTINCT p.id as id FROM threads th JOIN posts p ON p.thread=th.id " +
                                    "WHERE th.id='"+ thread + "'";
                    if (since != null) sqlQuery += " and p.date>='"+since+"' ";
                    sqlQuery += " ORDER BY p.date " + (order != null && order.equals("asc")?"ASC":"DESC");
                    if (limit != null) sqlQuery += " LIMIT " + limit;
                    System.out.println(sqlQuery);
                    Statement myStmt = myConn.createStatement();
                    ResultSet myRes = myStmt.executeQuery(sqlQuery);

                    while ( myRes != null & myRes.next()) postsID.add(myRes.getString("id"));
                    jsonResponse.put("code", 0);

                    for (Integer i=0; i < postsID.size(); i++) {
                        Map<String, Object> responseMap =  new HashMap<>();
                        sqlQuery =
                                "SELECT p.id, forum, thread, user, message, isApproved, isDeleted, isEdited, " +
                                        "isHighlighted, isSpam, date, parent, email,  short_name FROM posts p " +
                                        "JOIN users u ON p.user=u.id " +
                                        "JOIN forums f ON p.forum=f.id " +
                                        "WHERE p.id="+postsID.get(i);
                        String sqlLikes = "SELECT COALESCE(sum(tlike),0) as likes, " +
                                "COALESCE(count(*)-sum(tlike),0) as dislikes FROM postlikes WHERE post="+postsID.get(i);

                        System.out.println(sqlQuery);
                        myRes = myStmt.executeQuery(sqlQuery);
                        if (!myRes.first()) throw new SQLException("Result is null");

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
                        thread = myRes.getString("thread");


                        if (reluser) {
                            String userSQL = "SELECT * FROM users WHERE id="+ myRes.getString("user");
                            System.out.println(userSQL);
                            ResultSet cur = myStmt.executeQuery(userSQL);
                            JSONObject user = new JSONObject();
                            cur.first();
                            user.put("about", (cur.getString("about").equals("null")) ? JSONObject.NULL : cur.getString("about"));
                            user.put("email", cur.getString("email"));
                            user.put("name", (cur.getString("name").equals("null")) ? JSONObject.NULL : cur.getString("name"));
                            user.put("isAnonymous", cur.getString("isAnonymous").equals("0")?false:true);
                            user.put("username", (cur.getString("username").equals("null")) ? JSONObject.NULL : cur.getString("username"));
                            user.put("id", Integer.valueOf(cur.getString("id")));
                            String userID = cur.getString("id");

                            ResultSet followSet;
                            List<String> following = new ArrayList<>();
                            followSet = myStmt.executeQuery("SELECT email FROM follows f JOIN users u ON f.id2 = u.id" +
                                    " WHERE f.id1="+ userID + ";");
                            while (followSet!=null && followSet.next()) following.add(followSet.getString("email"));
                            user.put("following", following);

                            List<String> followers = new ArrayList<>();
                            followSet = myStmt.executeQuery("SELECT email FROM follows f JOIN users u ON f.id1 = u.id" +
                                    " WHERE f.id2="+ userID + ";");
                            while (followSet!=null && followSet.next()) followers.add(followSet.getString("email"));
                            user.put("followers", followers);

                            List<String> subscriptions = new ArrayList<>();
                            user.put("subscriptions", subscriptions);
                            responseMap.put("user", user);
                        } else responseMap.put("user", email);

                        if (relforum) {
                            String userSQL = "SELECT * FROM forums f JOIN users u ON f.userID=u.id " +
                                    "WHERE short_name='"+ short_name+"'";
                            System.out.println(userSQL);
                            ResultSet cur = myStmt.executeQuery(userSQL);
                            JSONObject forum = new JSONObject();
                            cur.first();
                            forum.put("id", cur.getString("id"));
                            forum.put("name", cur.getString("name"));
                            forum.put("short_name", cur.getString("short_name"));
                            forum.put("user", cur.getString("email"));
                            responseMap.put("forum", forum);
                        } else responseMap.put("forum", short_name);

                        if (relthread) {
                            String userSQL = "SELECT t.id, title, isClosed, t.isDeleted, email," +
                                    "t.date, t.message, slug, short_name, count(*) as posts " +
                                    "FROM threads t JOIN users u ON t.user=u.id " +
                                    "JOIN forums f ON t.forum=f.id "+
                                    "JOIN posts p ON t.id=p.thread "+
                                    "WHERE t.id=" + thread + " " +
                                    "GROUP BY t.id";
                            System.out.println(userSQL);
                            ResultSet cur = myStmt.executeQuery(userSQL);
                            JSONObject curthread = new JSONObject();
                            cur.first();
                            curthread.put("date", cur.getString("date").substring(0, 19));
                            curthread.put("dislikes", 0);
                            curthread.put("likes", 0);
                            curthread.put("id", cur.getString("id"));
                            curthread.put("isClosed", cur.getString("isClosed").equals("0") ? false : true);
                            curthread.put("isDeleted", cur.getString("isDeleted").equals("0") ? false : true);
                            curthread.put("points", 0);
                            curthread.put("posts", Integer.valueOf(cur.getString("posts")));
                            curthread.put("slug", cur.getString("slug"));
                            curthread.put("title", cur.getString("title"));
                            curthread.put("message", cur.getString("message"));
                            curthread.put("user", cur.getString("email"));
                            curthread.put("forum", cur.getString("short_name"));
                            curthread.put("thread", Integer.valueOf(cur.getString("id")));
                            responseMap.put("thread", curthread);
                        } else responseMap.put("thread", Integer.valueOf(thread));

                        ResultSet myLike = myStmt.executeQuery(sqlLikes);
                        myLike.first();
                        responseMap.put("likes", Integer.valueOf(myLike.getString("likes")));
                        responseMap.put("dislikes", Integer.valueOf(myLike.getString("dislikes")));
                        responseMap.put("points", Integer.valueOf(myLike.getString("likes")) - Integer.valueOf(myLike.getString("dislikes")));

                        posts.add(responseMap);
                    }

                    myRes.close();
                    myStmt.close();

                    jsonResponse.put("response", posts);

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
