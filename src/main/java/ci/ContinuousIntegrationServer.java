package ci;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ContinuousIntegrationServer extends AbstractHandler {
    HashMap<String, AbstractHandler> handlers;

    public ContinuousIntegrationServer() {
        handlers = new HashMap<>();
        handlers.put("/github/webhook", new ci.github.WebhookHandler());
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        System.out.println("routing " + target);

        AbstractHandler handler = handlers.get(target);
        if (handler != null) {
            try {
                handler.handle(target, baseRequest, request, response);
            } catch (RequestException e) {
                response.setStatus(e.status);
                response.getWriter().print(e.toString());
                baseRequest.setHandled(true);
                return;
            }
         }
    }
}
