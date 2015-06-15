package API;

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
import java.util.HashMap;
import java.util.Map;

public class Status extends HttpServlet {
    private Connection myConn;
    public Status(Connection myConnect) { myConn = myConnect;}


    public void doPost(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
    }

    public void doGet(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();

        try {

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            Statement myStmt = myConn.createStatement();
            ResultSet myRes = myStmt.executeQuery("SELECT count(*) as cnt FROM users");
            myRes.first();
            Map<String, Object> responseMap =  new HashMap<>();
            responseMap.put("user", myRes.getString("cnt"));
            myRes.close();

            myRes = myStmt.executeQuery("SELECT count(*) as cnt FROM threads");
            myRes.first();
            responseMap.put("thread", myRes.getString("cnt"));
            myRes.close();

            myRes = myStmt.executeQuery("SELECT count(*) as cnt FROM forums");
            myRes.first();
            responseMap.put("forum", myRes.getString("cnt"));
            myRes.close();

            myRes = myStmt.executeQuery("SELECT count(*) as cnt FROM posts");
            myRes.first();
            responseMap.put("post", myRes.getString("cnt"));
            myRes.close();
            myStmt.close();

            jsonResponse.put("code", 0);
            jsonResponse.put("response",responseMap);

        } catch (SQLException | JSONException e) {
            System.out.println("Could not truncate: " + e.getMessage());
        }
        response.getWriter().println(jsonResponse);
    }
}
