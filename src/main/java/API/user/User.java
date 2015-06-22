package API.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nano on 01.06.2015.
 */
public class User {
    public int id;
    public String username;
    public String about;
    public String name;
    public String email;
    public List<String> following = new ArrayList<>();
    public List<String> followers = new ArrayList<>();
    public List<Integer> subscriptions = new ArrayList<>();
    public boolean isAnonymous;

    public User(int uId,
                String uUsername,
                String uAbout,
                String uName,
                String uEmail,
                String uIsAnonymous) {
        setBasicData(uId, uUsername, uAbout, uName, uEmail,uIsAnonymous);
    }

    public User(int uId,
                String uUsername,
                String uAbout,
                String uName,
                String uEmail,
                String uIsAnonymous,
                List<String> uFollowing,
                List<String> uFollowers) {
        setBasicData(uId, uUsername, uAbout, uName, uEmail,uIsAnonymous);
        followers = uFollowers;
        following = uFollowing;
    }

    public User(Connection myConn, String email) {
        try {
            PreparedStatement myStmt = myConn.prepareStatement("SELECT * FROM users WHERE email = ?");
            myStmt.setString(1, email);
            ResultSet myRes = myStmt.executeQuery();

            myRes.first();
            setBasicData(Integer.valueOf(myRes.getString("id")),
                    myRes.getString("username"),
                    myRes.getString("about"),
                    myRes.getString("name"),
                    myRes.getString("email"),
                    myRes.getString("isAnonymous"));
            myRes.close();

            myStmt = myConn.prepareStatement("SELECT email " +
                    "FROM follows f JOIN users u ON f.id2 = u.id WHERE f.id1=?;");
            myStmt.setInt(1, id);
            myRes = myStmt.executeQuery();
            while (myRes!=null && myRes.next()) following.add(myRes.getString("email"));
            myRes.close();

            myStmt = myConn.prepareStatement("SELECT email " +
                    "FROM follows f JOIN users u ON f.id1 = u.id WHERE f.id2=?;");
            myStmt.setInt(1, id);
            myRes = myStmt.executeQuery();
            while (myRes!=null && myRes.next()) followers.add(myRes.getString("email"));
            myRes.close();

            myStmt = myConn.prepareStatement("SELECT thread FROM subscribes  WHERE user = ?");
            myStmt.setInt(1, id);
            myRes = myStmt.executeQuery();
            while (myRes!=null && myRes.next()) subscriptions.add(Integer.valueOf(myRes.getString("thread")));
            myRes.close();
            myStmt.close();

        } catch (SQLException | NullPointerException e) {}
    }

    public User(Connection myConn, int uID) {
        try {
            PreparedStatement myStmt = myConn.prepareStatement("SELECT * FROM users WHERE id = ? ;");
            myStmt.setInt(1, uID);
            ResultSet myRes = myStmt.executeQuery();

            myRes.first();
            setBasicData(Integer.valueOf(myRes.getString("id")),
                    myRes.getString("username"),
                    myRes.getString("about"),
                    myRes.getString("name"),
                    myRes.getString("email"),
                    myRes.getString("isAnonymous"));
            myRes.close();

            myStmt = myConn.prepareStatement("SELECT email " +
                    "FROM follows f JOIN users u ON f.id2 = u.id WHERE f.id1=?;");
            myStmt.setInt(1, id);
            myRes = myStmt.executeQuery();
            while (myRes!=null && myRes.next()) following.add(myRes.getString("email"));
            myRes.close();

            myStmt = myConn.prepareStatement("SELECT email " +
                    "FROM follows f JOIN users u ON f.id1 = u.id WHERE f.id2=?;");
            myStmt.setInt(1, id);
            myRes = myStmt.executeQuery();
            while (myRes!=null && myRes.next()) followers.add(myRes.getString("email"));
            myRes.close();

            myStmt = myConn.prepareStatement("SELECT thread FROM subscribes  WHERE user = ?");
            myStmt.setInt(1, id);
            myRes = myStmt.executeQuery();
            while (myRes!=null && myRes.next()) subscriptions.add(Integer.valueOf(myRes.getString("thread")));
            myRes.close();
            myStmt.close();

        } catch (SQLException e) {}
    }

    public void setId (int uID){
        id = uID;
    }

    public Map<String, Object> getDetails() {
        Map<String, Object> responseMap =  new HashMap<>();
        responseMap.put("id", id);
        responseMap.put("username", username);
        responseMap.put("about", about);
        responseMap.put("name", name);
        responseMap.put("email", email);
        responseMap.put("isAnonymous", isAnonymous);
        responseMap.put("following", following);
        responseMap.put("followers", followers);
        responseMap.put("subscriptions", subscriptions);
        return responseMap;
    }

    private void setBasicData(int uId,
                         String uUsername,
                         String uAbout,
                         String uName,
                         String uEmail,
                         String uIsAnonymous) {
        id = uId;
        username = (uUsername == null || uUsername.equals("null"))  ? null : uUsername;
        about = (uAbout == null || uAbout.equals("null"))    ? null : uAbout;
        name =  (uName == null || uName.equals("null"))   ? null : uName;
        email = uEmail;
        isAnonymous = (uIsAnonymous.equals("true") || uIsAnonymous.equals("1") ? true : false);
    }


}
