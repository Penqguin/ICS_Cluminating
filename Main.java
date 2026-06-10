import src.ui.*;
import src.DatabaseManager;

class Main {
  public static void main(String[] args) {
    DatabaseManager.initializeDatabase();
    Home.displayHomepage();
  }
}
