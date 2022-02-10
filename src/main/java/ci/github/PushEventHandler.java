package ci.github;

import java.io.IOException;

import org.json.JSONObject;

import ci.JobQueueConsumer.WorkItem;
import jakarta.servlet.ServletException;

/**
 * Implements the {@code push} GitHub webhook event. 
 */
public class PushEventHandler implements EventHandler {
    /**
     * Handles an incoming {@code push} GitHub webhook.
     * @param body the JSON body of the webhook.
     */
    @Override
    public void handle(JSONObject body) throws IOException, ServletException {
        WorkItem item = extractWorkItem(body);
        try {
            ci.JobQueueConsumer.addWork(item);
        } catch (InterruptedException e) {
            System.err.println("failed to add work item: " + e.getMessage());
        }
    }

    /**
     * Extract a work item from the push event JSON body
     * @param body the json body of a push event
     * @return the corresponding work item
     */
    static WorkItem extractWorkItem(JSONObject body) {
        JSONObject repository = body.getJSONObject("repository");
        JSONObject headCommit = body.getJSONObject("head_commit");

        String head = body.getString("after");

        WorkItem item = new WorkItem();

        // Example: https://api.github.com/repos/nolanderc/ci-test-repo/statuses/{sha}
        item.statusUrl = repository.getString("statuses_url").replace("{sha}", head);
        item.repoUrl = repository.getString("url");
        item.ref = body.getString("ref");
        item.head = head;
        item.message = headCommit.getString("message");

        return item;
    }
}