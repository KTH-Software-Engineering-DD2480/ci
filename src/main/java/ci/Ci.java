import java.io.*;
import java.lang.*;

public class Ci {

    public static void Ci(){
        try {
            Runtime rt = Runtime.getRuntime();
            Process process = rt.exec("./gradlew test");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

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