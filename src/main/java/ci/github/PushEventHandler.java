package ci.github;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.ServletException;

public class PushEventHandler implements EventHandler {
    static class PushEventData {
        // URL to the repository
        public String url;

        // git refspec of the given branch
        public String ref;

        // Commit SHA of the most recent commit after the push
        public String head;

        // Commit message of the most recent commit
        public String message;

        public PushEventData(JSONObject body) {
            JSONObject repository = body.getJSONObject("repository");
            JSONObject headCommit = body.getJSONObject("head_commit");

            this.url = repository.getString("url");
            this.ref = body.getString("ref");
            this.head = body.getString("after");
            this.message = headCommit.getString("message");
        }
    }

    @Override
    public void handle(JSONObject body) throws IOException, ServletException {
        PushEventData data = new PushEventData(body);

        System.out.println("push-url: " + data.url);
        System.out.println("push-ref: " + data.ref);
        System.out.println("push-commit: " + data.head);
        System.out.println("push-message: " + data.message);
    }
}