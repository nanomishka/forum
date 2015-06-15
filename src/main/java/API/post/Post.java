package API.post;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nano on 01.06.2015.
 */
public class Post {
    public int id;
    public String forum;
    public String user;
    public String message;
    public String date;
    public int thread;
    public boolean isDeleted;
    public boolean isApproved;
    public boolean isEdited;
    public boolean isHighlighted;
    public boolean isSpam;
    public String parent;
    public int likes;
    public int dislikes;
    public int points;

    public Post(int tId,
            String tForum,
            String tUser,
            String tMessage,
            String tDate,
            String tThread,
            String tIsDeleted,
            String tIsApproved,
            String tIsEdited,
            String tIsHighlighted,
            String tIsSpam,
            String tParent) {
        id = tId;
        forum = tForum;
        user = tUser;
        message = tMessage;
        date = tDate;
        thread = Integer.valueOf(tThread);
        isDeleted = tIsDeleted.equals("true")? true : false;
        isApproved = tIsApproved.equals("true")? true : false;
        isEdited = tIsEdited.equals("true")? true : false;
        isHighlighted = tIsHighlighted.equals("true")? true : false;
        isSpam = tIsSpam.equals("true")? true : false;
        parent = (tParent.equals("null"))  ? null : tParent;
    }

    public Post(Connection myConn, int pId) throws SQLException {
        PreparedStatement myStmt = myConn.prepareStatement("SELECT p.id, forum, thread, user, message, " +
            "isApproved, isDeleted, isEdited, " +
            "isHighlighted, isSpam, date, parent, email,  short_name FROM posts p " +
            "JOIN users u ON p.user=u.id " +
            "JOIN forums f ON p.forum=f.id " +
            "WHERE p.id = ?");
        myStmt.setInt(1, pId);
        ResultSet myRes = myStmt.executeQuery();

        if ( myRes != null && myRes.first()) {
            id = Integer.valueOf(myRes.getString("id"));
            forum = myRes.getString("short_name");
            thread = Integer.valueOf(myRes.getString("thread"));
            user = myRes.getString("email");
            message = myRes.getString("message");
            isApproved = myRes.getString("isApproved").equals("1");
            isDeleted = myRes.getString("isDeleted").equals("1");
            isEdited = myRes.getString("isEdited").equals("1");
            isHighlighted = myRes.getString("isHighlighted").equals("1");
            isSpam = myRes.getString("isSpam").equals("1");
            date = myRes.getString("date");
            parent = myRes.getString("parent");
            myRes.close();
            myStmt.close();
        } else {
            myRes.close();
            throw new SQLException("");
        }

        myStmt = myConn.prepareStatement("SELECT COALESCE(sum(tlike),0) as likes, " +
                "COALESCE(count(*)-sum(tlike),0) as dislikes FROM postlikes WHERE post = ?");
        myStmt.setInt(1, pId);
        myRes = myStmt.executeQuery();
        if ( myRes != null && myRes.first()) {
            likes = Integer.valueOf(myRes.getString("likes"));
            dislikes = Integer.valueOf(myRes.getString("dislikes"));
            points = likes - dislikes;
        }
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
        responseMap.put("thread", thread);
        responseMap.put("user", user);
        responseMap.put("message", message);
        responseMap.put("date", date.substring(0, 19));
        responseMap.put("isApproved", isApproved);
        responseMap.put("isDeleted", isDeleted);
        responseMap.put("isEdited", isEdited);
        responseMap.put("isHighlighted", isHighlighted);
        responseMap.put("isSpam", isSpam);
        responseMap.put("parent", parent != null ? Integer.valueOf(parent) : JSONObject.NULL);
        responseMap.put("likes", likes);
        responseMap.put("dislikes", dislikes);
        responseMap.put("points", points);
        responseMap.put("likes", likes);
        responseMap.put("dislikes", dislikes);
        return responseMap;
    }
}
