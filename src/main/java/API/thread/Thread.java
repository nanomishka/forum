package API.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nano on 01.06.2015.
 */
public class Thread {
    public int id;
    public String forum;
    public String user;
    public String title;
    public String message;
    public String date;
    public String slug;
    public boolean isClosed;
    public boolean isDeleted;
    public int posts;
    public int likes;
    public int dislikes;
    public int points;

    public Thread( int tId,
            String tForum,
            String tUser,
            String tTitle,
            String tMessage,
            String tDate,
            String tSlug,
            String tIsClosed,
            String tIsDeleted) {
        id = tId;
        forum =tForum;
        user = tUser;
        title = tTitle;
        message = tMessage;
        date = tDate;
        slug = tSlug;
        isClosed = tIsClosed.equals("true")? true : false;
        isDeleted = tIsDeleted.equals("true")? true : false;
        posts = likes = dislikes = points = 0;
    }

    public Thread(Connection myConn, int tId) throws SQLException {
        PreparedStatement myStmt = myConn.prepareStatement(
                "SELECT t.id, t.forum, title, t.isClosed, t.isDeleted, t.date, t.message, t.user, slug, " +
                        "short_name, email, COALESCE(count(distinct p.id),0) as posts," +
                        "COALESCE(count( distinct th.id),0) as points," +
                        "COALESCE(sum(th.tlike),0) as likes " +
                        "FROM threads t " +
                        "JOIN  forums f on t.forum = f.id " +
                        "JOIN  users u on t.user = u.id " +
                        "LEFT JOIN threadlikes th ON th.thread = t.id " +
                        "LEFT JOIN posts p ON p.thread=t.id and p.isDeleted != true " +
                        "WHERE t.id = ? GROUP by t.id;");
        myStmt.setInt(1, tId);
        ResultSet myRes = myStmt.executeQuery();

        myRes.first();
        id = Integer.valueOf(myRes.getString("id"));
        forum = myRes.getString("short_name");
        title = myRes.getString("title");
        message = myRes.getString("message");
        user = myRes.getString("email");
        date = myRes.getString("date");
        slug = myRes.getString("slug");
        isClosed = myRes.getString("isClosed").equals("1");
        isDeleted = myRes.getString("isDeleted").equals("1");
        posts = Integer.valueOf(myRes.getString("posts"));
        myRes.close();
        myStmt = myConn.prepareStatement("SELECT COALESCE(sum(tlike),0) as likes, " +
                "COALESCE(count(*)-sum(tlike),0) as dislikes FROM threadlikes WHERE thread = ?");
        myStmt.setInt(1, id);
        myRes = myStmt.executeQuery();

        myRes.first();
        likes = Integer.valueOf(myRes.getString("likes"));
        dislikes = Integer.valueOf(myRes.getString("dislikes"));
        points = likes - dislikes;
        myRes.close();
        myStmt.close();
    }

    public void setId (int uID){
        id = uID;
    }

    public Map<String, Object> getDetails() {
        Map<String, Object> responseMap =  new HashMap<>();
        responseMap.put("id", id);
        responseMap.put("forum", forum);
        responseMap.put("title", title);
        responseMap.put("message", message);
        responseMap.put("user", user);
        responseMap.put("date", date.substring(0, 19));
        responseMap.put("isClosed", isClosed);
        responseMap.put("isDeleted", isDeleted);
        responseMap.put("slug", slug);
        responseMap.put("posts", posts);
        responseMap.put("likes", likes);
        responseMap.put("dislikes", dislikes);
        responseMap.put("points", points);
        return responseMap;
    }
}
