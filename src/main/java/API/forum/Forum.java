package API.forum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nano on 01.06.2015.
 */
public class Forum {
    public int id;
    public String name;
    public String shortName;
    public String user;

    public Forum(int uId,
                 String uName,
                 String uShortName,
                 String uUser) {
        id = uId;
        name = uName;
        shortName = uShortName;
        user = uUser;
    }

    public Forum(Connection myConn, int fId) throws SQLException {
        PreparedStatement myStmt = myConn.prepareStatement("SELECT f.id, f.name, short_name, email " +
                "FROM forums f JOIN users u ON u.id = f.userID WHERE f.id = ?;");
        myStmt.setInt(1, fId);
        ResultSet myRes = myStmt.executeQuery();

        myRes.first();
        id = Integer.valueOf(myRes.getString("id"));
        name = myRes.getString("name");
        shortName = myRes.getString("short_name");
        user = myRes.getString("email");
        myRes.close();
        myStmt.close();
    }

    public Forum(Connection myConn, String fShortName) throws SQLException {
        PreparedStatement myStmt = myConn.prepareStatement("SELECT f.id, f.name, short_name, email " +
                "FROM forums f JOIN users u ON u.id = f.userID WHERE f.short_name = ?");
        myStmt.setString(1, fShortName);
        ResultSet myRes = myStmt.executeQuery();

        myRes.first();
        id = Integer.valueOf(myRes.getString("id"));
        name = myRes.getString("name");
        shortName = myRes.getString("short_name");
        user = myRes.getString("email");
        myRes.close();
        myStmt.close();
    }


    public void setId (int uID){
        id = uID;
    }

    public Map<String, Object> getDetails() {
        Map<String, Object> responseMap =  new HashMap<>();
        responseMap.put("id", id);
        responseMap.put("name", name);
        responseMap.put("short_name", shortName);
        responseMap.put("user", user);
        return responseMap;
    }


}
