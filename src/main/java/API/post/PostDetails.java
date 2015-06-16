package API.post;

import API.forum.Forum;
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
import java.sql.SQLException;
import java.util.Map;

public class PostDetails extends HttpServlet {
    private Connection myConn;
    public PostDetails(Connection myConnect) { myConn = myConnect; }
    static int k = 1;

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        System.out.println("POST DETAILS " + k++);
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        String id = request.getParameter("post");
        String related[] = request.getParameterValues("related");
        Boolean reluser = false, relforum = false, relthread = false;
        if (related != null)
            for (int i = 0; i< related.length; i++){
                if (related[i].equals("user")) reluser = true;
                if (related[i].equals("forum")) relforum = true;
                if (related[i].equals("thread")) relthread = true;
            }

        try {
            if ( id == null ) throw new JSONException("Arguments failed");
            Map<String, Object> post = new Post(myConn, Integer.valueOf(id)).getDetails();
            if (reluser) post.put("user", new User(myConn, post.get("user").toString()).getDetails());
            if (relforum) post.put("forum", new Forum(myConn, post.get("forum").toString()).getDetails());
            if (relthread) post.put("thread", new Thread(myConn, Integer.valueOf(post.get("thread").toString())).getDetails());

            jsonResponse.put("code", 0);
            jsonResponse.put("response", post);

        } catch (JSONException | SQLException e) {
            try {
                if (e instanceof SQLException) {
                    jsonResponse.put("code", 1);
                    jsonResponse.put("response", "Object is not exist");
                } else {
                    jsonResponse.put("code", 2);
                    jsonResponse.put("response", "JSON is not correct");
                }
            } catch (JSONException ex) {};

            //System.out.println("Error: " + e.getMessage());
        }
        response.getWriter().println(jsonResponse);
        System.gc();
    }
}
