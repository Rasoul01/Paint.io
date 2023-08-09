package model;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;

public class Tile implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    Player owner;
    Player trackOwner;

    private final int x, y;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Player getTrackOwner() {
        return trackOwner;
    }

    public void setTrackOwner(Player trackOwner) {
        this.trackOwner = trackOwner;

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {      //REMOVE
        return "[" + x + " , " + y + "]     Owner: " + owner + "    track owner: " + trackOwner;
    }

    public Color getColor() {
        if (owner != null && trackOwner == null) {
            return owner.getColor();
        } else if (owner == null && trackOwner != null) {
            return Colors.lighter(trackOwner.getColor());
        } else if (owner != null && trackOwner != null) {
            return Colors.blend(owner.getColor(), trackOwner.getColor());
        }
        return new Color(255,255,255,0);
    }
}