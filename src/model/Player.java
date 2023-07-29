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
    Tile lastOwnedTile;
    long lastLaserShotTime;
    boolean isRocketLaunched = false;

    public Player(String username) {
        this.username = username;
        color = Colors.getColor();
        currentDirection = Direction.RIGHT;
        isAlive = true;
    }

    public String getUsername() {
        return username;
    }

    public void move() {
        switch (currentDirection) {
            case UP -> y++;
            case DOWN -> y--;
            case RIGHT -> x++;
            case LEFT -> x--;
        }
    }

    public abstract void act(GameController gameController);

    public void spawn(Board board, CopyOnWriteArrayList<Player> playersList) {

        int playersCount = playersList.size();
        int bound = playersCount * 10;
        boolean suitable = false;
        Random r = new Random();

        do {
            y = r.nextInt(-bound, bound);
            x = r.nextInt(-bound, bound);

            for (Player player : playersList) {
                if (player != this) {
                    if (Math.abs(player.getX() - x) < 10 || Math.abs(player.getY() - y) < 10) {
                        suitable = false;
                        break;
                    }
                    suitable = true;
                }
            }
        } while (!suitable);

        ArrayList<Tile> spawnTiles = board.getAreaTiles(new Coordinate(x - 1, y + 1), new Coordinate(x + 1, y - 1));
        for (Tile tile : spawnTiles) {
            tile.setOwner(this);
            ownedTilesList.add(tile);
            lastOwnedTile = tile;
        }

        System.out.println(username + ": " + x + " , " + y);    //REMOVE
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
            }

            if (tile.getTrackOwner() != null && tile.getTrackOwner() != this) {
                tile.getTrackOwner().terminate(playersList);
                tile.setTrackOwner(this);
                trackTilesList.add(tile);
            }

            if (tile.getOwner() == this && trackTilesList.size() > 0) {
                fillTrack();
            }
        }

    }

    private void fillTrack() {

        ownedTilesList.addAll(trackTilesList);
        for (Tile tile : trackTilesList) {
            tile.setOwner(this);
            tile.setTrackOwner(null);
            lastOwnedTile = tile;
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
//                isRocketLaunched = true;
                int targetX = x;
                int targetY = y;

                switch (currentDirection) {
                    case UP -> targetY += 5;
                    case DOWN -> targetY -= 5;
                    case RIGHT -> targetX += 5;
                    case LEFT -> targetX -= 5;
                }

                for (Player player : playersList) {
                    if (player.getX() >= targetX - 1 && player.getX() <= targetX + 1 &&
                            player.getY() >= targetY - 1 && player.getY() <= targetY + 1)
                        player.terminate(playersList);
                }

                ArrayList<Tile> targetTiles = board.getAreaTiles(new Coordinate(targetX - 1, targetY + 1), new Coordinate(targetX + 1, targetY - 1));
                for (Tile tile : targetTiles) {
                    tile.setOwner(this);
                    ownedTilesList.add(tile);
                }
            }
        }

        else if (gun == Gun.LASER) {
            if (now - lastLaserShotTime >= 3000 || lastLaserShotTime == 0) {
                lastLaserShotTime = now;

                for (Player player : playersList) {
                    if (player != this) {
                        switch (currentDirection) {
                            case UP -> {
                                if (player.getX() == x && player.getY() > y)
                                    player.terminate(playersList);
                            }
                            case DOWN -> {
                                if (player.getX() == x && player.getY() < y)
                                    player.terminate(playersList);
                            }
                            case RIGHT -> {
                                if (player.getX() > x && player.getY() == y)
                                    player.terminate(playersList);
                            }
                            case LEFT -> {
                                if (player.getX() < x && player.getY() == y)
                                    player.terminate(playersList);
                            }
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

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
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