package it.polimi.ingsw.model.mapclasses;

import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.cardclasses.AmmoTile;

/**
 * Represents the squares that can contains AmmoTile.
 */
public class TileSquare extends Square {

    /**
     * The AmmoTile contained in the Square. Can be null if there are no AmmoTiles in that Square.
     */
    private AmmoTile tile;

    public TileSquare(Border up, Border down, Border left, Border right) {
        super(up, down, left, right);
        tile = null;
    }

    public AmmoTile getTile() {
        return tile;
    }

    public void setTile(AmmoTile tile) {
        this.tile = tile;
    }

    /**
     * Removes the AmmoTile from the Square. Must be called only when the Square contains a AmmoTile.
     * @return The AmmoTile that has been removed.
     */
    public AmmoTile removeTile(){
        AmmoTile tileRemoved;
        tileRemoved = getTile();
        setTile(null);
        return tileRemoved;
    }

    /**
     * Adds a AmmoTile to the square. Must be called only when the Square doesn't contain an AmmoTile.
     * @param tile The AmmoTile to add to the Square.
     */
    public void addTile(AmmoTile tile){
        setTile(tile);
    }
}