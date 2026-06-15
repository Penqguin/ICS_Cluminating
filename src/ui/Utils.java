package src.ui;

import java.util.Scanner;

/**
 * Utility class for common UI functions.
 * Provides screen clearing, pausing, sleep, and shared constants.
 */
public class Utils {
  /** Shared scanner instance for all user input across the application */
  public static final Scanner sc = new Scanner(System.in);

  /** ASCII art header banner displayed on UI screens */
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
    
  /** Standard UI width for consistent formatting */
  public static final int STANDARD_WIDTH = 53;

  /** Repeating equal sign line for visual separation */
  public static final String equalSignLine = "=================================================================";

  /**
   * Clears the terminal screen using ANSI escape codes.
   */
  public static void clearScreen() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

  /**
   * Pauses execution until the user presses Enter.
   */
  public static void pauseScreen() {
    System.out.print("  Press [Enter] to continue...");
    sc.nextLine();
  } 

  /**
   * Sleeps the current thread for the specified milliseconds.
   * @param ms The number of milliseconds to sleep
   */
  public static void sleep(int ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
