package API.forum;

import API.user.User;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForumListUsers extends HttpServlet {
    private Connection myConn;
    public ForumListUsers(Connection myConnect) { myConn = myConnect; }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        System.out.println(this.getClass());
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        String short_name = request.getParameter("forum");
        String limit = request.getParameter("limit");
        String order = request.getParameter("order");
        String since_id = request.getParameter("since_id");

        try {
            if (short_name == null) throw new JSONException("Arguments failed");

            String sqlQuery ="SELECT DISTINCT user FROM forums f JOIN posts p ON p.forum = f.id " +
                    "JOIN users u ON user = u.id " +
                    "WHERE short_name = '"+ short_name + "'";
            if (since_id != null) sqlQuery += " and user >= '" + since_id + "' ";
            sqlQuery += " ORDER BY u.name " + (order != null && order.equals("asc")?"ASC":"DESC");
            if (limit != null) sqlQuery += " LIMIT " + limit;
            Statement myStmt = myConn.createStatement();
            ResultSet myRes = myStmt.executeQuery(sqlQuery);

            List<Object> users = new ArrayList<>();
            while ( myRes != null & myRes.next()) {
                users.add(new User(myConn, Integer.valueOf(myRes.getString("user"))).getDetails());
            }
            myRes.close();
            myStmt.close();

            jsonResponse.put("code", 0);
            jsonResponse.put("response", users);

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
