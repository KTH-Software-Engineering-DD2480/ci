package ci;

import java.io.*;

public class Ci {
    /**
     * Runs {@code gradle build} in the specified directory, piping any output to the given writer.
     * @param workingDirectory Path to the directory relative to which the command is run.
     * @param outputWriter Where to write the output.
     * @return {@code true} if all tests passed successfully and {@code false} otherwise.
     * @throws IOException
     * @throws InterruptedException if the current thread was interrupted
     */
    public static boolean gradleBuild(String workingDirectory, Writer outputWriter) throws IOException, InterruptedException {
        return execCommand(new String[]{"./gradlew", "build", "--info"}, workingDirectory, outputWriter);
    }

    /**
     * Runs {@code gradle test} in the specified directory, piping any output to the given writer.
     * @param workingDirectory Path to the directory relative to which the command is run.
     * @param outputWriter Where to write the output.
     * @return {@code true} if all tests passed successfully and {@code false} otherwise.
     * @throws IOException
     * @throws InterruptedException if the current thread was interrupted
     */
    public static boolean gradleTest(String workingDirectory, Writer outputWriter) throws IOException, InterruptedException {
        return execCommand(new String[]{"./gradlew", "test", "--info"}, workingDirectory, outputWriter);
    }

    /**
     * Runs the given command and returns whether it succeeded or not.
     * @param command The command to run, with any arguments
     * @param workingDirectory Path to the directory relative to which the command is run
     * @param outputWriter Where to write the output.
     * @return {@code true} if the command exited successfully (exit code 0) and {@code false} if the program had a non-zero exit code.
     * @throws IOException
     * @throws InterruptedException if the current thread was interrupted
     */
    static boolean execCommand(String[] command, String workingDirectory, Writer outputWriter) throws IOException, InterruptedException {

        // Create command to run in pathToDirectory
        ProcessBuilder pb = new ProcessBuilder()
            .directory(new File(workingDirectory)) // all commands are run relative to this directory
            .redirectErrorStream(true) // merge stdout and stderr
            .command(command);
        Process process = pb.start();

        try {
            // We don't need to use BufferedReader, the caller is responsibly for any
            // buffering (through the outputWriter)
            Reader stdInput = new InputStreamReader(process.getInputStream());
            stdInput.transferTo(outputWriter);

            // Wait for the process to exit, yielding its exit code
            int exit = process.waitFor();

            // An exit code of 0 means success by convention
            return exit == 0 ? true : false;
        } finally {
            // Make sure to cleanup the process in case of an exception
            process.destroy();
        }
    }
}
