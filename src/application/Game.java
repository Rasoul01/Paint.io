package application;

import model.Board;
import model.BotPlayer;
import model.HumanPlayer;
import model.Player;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class Game {

    private final CopyOnWriteArrayList<Player> playersList = new CopyOnWriteArrayList<>();
    private final Board board;
    private boolean running = false;
    private int tickCounter = 0;
    private final int tickReset;

    public Game(int gameSpeed, int botCount, int botLevel) {
        board = new Board();

        for (int i = 0; i < botCount; i++ ) {
            playersList.add(new BotPlayer(botLevel));
        }

        int[] speeds = {12, 10, 8, 6, 4};
        tickReset = speeds[gameSpeed - 1];
    }

    public void startGame () {

        for (Player player : playersList) {
            player.spawn(board, playersList);
        }

        running = true;

        // Starts a timer to tick the game logic
        final int INITIAL_DELAY = 0;
        final int PERIOD_INTERVAL = 1000/60;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new ScheduleTask(), INITIAL_DELAY, PERIOD_INTERVAL);
    }

    private void  tick () {
        if(running) {
            for (Player player : playersList) {
                if (player instanceof BotPlayer BP)
                    BP.act(this);
                else
                    player.move();

                player.processMovement(board, playersList);
            }
        }
    }

    private void updateTickCounter(){
        tickCounter++;
        tickCounter %= tickReset;
    }

    public void addPlayer (HumanPlayer newPLayer) {
        if (!running)
            startGame();
        newPLayer.spawn(board, playersList);
        playersList.add(newPLayer);
    }

    public CopyOnWriteArrayList<Player> getPlayersList() {
        return playersList;
    }

    public Board getBoard() {
        return board;
    }

    private class ScheduleTask extends TimerTask {

        //Gets called by timer at specified interval. Calls tick at specified rate
        @Override
        public void run() {
            if(running) {
                updateTickCounter();
                if (tickCounter == 0) {
                    tick();
                }
            }
        }
    }
}
