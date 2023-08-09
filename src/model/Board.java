package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Board implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final HashMap<Coordinate, Tile> tilesMap = new HashMap<>();

    public ArrayList<Tile> getAreaTiles (Coordinate topLeftCorner, Coordinate bottomRightCorner) {

        ArrayList<Tile> areaTiles = new ArrayList<>();

        for (int j = topLeftCorner.getY(); j >= bottomRightCorner.getY(); j--)
            for (int i = topLeftCorner.getX(); i <= bottomRightCorner.getX(); i++) {
                areaTiles.add(getTile(i, j));
            }
        return areaTiles;
    }

    public Tile getTile (int x, int y) {
        Coordinate coordinate = new Coordinate(x, y);
        Tile tile = tilesMap.get(coordinate);
        if (tile == null) {
            tile = new Tile(coordinate.getX(), coordinate.getY());
            tilesMap.put(coordinate, tile);
        }
        return tile;
    }
}

