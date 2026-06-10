import src.ui.*;
import src.DatabaseManager;

class Main {
  public static void main(String[] args) {
    DatabaseManager.initializeDatabase();
    int userId = Login.promptForUser();
    if (userId != -1) {
      Home.displayHomepage(userId);
    }
  }
}
