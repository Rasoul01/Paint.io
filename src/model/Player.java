package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Player {

    Direction currentDirection;
    private final Color color;
    String username;
    private boolean isAlive;
    int x, y;
    ArrayList<Tile> trackTilesList = new ArrayList<>();
    ArrayList<Tile> ownedTilesList = new ArrayList<>();

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

        // first check for two player collision so both player would get terminated not just one of them
        // (because of statement (tile.getOwner() == null && tile.getTrackOwner() != this) in lines below)
        for (Player player : playersList) {
            if (player != this) {
                if (player.getX() == this.getX() && player.getY() == this.getY()) {
                    player.terminate(playersList);
                    this.terminate(playersList);
                }
            }
        }

        // Added if statement because player movement would be processed once
        // after termination so one extra track tile would appear on game board
        if (isAlive) {

            Tile tile = board.getTile(new Coordinate(x, y));

            if (tile.getOwner() != this && tile.getTrackOwner() == null) {
                tile.setTrackOwner(this);
                trackTilesList.add(tile);
            } else if (tile.getOwner() == null && tile.getTrackOwner() != this) {
                tile.getTrackOwner().terminate(playersList);
                tile.setTrackOwner(this);
                trackTilesList.add(tile);
            } else if (tile.getOwner() == this && trackTilesList.size() > 0) {
            checkBox();
            }
        }

    }

    private void checkBox() {

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