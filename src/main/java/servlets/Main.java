package servlets;

import API.*;
import API.post.*;
import API.thread.*;
import API.user.*;
import API.forum.*;
import API.user.*;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.sql.*;


public class Main {
    public static void main(String[] args) throws Exception {

        String bd = "api_test";
        if (args.length == 1) {
            bd = args[0];
        }

        System.out.append("Connect to bd : ").append(bd).append('\n');


        Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + bd +
                "?useUnicode=true&characterEncoding=utf-8", "root", "1234");
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new Create(myConn)),         "/db/api/create/");
        context.addServlet(new ServletHolder(new Clear(myConn)),          "/db/api/clear/");
        context.addServlet(new ServletHolder(new Cap(myConn)),     "/db/api/user/create/");
        context.addServlet(new ServletHolder(new Cap(myConn)),    "/db/api/user/details/");
        context.addServlet(new ServletHolder(new Cap(myConn)),     "/db/api/user/follow/");
        context.addServlet(new ServletHolder(new Cap(myConn)),  "/db/api/user/listFollowers/");
        context.addServlet(new ServletHolder(new Cap(myConn)),  "/db/api/user/listFollowing/");
        context.addServlet(new ServletHolder(new Cap(myConn)),   "/db/api/user/unfollow/");
        context.addServlet(new ServletHolder(new Cap(myConn)),     "/db/api/user/updateProfile/");
        context.addServlet(new ServletHolder(new Cap(myConn)),  "/db/api/user/listPosts/");
        context.addServlet(new ServletHolder(new Cap(myConn)),    "/db/api/forum/create/");
        context.addServlet(new ServletHolder(new Cap(myConn)),   "/db/api/forum/details/");
        context.addServlet(new ServletHolder(new Cap(myConn)), "/db/api/forum/listUsers/");
        context.addServlet(new ServletHolder(new Cap(myConn)), "/db/api/forum/listPosts/");
        context.addServlet(new ServletHolder(new Cap(myConn)),"/db/api/forum/listThreads/");
        context.addServlet(new ServletHolder(new Cap(myConn)),   "/db/api/thread/create/");
        context.addServlet(new ServletHolder(new Cap(myConn)),  "/db/api/thread/details/");
        context.addServlet(new ServletHolder(new Cap(myConn)),    "/db/api/thread/close/");
        context.addServlet(new ServletHolder(new Cap(myConn)),     "/db/api/thread/open/");
        context.addServlet(new ServletHolder(new Cap(myConn)),   "/db/api/thread/remove/");
        context.addServlet(new ServletHolder(new Cap(myConn)),  "/db/api/thread/restore/");
        context.addServlet(new ServletHolder(new Cap(myConn)),"/db/api/thread/subscribe/");
        context.addServlet(new ServletHolder(new Cap(myConn)),    "/db/api/thread/unsubscribe/");
        context.addServlet(new ServletHolder(new Cap(myConn)),     "/db/api/thread/vote/");
        context.addServlet(new ServletHolder(new Cap(myConn)),   "/db/api/thread/update/");
        context.addServlet(new ServletHolder(new Cap(myConn)),     "/db/api/thread/list/");
        context.addServlet(new ServletHolder(new Cap(myConn)),"/db/api/thread/listPosts/");
        context.addServlet(new ServletHolder(new Cap(myConn)),     "/db/api/post/create/");
        context.addServlet(new ServletHolder(new PostDetails(myConn)),    "/db/api/post/details/");
        context.addServlet(new ServletHolder(new Cap(myConn)),       "/db/api/post/list/");
        context.addServlet(new ServletHolder(new Cap(myConn)),     "/db/api/post/remove/");
        context.addServlet(new ServletHolder(new Cap(myConn)),    "/db/api/post/restore/");
        context.addServlet(new ServletHolder(new Cap(myConn)),     "/db/api/post/update/");
        context.addServlet(new ServletHolder(new Cap(myConn)),       "/db/api/post/vote/");
        context.addServlet(new ServletHolder(new Cap(myConn)),         "/db/api/status/");

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setResourceBase("templates");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, context});

        Server server = new Server(8080);
        server.setHandler(context);

        server.start();
        server.join();
    }
}
