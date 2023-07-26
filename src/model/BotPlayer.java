package model;

import java.util.Random;

public class BotPlayer extends Player {
    private final int level;
    private static int id;

    public BotPlayer(int level) {
        super("Bot");
        setBotUsername();
        this.level = level;
    }

    private void setBotUsername() {
        id++;
        this.username = "Bot" + id;
    }

    @Override
    public void move() {

        Random r = new Random();
        int ran = r.nextInt(20);

        if (ran == 1) {
            currentDirection = Direction.RIGHT;
        } else if (ran == 2) {
            currentDirection = Direction.LEFT;
        } else if (ran == 3) {
            currentDirection = Direction.UP;
        } else if (ran == 4) {
            currentDirection = Direction.DOWN;
        }

        if (currentDirection == Direction.UP) {
            y++;
        } else if (currentDirection == Direction.DOWN) {
            y--;
        } else if (currentDirection == Direction.RIGHT) {
            x++;
        } else if (currentDirection == Direction.LEFT) {
            x--;
        }

        if (level > 1) {


        }
    }
}
