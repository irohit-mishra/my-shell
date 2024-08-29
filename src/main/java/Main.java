import java.util.Scanner;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String input, typeSubstring;
        String[] commands = {"echo", "exit", "type"};

        while (true) {
            System.out.print("$ ");  // Print the prompt
            input = scanner.nextLine();  // Read user input

            if (input.equals("exit 0")) {  // Check if the input is "exit 0"
                scanner.close();  // Close the scanner before exiting
                System.exit(0);  // Exit with status code 0
            } else if (input.startsWith("echo ")) {  // Check if the input starts with "echo "
                System.out.println(input.substring(5));  // Print the extracted part
            } else if (input.startsWith("type")){  //implementation of type builtin
                typeSubstring = input.substring(5);
                if(Arrays.asList(commands).contains(typeSubstring)){
                    System.out.println(typeSubstring + " is a shell builtin");
                } else {
                    System.out.println(typeSubstring + " not found");
                }
            }else{
                System.out.println(input + ": command not found");  // Print error message
            }
        }
    }
}
