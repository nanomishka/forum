package API.forum;

import API.thread.Thread;
import API.user.User;
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
import java.util.List;
import java.util.Map;

public class ForumListThreads extends HttpServlet {
    private Connection myConn;
    public ForumListThreads(Connection myConnect) { myConn = myConnect; }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        String short_name = request.getParameter("forum");
        String limit = request.getParameter("limit");
        String order = request.getParameter("order");
        String since = request.getParameter("since");
        String related[] = request.getParameterValues("related");
        Boolean reluser = false, relforum = false;
        if (related != null)
            for (int i=0; i< related.length; i++){
                if (related[i].equals("user")) reluser = true;
                if (related[i].equals("forum")) relforum = true;
            }
        String id = "";

        try {
            Statement myStmt = myConn.createStatement();
            if (short_name == null) throw new JSONException("Arguments failed");

            String sqlQuery = "SELECT DISTINCT th.id as id, user, forum FROM forums f JOIN threads th ON th.forum=f.id " +
                    "WHERE short_name='"+ short_name + "'";
            if (since != null) sqlQuery += " and th.date>='"+since+"' ";
            sqlQuery += " ORDER BY th.date " + (order != null && order.equals("asc")?"ASC":"DESC");
            if (limit != null) sqlQuery += " LIMIT " + limit;
            ResultSet myRes = myStmt.executeQuery(sqlQuery);

            List<Object> threads = new ArrayList<>();
            while ( myRes != null & myRes.next()) {
                Map<String, Object> thread = new Thread(myConn, Integer.valueOf(myRes.getString("id"))).getDetails();
                if ( reluser )
                    thread.put("user", new User(myConn, Integer.valueOf(myRes.getString("user"))).getDetails());
                if ( relforum )
                    thread.put("forum", new Forum(myConn, Integer.valueOf(myRes.getString("forum"))).getDetails());
                threads.add(thread);
            }
            myRes.close();
            myStmt.close();

            jsonResponse.put("code", 0);
            jsonResponse.put("response", threads);

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
