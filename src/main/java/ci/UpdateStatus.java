package ci;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;


import org.json.JSONObject;

import ci.Helpers.Status;
import jakarta.servlet.http.HttpServletResponse;

public class UpdateStatus {

    /**
     * Takes strings `url` and `status` as inputs, and updates commit for the
     * given SHA (unique id of commit) in `url`. 
     * Input `status` should be either "error", "failure", "pending" or "success".
     * @param url - - https://api.github.com/repos/{owner}/{repo}/statuses/{sha}
     * @param status
     */
    public static void updateStatus(String url, Status status) 
        throws IOException, URISyntaxException, InterruptedException {
        JSONObject parameters = new JSONObject();
        parameters.put("state", status.toString());
        parameters.put("context", "KTH CI-17 - gradle test");
        
        String accessToken = getGitHubAccessTokenFromEnvironment();
        HttpRequest request = HttpRequest.newBuilder()
        .uri(new URI(url))
        .headers("accept", "application/vnd.github.v3+json", "Authorization", "token " + accessToken)
        .POST(HttpRequest.BodyPublishers.ofString(parameters.toString()))
        .build();
        
        var response = HttpClient.newBuilder()
                .build()
                .send(request, BodyHandlers.ofString());

        int code = response.statusCode();
        if (code < 200 || code >= 300) {
            System.err.println(response.body());
            throw new IOException("failed to update commit status: " + code);
        }
    }

    // load the GitHub secret token from an environemnt variable
    private static String getGitHubAccessTokenFromEnvironment() {
        String accessToken = System.getenv("GITHUB_ACCESS_TOKEN");
        if (accessToken == null) {
            throw new RuntimeException("GITHUB_ACCESS_TOKEN environment variable not set (must be set to the same value as the one used on GitHub)");
        }
        return accessToken;
    }

    /**
     * Takes a string `url` as input and returns `status` of the commit as a string.
     * Used as a helper method to test `updateStatus`.
     * @param url - https://api.github.com/repos/{owner}/{repo}/commits/{ref}/statuses
     * @return status
     */
    public static String getStatus(String url) 
        throws IOException, URISyntaxException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
        .uri(new URI(url))
        .GET()
        .build();

        HttpResponse<String> response = HttpClient.newBuilder()
        .build()
        .send(request, BodyHandlers.ofString());
        JSONObject json = new JSONObject(response.body());

        return json.getString("state").toString();

    }
}

