package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Board {
    private HashMap<Coordinate, Tile> tilesMap = new HashMap<>();

    public ArrayList<Tile> getAreaTiles (Coordinate topLeftCorner, Coordinate bottomRightCorner) {

        ArrayList<Tile> areaTiles = new ArrayList<>();

        for (int j = topLeftCorner.getY(); j >= bottomRightCorner.getY(); j--)
            for (int i = topLeftCorner.getX(); i <= bottomRightCorner.getX(); i++) {
                areaTiles.add(getTile(new Coordinate(i, j)));
            }
        return areaTiles;
    }

    public void draw (Graphics g, CopyOnWriteArrayList<Player> playersList, HumanPlayer mainPlayer,
                      int colsCount, int rowsCount, int unitSize, int tickCounter, int tickReset) {

//        coordinates start from top-left corner

//        draw Background
        for (int j = 0; j < rowsCount; j++) {
            for (int i = 0; i < colsCount; i++) {
                if ((i + j) % 2 == 0) {
                    g.setColor(Color.lightGray);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fillRect(i*unitSize, j*unitSize, unitSize, unitSize);
            }
        }


        //draw tiles
        ArrayList<Tile> areaTiles = getAreaTiles(new Coordinate(mainPlayer.getX() - (colsCount / 2), mainPlayer.getY() + (rowsCount / 2)),
                new Coordinate(mainPlayer.getX() + (colsCount / 2), mainPlayer.getY() - (rowsCount / 2)));

        int tileCounter = 0;

        for (int j = 0; j < rowsCount; j++) {
            for (int i = 0; i < colsCount; i++) {

                Tile tile = areaTiles.get(tileCounter);

                if (tile.getOwner() == null && tile.getTrackOwner() == null) {
                    g.setColor(new Color(255,255,255,0));
                } else if (tile.getOwner() != null && tile.getTrackOwner() == null) {
                    g.setColor(tile.getOwner().getColor());
                } else if (tile.getOwner() == null && tile.getTrackOwner() != null) {
                    g.setColor(Colors.lighter(tile.getTrackOwner().getColor()));
                } else if (tile.getOwner() != null && tile.getTrackOwner() != null) {
                    g.setColor(Colors.blend(tile.getOwner().getColor(), tile.getTrackOwner().getColor()));
                }

                tileCounter++;
                g.fillRect(i*unitSize, j*unitSize, unitSize, unitSize);

            }
        }


        //draw players
        int drawX, drawY;
        g.setFont(new Font(null, Font.PLAIN, 30));

        for (Player player : playersList) {

            if (Math.abs(player.getX() - mainPlayer.getX()) < ((colsCount / 2) + 1) &&
                    Math.abs(player.getY() - mainPlayer.getY()) < ((rowsCount / 2) + 1)) {

                // x and y position relative to humanPlayer at which player should be drawn
                drawX = (player.getX() - mainPlayer.getX() + (colsCount / 2)) * unitSize;
                drawY = ((-1 * (player.getY() - mainPlayer.getY())) + (rowsCount / 2)) * unitSize;

                // For all other players than mainPlayer we need to smooth animations regarding animation smoothing of humanPlayer
//                if (player != mainPlayer) {
//                    drawX += ((player.getDirectionX() - mainPlayer.getDirectionX()) * unitSize
//                            * ((tickCounter + 1) / (double) tickReset));
//                    drawY += ((player.getDirectionY() - mainPlayer.getDirectionY()) * unitSize
//                            * ((tickCounter + 1) / (double) tickReset));
//                }

                g.setColor(Colors.darker(player.getColor()));
                g.drawString(player.getUsername(), drawX , (int) (drawY - unitSize / 3.0));
                g.fillRoundRect(drawX, drawY, unitSize, unitSize, (int) (unitSize / 2.0), (int) (unitSize / 2.0));
            }
        }

        //other
        g.setFont(new Font(null, Font.BOLD, 30));
        g.setColor(Color.BLACK);
        g.drawString("Point: " + mainPlayer.getOwnedTilesCount(), 50 , 50);
        g.drawString("Coordinate: " + mainPlayer.getX()+ " , " + mainPlayer.getY(), 50 , 100);
    }

    public Tile getTile (Coordinate coordinate) {
        Tile tile = tilesMap.get(coordinate);
        if (tile == null) {
            tile = new Tile(coordinate.getX(), coordinate.getY());
            tilesMap.put(coordinate, tile);
        }
        return tile;
    }
}

