package ci;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Routes incoming requests to their respective handlers.
 */
public class ContinuousIntegrationServer extends AbstractHandler {
    /**
     * Given a URL path (ie. {@code /github/webhook}) determines the handler for that specific path.
     */
    HashMap<String, AbstractHandler> handlers;

    /**
     * Creates a server with the default set of handlers.
     */
    public ContinuousIntegrationServer() {
        // Setup handlers for different paths
        handlers = new HashMap<>();
        handlers.put("/github/webhook", new ci.github.WebhookHandler());
        handlers.put("/logs", new ci.LogsRestHandler());
        handlers.put("/view-logs", new ci.ViewLogsHandler());
    }

    /**
     * Handles an incoming request by forwarding them to a respective handler. 
     * If no appropriate handler can be found, returns a 404 error.
     * @param target the URL path of the request (ie. {@code /github/webhook}).
     * @param baseRequest the incoming GitHub webhook request.
     * @param request not used
     * @param response will either contain a status code or an error.
     */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        System.out.println("routing: " + target);

        AbstractHandler handler = handlers.get(target);
        if (handler != null) {
            try {
                handler.handle(target, baseRequest, request, response);
            } catch (RequestException e) {
                // Special-case our own exception, in order to provide custom status codes. Other exceptions are returned with status 500.
                response.setStatus(e.status);
                response.getWriter().print(e.toString());
                baseRequest.setHandled(true);
                return;
            }
         }

         // If a request is not handled, Jetty responds with `404 Not Found`.
    }
}
