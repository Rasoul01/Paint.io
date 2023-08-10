package application;

public class SharedSources {
     public static Game game;

     public static void createGame(int gameSpeed, int botCount, int botLevel) {
         game = new Game(gameSpeed, botCount, botLevel);
     }
}
