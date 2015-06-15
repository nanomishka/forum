package API.user;

import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

public class UserDetails extends HttpServlet {
    private Connection myConn;
    public UserDetails(Connection myConnect) { myConn = myConnect; }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        String email = request.getParameter("user");

        try {
            if (email == null) throw new JSONException("Arguments failed");
            jsonResponse.put("code", 0);
            jsonResponse.put("response", new User(myConn, email).getDetails());
        } catch ( JSONException  e) {
            try {
                jsonResponse.put("code", 2);
                jsonResponse.put("response", "Arguments are not correct");
            } catch (JSONException ex) {};

            System.out.println("Error: " + e.getMessage());
        }
        response.getWriter().println(jsonResponse);
    }
}
