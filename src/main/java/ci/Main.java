package ci;

import org.eclipse.jetty.server.Server;

public class Main {
    static short PORT = 8017;

    public static void main(String[] args) throws Exception {
        System.out.println("Hello CI!");

        Server server = new Server(PORT);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}

