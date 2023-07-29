package application;

import model.Board;
import model.BotPlayer;
import model.HumanPlayer;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameController extends JPanel {

    private HumanPlayer mainPlayer;
    private final CopyOnWriteArrayList<Player> playersList = new CopyOnWriteArrayList<>();
    private final Board board;
    private boolean running = false;

    // make sure colsCount & rowsCount match each other
    // (colsCount = screenWidth / desired unit size & rowsCount = screenHeight / desired unit size)
    // so unitSize = screenHeight / rowsCount = screenWidth / colsCount which will make everything in game square
    private final int screenHeight = 850;
    private final int screenWidth = 1250;
    final int colsCount = 25;
    final int rowsCount = 17;
    private final int unitSize = screenHeight / rowsCount;

    private int tickCounter = 0;
    private final int tickReset;

    public GameController(int gameSpeed) {

        board = new Board();

        int[] speeds = {12, 10, 8, 6, 4};
        tickReset = speeds[gameSpeed - 1];
    }

    public void startGame (int botCount, int botLevel) {

        running = true;

        for (int i = 0; i < botCount; i++ ) {
            playersList.add(new BotPlayer(botLevel));
        }

        for (Player player : playersList)
        {
            player.spawn(board, playersList);
        }

        // Starts a timer to tick the game logic
        final int INITIAL_DELAY = 0;
        final int PERIOD_INTERVAL = 1000/60;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new ScheduleTask(), INITIAL_DELAY, PERIOD_INTERVAL);
    }

    private void  tick () {

        if(running) {
            for (Player player : playersList) {

                if (player instanceof HumanPlayer HP)
                    HP.processInput(this);

                player.move();
                player.processMovement(board, playersList);
            }
        }
    }

    private void updateTickCounter(){
        tickCounter++;
        tickCounter %= tickReset;
    }

    public boolean addPlayer (HumanPlayer newPLayer) {
        boolean exists = false;

        for (Player player : playersList) {
            if (player.getUsername().equals(newPLayer.getUsername())) {
                exists = true;
                break;
            }
        }

        if (exists) {
            return false;
        } else {
            playersList.add(newPLayer);
            mainPlayer = newPLayer;
            return true;
        }
    }

    public CopyOnWriteArrayList<Player> getPlayersList() {
        return playersList;
    }

    public Board getBoard() {
        return board;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        board.draw(g, playersList, mainPlayer, colsCount, rowsCount, unitSize);
//        Toolkit.getDefaultToolkit().sync(); //REMOVE
    }

    private class ScheduleTask extends TimerTask {

        //Gets called by timer at specified interval. Calls tick at specified rate and repaint each time
        @Override
        public void run() {
            if(running) {
                updateTickCounter();
                if (tickCounter == 0) {
                    tick();
                }
                repaint();
            }
        }
    }
}
