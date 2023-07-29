package model;

import application.GameController;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class HumanPlayer extends Player {

    public HumanPlayer(String username) {
        super(username);
    }

    @Override
    public void act(GameController gameController) {

        gameController.setFocusable(true);
        gameController.requestFocus();
        gameController.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int pressedKey = e.getKeyCode();

                if (pressedKey == 37 || pressedKey == 65){
                    currentDirection = Direction.LEFT;
                } else if (pressedKey == 38 || pressedKey == 87) {
                    currentDirection = Direction.UP;
                } else if (pressedKey == 39 || pressedKey == 68) {
                    currentDirection = Direction.RIGHT;
                } else if (pressedKey == 40 || pressedKey == 83) {
                    currentDirection = Direction.DOWN;
                } else if (pressedKey == 10) {
                    fire(Gun.ROCKET, gameController);
                } else if (pressedKey == 32) {
                    fire(Gun.LASER, gameController);
                }
            }
        });

        move();
    }
}
