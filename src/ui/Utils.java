package src.ui;

import java.util.Scanner;

/**
 * Utility class for common functions like clearing the screen and pausing.
 * And Common things we would want to print over and over again like the header banner, financial summary block, and recent transactions table.
 */

public class Utils {
  public static final Scanner sc = new Scanner(System.in);

  // TODO: If we cant find another place to use a 2d array change this to a 2d array of chars
  public static final String HEADER_BANNER = 
      " /$$$$$$$                  /$$           /$$          \n" +
      "| $$__  $$                | $$          |__/          \n" +
      "| $$  \\ $$ /$$   /$$  /$$$$$$$  /$$$$$$  /$$  /$$$$$$ \n" +
      "| $$$$$$$ | $$  | $$ /$$__  $$ /$$__  $$| $$ /$$__  $$\n" +
      "| $$__  $$| $$  | $$| $$  | $$| $$  \\ $$| $$| $$$$$$$$\n" +
      "| $$  \\ $$| $$  | $$| $$  | $$| $$  | $$| $$| $$_____/\n" +
      "| $$$$$$$/|  $$$$$$/|  $$$$$$$|  $$$$$$$| $$|  $$$$$$$\n" +
      "|_______/  \\______/  \\_______/ \\____  $$|__/ \\_______/\n" +
      "                               /$$  \\ $$              \n" +
      "                              |  $$$$$$/              \n" +
      "                               \\______/               \n";
    
  public static final String equalSignLine = dashGenerator(53, '=');
  public static final String dashLine = dashGenerator(53, '-');

  public static String dashGenerator(int length, char ch) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      sb.append(ch);
    }
    return sb.toString();
  }

  public static void clearScreen() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }
  public static void pauseScreen() {
    System.out.print("  Press [Enter] to continue...");
    sc.nextLine();
  } 

  public static void sleep(int ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
