package API.user;

import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class UserFollowing extends HttpServlet {
    private Connection myConn;
    public UserFollowing(Connection myConnect) { myConn = myConnect; }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        System.out.println(this.getClass());
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        String email = request.getParameter("user");
        String limit = request.getParameter("limit");
        String order = request.getParameter("order");
        String since_id = request.getParameter("since_id");
        String id = "";


        try{
            if (email == null) throw new JSONException("Arguments failed");
            PreparedStatement myStmt = myConn.prepareStatement("SELECT id FROM users WHERE email = ?");
            myStmt.setString(1, email);
            ResultSet myRes = myStmt.executeQuery();
            if (myRes != null && myRes.first()) id = myRes.getString("id");
            myRes.close();

            List<Object> listFollowers = new ArrayList<>();
            String query = "SELECT id2 as id FROM follows f JOIN users u ON f.id2 = u.id WHERE f.id1 = " + id + " ";
            if (since_id != null) query.concat(" and id1>=" + since_id);
            query.concat(" ORDER BY name ");
            if (order != null && order.equals("asc")) query.concat("ASC");
            else query.concat("DESC");
            if (limit != null) query.concat(" LIMIT " + limit);

            myRes = myStmt.executeQuery(query);
            while (myRes != null && myRes.next()) {
                listFollowers.add(new User(myConn, Integer.valueOf(myRes.getString("id"))).getDetails());
            }
            myRes.close();
            myStmt.close();

            jsonResponse.put("code", 0);
            jsonResponse.put("response", listFollowers);

        } catch (SQLException | JSONException  e) {
            try {
                if (e instanceof SQLException) {
                    jsonResponse.put("code", 1);
                    jsonResponse.put("response", "Object is not exist");
                } else {
                    jsonResponse.put("code", 2);
                    jsonResponse.put("response", "Arguments are not correct");
                }
            } catch (JSONException ex) {};

            System.out.println("Error: " + e.getMessage());
        }
        response.getWriter().println(jsonResponse);
    }
}
