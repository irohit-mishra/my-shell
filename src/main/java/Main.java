import java.util.Scanner;
import java.util.Arrays;
import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String input, typeSubstring;
        String[] commands = {"echo", "exit", "type"};
        String pathEnv = System.getenv("PATH");  // Get the PATH environment variable
        String[] pathDirs = pathEnv.split(":");  // Split it into individual directories

        while (true) {
            System.out.print("$ ");  // Print the prompt
            input = scanner.nextLine();  // Read user input

            if (input.equals("exit 0")) {  // Check if the input is "exit 0"
                scanner.close();  // Close the scanner before exiting
                System.exit(0);  // Exit with status code 0
            } else if (input.startsWith("echo ")) {  // Check if the input starts with "echo "
                System.out.println(input.substring(5));  // Print the extracted part
            } else if (input.startsWith("type ")) {  // Check if the input starts with "type "
                typeSubstring = input.substring(5);  // Extract the command to check

                // Check if it's a shell builtin
                if (Arrays.asList(commands).contains(typeSubstring)) {
                    System.out.println(typeSubstring + " is a shell builtin");
                } else {
                    boolean found = false;

                    // Search through each directory in the PATH
                    for (String dir : pathDirs) {
                        File file = new File(dir, typeSubstring);  // Create a file object for the command
                        if (file.exists() && file.canExecute()) {  // Check if the file exists and is executable
                            System.out.println(typeSubstring + " is " + file.getAbsolutePath());
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        System.out.println(typeSubstring + ": not found");
                    }
                }
            } else {
                System.out.println(input + ": command not found");  // Print error message
            }
        }
    }
}
