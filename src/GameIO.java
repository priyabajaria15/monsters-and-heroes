import java.util.Scanner;

public class GameIO {
    private static final Scanner scanner = new Scanner(System.in);

    // Reads an integer safely (prevents crashes)
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                println(" Invalid number! Please enter a valid integer.");
            }
        }
    }

    // Reads an integer with Quit support (Q)
    public static int readIntOrQuit(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("Q")) {
                println(" Exiting game...");
                System.exit(0);
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                println("Invalid number! Enter number or Q to quit.");
            }
        }
    }

    // Read text input (commands, names, etc.)
    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    // Print a message
    public static void println(String msg) {
        System.out.println(msg);
    }

    public static void print(String msg) {
        System.out.print(msg);
    }
}
