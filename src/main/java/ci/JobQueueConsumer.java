package ci;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class JobQueueConsumer extends Thread {
    public static class WorkItem {
        /** URL to the Git repository */
        public String statusUrl;

        /** URL to the Git repository */
        public String repoUrl;

        /** git refspec of the given branch */
        public String ref;

        /** Commit SHA of the most recent commit after the push */
        public String head;

        /** Commit message of the most recent commit */ 
        public String message;
    }

    private static BlockingQueue<WorkItem> queue = new LinkedBlockingQueue<>();

    public static void addWork(WorkItem item) throws InterruptedException {
        queue.put(item);
    }

    public void run() {
        while (true) {
            try {
                WorkItem item = queue.take();
                ci.ExecuteJob.execute(item.repoUrl, "execute_temp", item.head, item.statusUrl, item.ref, "logs");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}