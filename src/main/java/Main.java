import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        // Uncomment this block to pass the first stage
        System.out.print("$ ");

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while(!input.equals(" ")){
            System.out.print(input + ": command not found\n");
            System.out.print("$ ");
        input = scanner.nextLine();
    }
    scanner.close();
}
}