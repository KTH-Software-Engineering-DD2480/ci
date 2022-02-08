package ci;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;


import org.json.JSONObject;

public class UpdateStatus {

    /**
     * Takes strings `SHA` (unique id of commit) and `status` as inputs, and updates commit for the
     * given `SHA`. 
     * Input `status` should be either "error", "failure", "pending" or "success".
     * @param SHA
     * @param status
     */
    public static void updateStatus(String SHA, String status) {

        if(!isValidStatus(status)){
            System.out.println("Invalid input for status");
            return;
        }

        String accessToken = "token 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";

        try {
            HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI("https://api.github.com/repos/KTH-Software-Engineering-DD2480/commit-status-test/statuses/" + SHA))
            .headers("accept", "application/vnd.github.v3+json", "Authorization", accessToken)
            .POST(HttpRequest.BodyPublishers.ofString("{\"state\":\"" + status + "\"}"))
            .build();
          
            HttpResponse<String> response = HttpClient.newBuilder()
            .build()
            .send(request, BodyHandlers.ofString());

            JSONObject json = new JSONObject(response.body());
            System.out.println("json object: " + json);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Check that `status` is one of "error", "success", "pending" or "failure"
    private static boolean isValidStatus(String status) {
        if(status == "error" || status == "failure" || status == "success" || status == "pending") {
            return true;
        }
        return false;
    }

    /**
     * Takes a string `SHA` (unique id of commit) as input and returns `status` of the commit as a string.
     * Used as a helper method to test `updateStatus`.
     * @param SHA
     * @return status
     */
    public static String getStatus(String SHA) {
        String status = "";
        try {
            HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI("https://api.github.com/repos/KTH-Software-Engineering-DD2480/commit-status-test/commits/" + SHA + "/status"))
            .GET()
            .build();
            HttpResponse<String> response = HttpClient.newBuilder()
            .build()
            .send(request, BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());
            status = json.getString("state").toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return status;
    }
}

