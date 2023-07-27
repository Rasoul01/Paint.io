package model;

public class Tile {

    Player owner;
    Player trackOwner;

    private final int x, y;   //REMOVE

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

    @Override
    public String toString() {      //REMOVE
        return "[" + x + " , " + y + "]     Owner: " + owner + "    track owner: " + trackOwner;
    }
}