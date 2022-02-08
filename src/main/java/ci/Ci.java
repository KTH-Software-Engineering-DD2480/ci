package ci;

import java.io.*;
import java.lang.*;

public class Ci {

    public static void runTests(String pathToDirectory){
        try {
            // Create command to run in pathToDirectory
            ProcessBuilder pb = new ProcessBuilder().command(new String[]{"./gradlew", "test"});
            pb.directory(new File(pathToDirectory));
            Process process = pb.start();

            // Read output
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // Print output to console
            String error_string = null;
            while ((error_string = stdError.readLine()) != null) {
                System.out.println(error_string);
            }
            String input_string = null;
            while ((input_string = stdInput.readLine()) != null) {
                System.out.println(input_string);
            }

        } catch(IOException e){
            e.printStackTrace();
        }

     }

}