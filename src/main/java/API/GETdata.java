package API;

import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;

/**
 * Created by nano on 01.06.2015.
 */
public class GETdata {
    static private GETdata obj = null;

    public static GETdata getInstance() {
        if (obj == null) {
            obj = new GETdata();
        }
        return obj;
    }


    public static JSONObject getData(HttpServletRequest request) {
        StringBuffer buffer = new StringBuffer();
        String line;
        JSONObject response = null;

        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            response = new JSONObject(buffer.toString());
        } catch (Exception e) {
            System.out.println("Error: parse GET data");
        }

        return  response;
    }
}
