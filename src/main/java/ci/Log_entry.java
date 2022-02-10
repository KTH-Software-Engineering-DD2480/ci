package ci;

import java.util.Date;

import org.json.JSONObject;

public class Log_entry {
    public Log_type type;           // The type of the log entry
    public String refspec;          // e.g. refs/heads/master
    public String commit_SHA;       // The SHA of the most recent commit after the push
    public Date date_time;          // The date and time of the push
    public Test_status status;      // Did the build succeed or fail?
    public String gradle_output;    // The console output of the gradle build

    public Log_entry(Log_type type, String refspec, String commit_SHA, Date date_time, Test_status status, String gradle_output) {
        this.type = type;
        this.refspec = refspec;
        this.commit_SHA = commit_SHA;
        this.date_time = date_time;
        this.status = status;
        this.gradle_output = gradle_output;
    }

    public String generate_log_file_name() {
        return type + ".log";
    }

    @Override
    public String toString() {
        JSONObject json_object = new JSONObject();
        json_object.put("type", type.toString());
        json_object.put("refspec", refspec);
        json_object.put("commit_SHA", commit_SHA);
        json_object.put("date_time", date_time.getTime());
        json_object.put("status", status.toString());
        json_object.put("gradle_output", gradle_output);
        return json_object.toString();
    }

    enum Log_type {
        PUSH
    }

    enum Test_status {
        SUCCESS,
        FAILURE
    }
}


