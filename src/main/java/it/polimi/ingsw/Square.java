package it.polimi.ingsw;

import java.util.ArrayList;

/**
 * Represent a single Square on the map of the game.
 */
public class Square {
    /**
     * Represents the upper border of the Square
     */
    private final Border up;
    /**
     * Represents the inferior border of the Square
     */
    private final Border down;

    /**
     * Represents the left border of the Square
     */
    private final Border left;

    /**
     * Represents the right border of the Square
     */
    private final Border right;

    /**
     * Represent all the players that are positioned in that Square
     */
    private ArrayList<Player> players;

    public Square(Border up, Border down, Border left, Border right) {
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
        this.players = new ArrayList<>();
    }

    public Border getUp() {
        return up;
    }

    public Border getDown() {
        return down;
    }

    public Border getLeft() {
        return left;
    }

    public Border getRight() {
        return right;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    /**
     * Adds a new player to the list of players in the Square.
     * @param player The player that must be added to the list of players in the Square.
     */
    public void addPlayer(Player player){
        getPlayers().add(player);
    }

    /**
     * Removes the player in the parameter from the list of players in the Square.
     * @param player The player that must be removed from the list of players in the Square
     */
    public void removePlayer(Player player){
        getPlayers().remove(player);
    }
}
