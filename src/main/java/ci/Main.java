package ci;

import org.eclipse.jetty.server.Server;

/** The main entry point */
public class Main {
    /** The default port to which the server is bound */
    static short PORT = 8017;

    /** 
     * The main entry point
     * @param args Arguments passed from the command line
     * @throws Exception in case of an error
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Hello CI!");

        JobQueueConsumer jobQueue = new JobQueueConsumer();
        jobQueue.start();

        Server server = new Server(PORT);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}

