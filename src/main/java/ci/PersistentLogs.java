package ci;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Arrays;

import org.json.JSONTokener;
import org.json.JSONObject;


public class PersistentLogs {
    public String path;         // The folder where the logs are stored.
    public int build_number;    // The build number for easier ordering of logs and unique identifiers

    // Initialize the persistent logs object. This object knows where to store and read logs and keeps track of
    // how many builds have been performed.
    public PersistentLogs(String path_to_logs_folder) {
        this.path = path_to_logs_folder;
        File[] files = new File(path).listFiles();
        if (files.length == 1) {
            this.build_number = 0;
        } else {
            // (Lambda) In line comparing function, since file name sorting is not equal across major OSes (and 10
            // is sometimes less than 2).
            Arrays.sort(files, (f1, f2) -> {
                if (f1.getName().equals("README.md")) return -1;
                else if (f2.getName().equals("README.md")) return 1;
                return Integer.parseInt(f1.getName().split("_")[0]) 
                    - Integer.parseInt(f2.getName().split("_")[0]);
            });
            this.build_number = Integer.parseInt(files[files.length - 1].getName().split("_")[0]) + 1;
        }
    }

    // Add a log to the folder the class is initialized with. The log's name is the build number and the type of the
    // commit separated by an underscore.
    public void add_log(Log_entry log_entry) {
        try {
            File log_file = new File(path + "/" + this.build_number + "_" + log_entry.generate_log_file_name());
            this.build_number++;
            if (!log_file.exists()) {
                log_file.createNewFile();
            }
            FileWriter fw = new FileWriter(log_file);
            fw.write(log_entry.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Return an array with all the files in the logs folder (including the README.md file which will be 
    // in position [0]).
    public File[] all_files() {
        File[] files = new File(path).listFiles();
        if (files.length == 1) return files;
        Arrays.sort(files, (f1, f2) -> {
            if (f1.getName().equals("README.md")) return -1;
            else if (f2.getName().equals("README.md")) return 1;
            return Integer.parseInt(f1.getName().split("_")[0]) 
                - Integer.parseInt(f2.getName().split("_")[0]);
        });
        return files;
    }

    // Given a File object (usually created by all_files() or by specifying a file name), return the 
    // contents of the file as a Log_entry object.
    public static Log_entry get_log(File log_file) {
        try {
            if (log_file.getName().equals("README.md")) return null;
            FileReader fr = new FileReader(log_file);
            JSONObject json_object = new JSONObject(new JSONTokener(fr));
            Log_entry le = new Log_entry(
                Log_entry.Log_type.valueOf(json_object.getString("type")),
                json_object.getString("refspec"),
                json_object.getString("commit_SHA"),
                new Date(json_object.getLong("date_time")),
                Log_entry.Test_status.valueOf(json_object.getString("status"))
            );
            // Prevent absurd behaviour where file isn't deleted when output stream isn't closed in this terminated function.
            fr.close();
            return le;            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Attempt to delete all test log files in logs folder.
    public static void delete_test_logs() {
        File[] files = new File("logs").listFiles();
        for (File f : files) {
            System.out.println("Trying to delete: " + f.getName());
            if (f.getName().endsWith("_TEST.log")) {
                f.delete();
            }
        }
    }
}
