package model;

import application.GameController;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Player {

    Direction currentDirection;
    private final Color color;
    String username;
    private boolean isAlive;
    int x, y;
    HashSet<Tile> trackTilesList = new HashSet<>();
    HashSet<Tile> ownedTilesList = new HashSet<>();
    long lastLaserShotTime;
    boolean isRocketLaunched = false;

    Random r = new Random();

    public Player(String username) {
        this.username = username;
        color = Colors.getColor();
        currentDirection = Direction.RIGHT;
        isAlive = true;
    }

    public String getUsername() {
        return username;
    }

    public abstract void move();

    public void spawn(Board board, CopyOnWriteArrayList<Player> playersList) {

        int playersCount = playersList.size();
        int bound = playersCount * 10;
        boolean suitable = false;

        do {
            y = r.nextInt(-bound, bound);
            x = r.nextInt(-bound, bound);

            for (Player player : playersList) {
                if ((Math.abs(player.getX() - x) > 10 && Math.abs(player.getY() - y) > 10)) {
                    suitable = true;
                    break;
                }
            }
        } while (!suitable);

        ArrayList<Tile> spawnTiles = board.getAreaTiles(new Coordinate(x - 1, y - 1), new Coordinate(x + 1, y + 1));
        for (Tile tile : spawnTiles) {
            tile.setOwner(this);
            ownedTilesList.add(tile);
        }

        System.out.println(username + ": " + x + " , " + -1 * y);    //REMOVE
    }

    public void processMovement(Board board, CopyOnWriteArrayList<Player> playersList) {

        // remove owned tile if other player has captured it
        ownedTilesList.removeIf(ownedTile -> ownedTile.getOwner() != this);


        Tile tile = board.getTile(new Coordinate(x, y));

        // first check for two player collision so both player would get terminated not just one of them
        // (because of statement (tile.getOwner() == null && tile.getTrackOwner() != this) in lines below)
        for (Player player : playersList) {
            if (player != this) {
                if (player.getX() == this.getX() && player.getY() == this.getY()) {
                    if (tile.getOwner() != player)
                        player.terminate(playersList);
                    if (tile.getOwner() != this)
                        this.terminate(playersList);
                }
            }
        }

        // Added if statement because player movement would be processed once
        // after termination so one extra track tile would appear on game board
        if (isAlive) {

            if (tile.getOwner() != this && tile.getTrackOwner() == null) {
                tile.setTrackOwner(this);
                trackTilesList.add(tile);
            } else if (tile.getTrackOwner() != null && tile.getTrackOwner() != this) {
                tile.getTrackOwner().terminate(playersList);
                tile.setTrackOwner(this);
                trackTilesList.add(tile);
            } else if (tile.getOwner() == this && trackTilesList.size() > 0) {
                fillTrack();
            }
        }

    }

    private void fillTrack() {

        ownedTilesList.addAll(trackTilesList);
        for (Tile tile : trackTilesList) {
            tile.setOwner(this);
            tile.setTrackOwner(null);
        }
        trackTilesList.clear();

        /*
        *
        *
        *
        */
    }

    public void fire(Gun gun, GameController gameController) {
        CopyOnWriteArrayList<Player> playersList = gameController.getPlayersList();
        Board board = gameController.getBoard();
        long now = System.currentTimeMillis();

        if (gun == Gun.ROCKET) {
            if (!isRocketLaunched) {
                isRocketLaunched = true;
                int targetX = x;
                int targetY = y;

                if (currentDirection == Direction.UP) {
                    targetY -= 5;
                } else if (currentDirection == Direction.DOWN) {
                    targetY += 5;
                } else if (currentDirection == Direction.RIGHT) {
                    targetX += 5;
                } else if (currentDirection == Direction.LEFT) {
                    targetX -= 5;
                }

                ArrayList<Tile> targetTiles = board.getAreaTiles(new Coordinate(targetX - 1, targetY - 1), new Coordinate(targetX + 1, targetY + 1));
                for (Tile tile : targetTiles) {
                    tile.setOwner(this);
                    ownedTilesList.add(tile);
                }

                for (Player player : playersList) {
                    if (player.getX() >= targetX - 1 && player.getX() <= targetX + 1 &&
                            player.getY() >= targetY - 1 && player.getY() <= targetY + 1)
                        player.terminate(playersList);
                }
            }
        }

        else if (gun == Gun.LASER) {
            if (now - lastLaserShotTime >= 3000 || lastLaserShotTime == 0) {
                lastLaserShotTime = now;

                for (Player player : playersList) {
                    if (player != this) {
                        if (currentDirection == Direction.UP) {
                            if (player.getX() == x && player.getY() < y)
                                player.terminate(playersList);
                        } else if (currentDirection == Direction.DOWN) {
                            if (player.getX() == x && player.getY() > y)
                                player.terminate(playersList);
                        } else if (currentDirection == Direction.RIGHT) {
                            if (player.getX() > x && player.getY() == y)
                                player.terminate(playersList);
                        } else if (currentDirection == Direction.LEFT) {
                            if (player.getX() < x && player.getY() == y)
                                player.terminate(playersList);
                        }
                    }
                }
            }
        }
    }

    private void terminate(CopyOnWriteArrayList<Player> playersList) {
        isAlive = false;
        for (Tile tile : ownedTilesList) {
            tile.setOwner(null);
        }
        for (Tile tile : trackTilesList) {
            tile.setTrackOwner(null);
        }
        ownedTilesList.clear();
        trackTilesList.clear();
        playersList.remove(this);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Color getColor() {
        return color;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public int getDirectionX() {
        if (currentDirection == Direction.RIGHT) {
            return 1;
        } else if (currentDirection == Direction.LEFT) {
            return -1;
        } else {
            return 0;
        }
    }

    public int getDirectionY() {
        if (currentDirection == Direction.UP) {
            return 1;
        } else if (currentDirection == Direction.DOWN) {
            return -1;
        } else  {
            return 0;
        }
    }

    public int getOwnedTilesCount() {
        return ownedTilesList.size();
    }

    @Override
    public String toString() {
        return username;
    }
}

enum Gun {
    LASER, ROCKET
}