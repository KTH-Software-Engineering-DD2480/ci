package ci;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Arrays;
import java.util.ArrayList;

import org.json.JSONTokener;
import org.json.JSONObject;


public class PersistentLogs {
    public String path;  // The relative path to the folder where the logs are stored.
    public int build_number;    // The build number for easier ordering of logs and unique identifiers

    /**
     * Constructor for the PersistentLogs class.
     * @param path_to_logs_folder - relative path to a place where the logs should be stored.
     */
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

    /**
     * Add a log file with JSON data taken from the parameter log_entry.
     * @param log_entry - The Log_entry object containing data to be written to the log file
     */
    public void add_log(Log_entry log_entry) throws IOException {
        File log_file = new File(path + "/" + this.build_number + "_" + log_entry.generate_log_file_name());
        this.build_number++;
        if (!log_file.exists()) {
            log_file.createNewFile();
        }
        FileWriter fw = new FileWriter(log_file);
        fw.write(log_entry.toString());
        fw.close();
    }

    /**
     * Look into the logs folder and return all *log* files in it, if the directory doesn't exist yet it is created
     * @return An array of all log files in the logs folder
     */
    public File[] all_logs() {
        File[] files_with_README = new File(this.path).listFiles();
        if (files_with_README == null) {
            new File(this.path).mkdir();
            return new File[0];
        }

        // Edge cases
        else if (files_with_README.length == 0) return new File[0];
        else if (files_with_README.length == 1 && files_with_README[0].getName().equals("README.md")) return new File[0];

        ArrayList<File> files = new ArrayList<File>(Arrays.asList(files_with_README));
        files.removeIf(file -> !file.getName().endsWith(".log"));
        File[] sorted_files = files.toArray(new File[files.size()]);
        Arrays.sort(sorted_files, (f1, f2) -> {
            return Integer.parseInt(f1.getName().split("_")[0])
                - Integer.parseInt(f2.getName().split("_")[0]);
        });
        return sorted_files;
    }

    /**
     * Convert and return Log_entry from JSON in file specified by log_file
     * @param log_file - the file to read the JSON data from
     * @return the Log_entry object
     */
    public static Log_entry get_log(File log_file) throws IOException {
        if (!log_file.exists()) return null;
        if (log_file.getName().equals("README.md")) return null;
        FileReader fr = new FileReader(log_file);
        JSONObject json_object = new JSONObject(new JSONTokener(fr));
        Log_entry le = new Log_entry(
            Log_entry.Log_type.valueOf(json_object.getString("type")),
            json_object.getString("repo_url"),
            json_object.getString("refspec"),
            json_object.getString("commit_SHA"),
            new Date(json_object.getLong("date_time")),
            Log_entry.Test_status.valueOf(json_object.getString("status")),
            json_object.getString("gradle_output")
        );
        // Prevent absurd behaviour where file isn't deleted when output stream isn't closed in this terminated function.
        fr.close();
        return le;
    }

    /**
     * Get a range of the most recent log messages
     * @param offset number of log messages to skip (ie. a higher number leads to looking further back the history)
     * @param count number of log messages to return
     * @return the {@code count} most recent log messages, but skipping the first `offset` number of them
     * @throws IOException if a log couldn't be read
     */
    public Log_entry[] getLogRange(int offset, int count) throws IOException {
        File[] files = this.all_logs();

        // clamp `offset` to the valid range `0 <= offset <= files.length`
        offset = Math.min(Math.max(offset, 0), files.length);

        // clamp `count` to the valid range `0 <= count && count + offset <= files.length`
        count = Math.min(Math.max(count, 0), files.length - offset);

        Log_entry[] entries = new Log_entry[count];
        for (int i = 0; i < count; i++) {
            entries[i] = get_log(files[files.length - 1 - (offset + i)]);
        }

        return entries;
    }

    /**
     * Attempt to delete all log files in logs folder
     */
    public void delete_logs() {
        File[] files = new File(this.path).listFiles();
        for (File f : files) {
            System.out.println("Trying to delete: " + f.getName());
            if (f.getName().endsWith(".log")) {
                f.delete();
            }
        }
    }
}
