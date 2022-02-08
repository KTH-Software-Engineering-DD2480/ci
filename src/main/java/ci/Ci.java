package ci;

import java.io.*;
import java.lang.*;

public class Ci {

    public static Boolean runTests(String pathToDirectory){
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
            Boolean toReturn = true;
            while ((error_string = stdError.readLine()) != null) {
                System.out.println(error_string);
                toReturn = false;
            }
            String input_string = null;
            while ((input_string = stdInput.readLine()) != null) {
                System.out.println(input_string);
            }

            return toReturn;
            
        } catch(IOException e){
            e.printStackTrace();
            return false;
        }

     }

}