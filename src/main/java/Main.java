import java.nio.file.*;
import java.util.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        Set<String> commands = Set.of("echo", "exit", "type", "pwd");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();
            String[] inputParts = input.split(" ", 2);  // Split input into command and arguments
            String command = inputParts[0];  // The command to execute
            String[] arguments = inputParts.length > 1 ? inputParts[1].split(" ") : new String[0];  // The arguments

            if (command.equals("exit") && arguments.length == 1 && arguments[0].equals("0")) {
                scanner.close();  // Close the scanner before exiting
                System.exit(0);
            } else if (command.equals("echo")) {
                System.out.println(String.join(" ", arguments));
            } else if (command.equals("type")) {
                if (arguments.length == 0) {
                    System.out.println("type: missing argument");
                    continue;
                }
                String arg = arguments[0];
                if (commands.contains(arg)) {
                    System.out.printf("%s is a shell builtin%n", arg);
                } else {
                    String path = getPath(arg);
                    if (path == null) {
                        System.out.printf("%s: not found%n", arg);
                    } else {
                        System.out.printf("%s is %s%n", arg, path);
                    }
                }
            } else if (command.equals("pwd")){
                System.out.println(System.getProperty("user.dir"));
            } else{
                String path = getPath(command);
                if (path == null) {
                    System.out.printf("%s: command not found%n", command);
                } else {
                    try {
                        // Build command array including the command and its arguments
                        List<String> commandArgs = new ArrayList<>();
                        commandArgs.add(path);
                        commandArgs.addAll(Arrays.asList(arguments));
                        ProcessBuilder processBuilder = new ProcessBuilder(commandArgs);
                        Process process = processBuilder.start();
                        process.getInputStream().transferTo(System.out);
                        process.getErrorStream().transferTo(System.err);
                        process.waitFor();
                    } catch (IOException | InterruptedException e) {
                        System.out.printf("%s: error executing command%n", command);
                    }
                }
            }
        }
    }

    private static String getPath(String command) {
        String pathEnv = System.getenv("PATH");
        String[] pathDirs = pathEnv.split(":");

        for (String dir : pathDirs) {
            Path filePath = Paths.get(dir, command);
            if (Files.exists(filePath) && Files.isExecutable(filePath)) {
                return filePath.toString();
            }
        }
        return null;
    }
}
