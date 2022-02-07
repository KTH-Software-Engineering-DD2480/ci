package ci;

import java.io.IOException;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Main {
    // This is just an example of how to run a Jetty server. Our CI should probably live in its own class.
    static class HelloServer extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
                throws IOException, ServletException {
            System.out.println("target: " + target);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("Hello! You are visiting " + target);
            baseRequest.setHandled(true);
        }
    }

    static short PORT = 8017;

    public static void main(String[] args) throws Exception {
        System.out.println("Hello CI!");

        Server server = new Server(PORT);
        server.setHandler(new HelloServer());
        server.start();
        server.join();
    }
}

