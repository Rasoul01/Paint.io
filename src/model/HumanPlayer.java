package model;

import application.Game;
import application.SharedSources;

import java.io.Serial;

public class HumanPlayer extends Player {
    @Serial
    private static final long serialVersionUID = 1L;

    public HumanPlayer(String username) {
        super(username);
    }
    
    public void processInput(int keyCode) {
        Game game = SharedSources.game;

        switch(keyCode) {
            case 37, 65 -> currentDirection = Direction.LEFT;
            case 38, 87 -> currentDirection = Direction.UP;
            case 39, 68 -> currentDirection = Direction.RIGHT;
            case 40, 83 -> currentDirection = Direction.DOWN;
            case 10 -> fire(Gun.ROCKET, game);
            case 32 -> fire(Gun.LASER, game);
        }
    }
}
