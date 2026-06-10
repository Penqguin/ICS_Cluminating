package src.ui;

import java.util.Scanner;

public class Utils {
  public static final Scanner sc = new Scanner(System.in);

  public static void clearScreen() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }
  public static void pauseScreen() {
    System.out.print("  Press [Enter] to continue...");
    sc.nextLine();
  } 
}
