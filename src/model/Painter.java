package model;

import java.awt.*;
import java.util.ArrayList;

public class Painter {

    private Graphics g;

    public void drawAll (Graphics g, Board board, ArrayList<Player> playersList, HumanPlayer mainPlayer,
                         int colsCount, int rowsCount, int unitSize) {

        // coordinates start from top-left corner
        this.g = g;
        drawBackground(colsCount, rowsCount, unitSize);
        if (board != null && playersList != null && mainPlayer != null) {
            drawTiles(board, colsCount, rowsCount, unitSize, mainPlayer);
            drawPlayers(colsCount, rowsCount, unitSize, playersList, mainPlayer);
            drawStatus(mainPlayer);
        }
    }

    private void drawBackground(int colsCount, int rowsCount, int unitSize) {

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
    }

    private void drawTiles(Board board, int colsCount, int rowsCount, int unitSize, HumanPlayer mainPlayer) {

        ArrayList<Tile> areaTiles = board.getAreaTiles(
                new Coordinate(mainPlayer.getX() - (colsCount / 2), mainPlayer.getY() + (rowsCount / 2)),
                new Coordinate(mainPlayer.getX() + (colsCount / 2), mainPlayer.getY() - (rowsCount / 2))
        );

        int tileCounter = 0;

        for (int j = 0; j < rowsCount; j++) {
            for (int i = 0; i < colsCount; i++) {

                Tile tile = areaTiles.get(tileCounter);
                tileCounter++;

                g.setColor(tile.getColor());
                g.fillRect(i*unitSize, j*unitSize, unitSize, unitSize);
            }
        }
    }

    private void drawPlayers(int colsCount, int rowsCount, int unitSize, ArrayList<Player> playersList, HumanPlayer mainPlayer) {
        int drawX, drawY;
        g.setFont(new Font(null, Font.PLAIN, 30));

        for (Player player : playersList) {

            if (Math.abs(player.getX() - mainPlayer.getX()) < ((colsCount / 2) + 1) &&
                    Math.abs(player.getY() - mainPlayer.getY()) < ((rowsCount / 2) + 1)) {

                // x and y position relative to humanPlayer at which player should be drawn
                drawX = (player.getX() - mainPlayer.getX() + (colsCount / 2)) * unitSize;
                drawY = ((-1 * (player.getY() - mainPlayer.getY())) + (rowsCount / 2)) * unitSize;

                g.setColor(Colors.darker(player.getColor()));
                g.drawString(player.getUsername(), drawX , (int) (drawY - unitSize / 3.0));
                g.fillRoundRect(drawX, drawY, unitSize, unitSize, (int) (unitSize / 2.0), (int) (unitSize / 2.0));
            }
        }
    }

    private void drawStatus(Player mainPlayer) {
        g.setFont(new Font(null, Font.BOLD, 30));
        g.setColor(Color.BLACK);
        g.drawString("Point: " + mainPlayer.getOwnedTilesCount(), 50 , 50);
        g.drawString("Coordinate: " + mainPlayer.getX()+ " , " + mainPlayer.getY(), 50 , 100);
    }
}
