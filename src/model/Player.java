package model;

import application.Game;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Player implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    Direction currentDirection;
    private final Color color;
    String username;
    private boolean isAlive;
    int x, y;
    HashSet<Tile> trackTilesList = new HashSet<>();
    HashSet<Tile> ownedTilesList = new HashSet<>();
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
        }

        System.out.println(username + ": " + x + " , " + y);    //REMOVE
    }

    public void processMovement(Board board, CopyOnWriteArrayList<Player> playersList) {

        // remove owned tile if other player has captured it
        ownedTilesList.removeIf(ownedTile -> ownedTile.getOwner() != this);

        Tile tile = board.getTile(x, y);

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
                fillTrack(board);
            }
        }

    }

    private void fillTrack(Board board) {
        // set track as owned tile
        ownedTilesList.addAll(trackTilesList);
        for (Tile tile : trackTilesList) {
            tile.setOwner(this);
            tile.setTrackOwner(null);
        }
        trackTilesList.clear();

        // filling captured area using flood fill like algorithm
        int maxX = ownedTilesList.stream().toList().get(0).getX();
        int minX = maxX;
        int maxY = ownedTilesList.stream().toList().get(0).getY();
        int minY = maxY;
        for (Tile t : ownedTilesList) {
            if (t.getX() > maxX) maxX = t.getX();
            if (t.getX() < minX) minX = t.getX();
            if (t.getY() > maxY) maxY = t.getY();
            if (t.getY() < minY) minY = t.getY();
        }

        ArrayList<Tile> outside = new ArrayList<>();
        ArrayList<Tile> inside = new ArrayList<>();
        ArrayList<Tile> queue = new ArrayList<>();
        ArrayList<Tile> currArea = new ArrayList<>();

        ArrayList<Tile> areaTile = board.getAreaTiles(new Coordinate(minX, maxY), new Coordinate(maxX, minY));
        for (Tile tile : areaTile) {
            // looping through non-filled tiles to find separate areas that should be filled
            if (tile.getOwner() != this && !outside.contains(tile) && !inside.contains(tile)) {
                queue.add(tile);
                currArea.add(tile);
                boolean isBounded = true;
                while (queue.size() > 0) {

                    Tile currTile = queue.get(queue.size() - 1);
                    queue.remove(queue.size() - 1);
                    int posX = currTile.getX();
                    int posY = currTile.getY();

                    // if area isn't closed until bounds, it is an open area that shouldn't be filled
                    if ((posX == minX || posX == maxX || posY == minY || posY == maxY) && currTile.getOwner() != this)
                        isBounded = false;

                    // Check if the adjacent tiles are valid
                    for (int i = posX - 1; i <= posX + 1; i += 2) {
                        Tile adjacent = board.getTile(i, posY);
                        if (adjacent.getOwner() != this && !currArea.contains(adjacent) &&
                                i <= maxX && i >= minX && posY <= maxY && posY >= minY) {
                            queue.add(adjacent);
                            currArea.add(adjacent);
                        }
                    }

                    for (int j = posY - 1; j <= posY + 1; j += 2) {
                        Tile adjacent = board.getTile(posX, j);

                        if (adjacent.getOwner() != this && !currArea.contains(adjacent) &&
                                posX <= maxX && posX >= minX && j <= maxY && j >= minY) {
                            queue.add(adjacent);
                            currArea.add(adjacent);
                        }
                    }
                }

                if (isBounded) {
                    inside.addAll(currArea);
                } else {
                    outside.addAll(currArea);
                }
                currArea.clear();
            }
        }

        ownedTilesList.addAll(inside);
        for (Tile tile : inside) {
            tile.setOwner(this);
        }
        inside.clear();
        outside.clear();
    }

    public void fire(Gun gun, Game game) {
        CopyOnWriteArrayList<Player> playersList = game.getPlayersList();
        Board board = game.getBoard();

        if (gun == Gun.ROCKET) {
            if (!isRocketLaunched) {
                isRocketLaunched = true;
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
            long now = System.currentTimeMillis();

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
        return '[' + username + ": " + x + " , " + y +']';
    }
}

enum Gun {
    LASER, ROCKET
}