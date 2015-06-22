package API.thread;

import API.user.User;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.Map;

public class ThreadDetails extends HttpServlet {
    private Connection myConn;
    public ThreadDetails(Connection myConnect) { myConn = myConnect; }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        System.out.println(this.getClass());
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        String id = request.getParameter("thread");
        String related[] = request.getParameterValues("related");
        Boolean reluser = false, jsonerr = false;
        if (related != null)
            for (int i = 0; i < related.length; i++) {
                if (related[i].equals("user")) reluser = true;
                if (related[i].equals("thread")) jsonerr = true;
            }

        try {
            if (id == null || jsonerr) throw new JSONException("Arguments failed");
            Map<String, Object> thread = new Thread(myConn, Integer.valueOf(id)).getDetails();
            if (reluser) thread.put("user", new User(myConn, thread.get("user").toString()).getDetails());

            jsonResponse.put("code", 0);
            jsonResponse.put("response", thread);

        } catch (JSONException | SQLException e) {
            try {
                if (e instanceof SQLException) {
                    jsonResponse.put("code", 3);
                    jsonResponse.put("response", "Wrong query");
                } else {
                    jsonResponse.put("code", 3);
                    jsonResponse.put("response", "JSON is not correct");
                }
            } catch (JSONException ex) {};

            System.out.println("Error: " + e.getMessage());
        }
        response.getWriter().println(jsonResponse);
    }
}