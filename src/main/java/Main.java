import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        // Print initial prompt
        System.out.print("$ ");

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();  // Read user input
        
        while (true) {
            if (input.equals("exit 0")) {  // Check if the input is "exit 0"
                System.exit(0);  // Exit with status code 0
        }else if(input.startsWith("echo ")){
            String toEcho =input.substring(5);
            System.out.println(toEcho);
            } else {
                System.out.print(input + ": command not found\n");  // Print error message
                System.out.print("$ ");  // Print prompt for the next command
                
                input = scanner.nextLine();  // Read the next command
            }
        }
    }
}
