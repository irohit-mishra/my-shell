import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.*;
import java.io.IOException;

public class Main {
    // Stores the current working directory of the shell
    private static Path currentDirectory = Paths.get("").toAbsolutePath();

    // Stores alias mappings like alias name -> command
    private static Map<String, String> aliases = new HashMap<>();

    public static void main(String[] args) throws Exception {
        // Set of recognized built-in shell commands
        Set<String> commands = Set.of("echo", "exit", "type", "pwd", "cd", "alias", "clear", "ls");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printPrompt(currentDirectory); // Display prompt with user@dir$
            String input = scanner.nextLine();
            input = resolveAlias(input); // Resolve if any alias is used

            List<String> chainedCommands = splitCommands(input); // Handle multiple commands
            for (String fullCommand : chainedCommands) {
                fullCommand = fullCommand.trim();
                if (fullCommand.isEmpty()) continue;

                String[] inputParts = parseRedirection(fullCommand); // Handle redirection (>, >>, <)
                String command = inputParts[0];
                String[] arguments = Arrays.copyOfRange(inputParts, 1, inputParts.length);

                // Built-in commands handling
                if (command.equals("exit") && arguments.length == 1 && arguments[0].equals("0")) {
                    scanner.close();
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
                } else if (command.equals("pwd")) {
                    System.out.println(currentDirectory.toString());
                } else if (command.equals("cd")) {
                    // Change directory command
                    if (arguments.length == 0) {
                        System.out.println("cd: missing argument");
                        continue;
                    }
                    String newDir = arguments[0];
                    if (newDir.equals("~")) {
                        newDir = System.getenv("HOME");
                    }
                    Path newPath = Paths.get(newDir);
                    if (!newPath.isAbsolute()) {
                        newPath = currentDirectory.resolve(newPath);
                    }
                    newPath = newPath.normalize();
                    if (Files.exists(newPath) && Files.isDirectory(newPath)) {
                        currentDirectory = newPath;
                    } else {
                        System.out.printf("cd: %s: No such file or directory%n", newDir);
                    }
                } else if (command.equals("alias")) {
                    handleAlias(arguments); // Add or list aliases
                } else if (command.equals("clear")) {
                    clearScreen(); // Clear terminal screen
                } else if (command.equals("ls")) {
                    runLs(arguments, currentDirectory); // List directory contents
                } else {
                    // Attempt to execute external command from system PATH
                    String path = getPath(command);
                    if (path == null) {
                        System.out.printf("%s: command not found%n", command);
                    } else {
                        try {
                            List<String> commandArgs = new ArrayList<>();
                            commandArgs.add(path);
                            commandArgs.addAll(Arrays.asList(arguments));
                            ProcessBuilder processBuilder = new ProcessBuilder(commandArgs);
                            processBuilder.directory(currentDirectory.toFile());
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
    }

    // Searches the PATH environment variable for a given executable
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

    // Defines or prints aliases
    private static void handleAlias(String[] arguments) {
        if (arguments.length == 0) {
            aliases.forEach((k, v) -> System.out.println("alias " + k + "='" + v + "'"));
            return;
        }
        for (String arg : arguments) {
            if (arg.contains("=")) {
                String[] parts = arg.split("=", 2);
                String name = parts[0];
                String value = parts[1].replaceAll("^'|'$", "");
                aliases.put(name, value);
            }
        }
    }

    // Replaces alias with corresponding actual command if defined
    private static String resolveAlias(String input) {
        String[] split = input.split(" ", 2);
        if (aliases.containsKey(split[0])) {
            return aliases.get(split[0]) + (split.length > 1 ? " " + split[1] : "");
        }
        return input;
    }

    // Clears the terminal screen (works on Unix terminals)
    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // Implements ls command with optional -l long format
    private static void runLs(String[] args, Path currentDirectory) throws IOException {
        boolean longFormat = Arrays.asList(args).contains("-l");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentDirectory)) {
            for (Path entry : stream) {
                if (longFormat) {
                    long size = Files.size(entry);
                    String perms = Files.getPosixFilePermissions(entry).toString();
                    String modified = Files.getLastModifiedTime(entry).toInstant()
                            .atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm"));
                    System.out.printf("%-10s %8d %s %s%n", perms, size, modified, entry.getFileName());
                } else {
                    System.out.println(entry.getFileName());
                }
            }
        }
    }

    // Parses redirection operators (>, >>, <) and sets input/output streams
    private static String[] parseRedirection(String input) throws IOException {
        if (input.contains(">") || input.contains("<")) {
            String[] parts = input.split("[<>]", 2);
            String[] commandParts = parts[0].trim().split(" ");
            String file = parts[1].trim();
            if (input.contains(">>")) {
                System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(file, true)));
            } else if (input.contains(">")) {
                System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(file)));
            } else if (input.contains("<")) {
                System.setIn(new java.io.FileInputStream(file));
            }
            return commandParts;
        }
        return input.split(" ");
    }

    // Splits chained commands based on ;, &&, || operators
    private static List<String> splitCommands(String input) {
        return List.of(input.split("(?<=;|&&|\\|\\|)"));
    }

    // Prints the shell prompt in the format user@java-shell:path$
    private static void printPrompt(Path currentDirectory) {
        String user = System.getProperty("user.name");
        System.out.printf("%s@java-shell:%s$ ", user, currentDirectory);
    }
}
