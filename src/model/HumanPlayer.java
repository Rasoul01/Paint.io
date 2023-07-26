package model;

import application.GameController;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class HumanPlayer extends Player {

    public HumanPlayer(String username) {
        super(username);
    }

    @Override
    public void move() {

        if (currentDirection == Direction.UP) {
            y--;
        } else if (currentDirection == Direction.DOWN) {
            y++;
        } else if (currentDirection == Direction.RIGHT) {
            x++;
        } else if (currentDirection == Direction.LEFT) {
            x--;
        }
    }

    public void processInput(GameController gameController) {

        gameController.setFocusable(true);
        gameController.requestFocus();
        gameController.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int pressedKey = e.getKeyCode();

                if (pressedKey == 37 || pressedKey == 65){
                    currentDirection = Direction.LEFT;
//                    System.out.println("LEFT"); //REMOVE
                } else if (pressedKey == 38 || pressedKey == 87) {
                    currentDirection = Direction.UP;
//                    System.out.println("UP");
                } else if (pressedKey == 39 || pressedKey == 68) {
                    currentDirection = Direction.RIGHT;
//                    System.out.println("RIGHT");
                } else if (pressedKey == 40 || pressedKey == 83) {
                    currentDirection = Direction.DOWN;
//                    System.out.println("DOWN");
                } else if (pressedKey == 10) {
                    fire();
//                    System.out.println("ENTER");
                    //Enter
                } else if (pressedKey == 32) {
                    fire();
//                    System.out.println("SPACE");
                    //Space
                }

            }
        });
    }

    public void fire() {

    }
}
