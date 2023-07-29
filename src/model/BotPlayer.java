package model;

import application.GameController;

import java.util.ArrayList;
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
    public void act(GameController gameController) {

        if (level > 1 && !isRocketLaunched) {
            for (Player player : gameController.getPlayersList()) {
                if (player != this) {
                    switch (currentDirection) {
                        case UP -> {
                            if (player.getY() - this.y >= 4 && player.getY() - this.y <= 6 &&
                                    player.getX() - this.x >= -1 && player.getX() - this.x <= 1)
                                fire(Gun.ROCKET, gameController);
                        }
                        case DOWN ->  {
                            if (player.getY() - this.y >= -6 && player.getY() - this.y <= -4 &&
                                    player.getX() - this.x >= -1 && player.getX() - this.x <= 1)
                                fire(Gun.ROCKET, gameController);
                        }
                        case RIGHT ->  {
                            if (player.getX() - this.x >= 4 && player.getX() - this.x <= 6 &&
                                    player.getY() - this.y >= -1 && player.getY() - this.y <= 1)
                                fire(Gun.ROCKET, gameController);
                        }
                        case LEFT ->  {
                            if (player.getX() - this.x >= -6 && player.getX() - this.x <= -4 &&
                                    player.getY() - this.y >= -1 && player.getY() - this.y <= 1)
                                fire(Gun.ROCKET, gameController);
                        }
                    }
                }
            }
        }

        if (level > 2) {
            ArrayList<Tile> areaTiles = gameController.getBoard().getAreaTiles(new Coordinate(x - 4, y + 4),
                    new Coordinate(x + 4, y - 4));

            for (Tile tile : areaTiles) {
                if (tile.getTrackOwner() != this && tile.getTrackOwner() != null) {
                    System.out.println(username + " on Attack to " + tile.getTrackOwner()); //REMOVE
                    moveTo(tile.getX(), tile.getY());
                    return;
                }
            }
        }

        if (trackTilesList.size() < 20) {
            Random r = new Random();
            // Occasionally changes bot direction
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

            move();
        } else {
            moveTo(lastOwnedTile.getX(), lastOwnedTile.getY());
        }
    }

    public void moveTo (int destinationX, int destinationY) {
        int deltaX = destinationX - this.x;
        int deltaY = destinationY - this.y;

        if (Math.abs(deltaX) > 0) {
            if (deltaX < 0)
                x--;
            else if (deltaX > 0)
                x++;
        } else if (Math.abs(deltaY) > 0) {
            if (deltaY < 0)
                y--;
            else if (deltaY > 0)
                y++;
        }
    }
}
