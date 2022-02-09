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
    public static String path;         // The folder where the logs are stored.
    public int build_number;    // The build number for easier ordering of logs and unique identifiers

    // Initialize the persistent logs object. This object knows where to store and read logs and keeps track of
    // how many builds have been performed.
    public PersistentLogs(String path_to_logs_folder) {
        this.path = path_to_logs_folder;
        File[] files = all_logs();
        if (files.length == 0) {
            this.build_number = 0;
        } else {
            // By grabbing the latest log, the counting is uninterrupted in the case of accidental log deletions.
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

    // Return an array with all the log files in the logs folder (excluding the README.md).
    // If the folder doesn't yet exist, it will be created.
    public File[] all_logs() {
        File[] files_with_README = new File(path).listFiles();
        if (files_with_README == null) {
            new File(path).mkdir();
            return new File[0];
        }
        else if (files_with_README.length == 0) return new File[0];
        else if (files_with_README.length == 1 && files_with_README[0].getName().equals("README.md")) return new File[0];

        boolean has_README = false;
        for (File file : files_with_README) {
            if (file.getName().equals("README.md")) {
                has_README = true;
                break;
            }
        }
        if (!has_README) {
            Arrays.sort(files_with_README, (f1, f2) -> {
                return Integer.parseInt(f1.getName().split("_")[0])
                    - Integer.parseInt(f2.getName().split("_")[0]);
            });
            return files_with_README;
        }
        File[] files = new File[files_with_README.length - 1];
        int i = 0;
        for (File file : files_with_README) {
            if (file.getName().equals("README.md")) continue;
            files[i] = file;
            i++;
        }
        Arrays.sort(files, (f1, f2) -> {
            return Integer.parseInt(f1.getName().split("_")[0])
                - Integer.parseInt(f2.getName().split("_")[0]);
        });
        return files;
    }

    // Given a File object (usually created by all_logs() or by specifying a file name), return the
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
        File[] files = new File(path).listFiles();
        for (File f : files) {
            System.out.println("Trying to delete: " + f.getName());
            if (f.getName().endsWith("_TEST.log")) {
                f.delete();
            }
        }
    }
}
