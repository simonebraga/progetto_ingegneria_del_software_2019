package it.polimi.ingsw.model.playerclasses;

import it.polimi.ingsw.model.cardclasses.Powerup;

import java.util.ArrayList;

/**
 * This class represents the set of the powerups available to the player
 *
 * @author simonebraga
 */
public class PowerupPocket {

    /**
     * This attribute contains the powerups of the player
     */
    private ArrayList<Powerup> powerups;

    public PowerupPocket() {
        this.powerups = new ArrayList<>();
    }

    public ArrayList<Powerup> getPowerups() {
        return powerups;
    }

    /**
     * This method sets a new value for powerups attribute.<br>
     *     It's mainly used for Jackson JSON files fetching.
     *
     * @param powerups an ArrayList of Powerup enumerated objects that will be the new attribute value.
     * @author Draghi96
     */
    public void setPowerups(ArrayList<Powerup> powerups) {
        this.powerups = powerups;
    }

    /**
     * This method tries to add a powerup in the powerup pocket of the player
     * @param powerup is the powerup to be added
     */
    public void addPowerup(Powerup powerup) {

        if (powerups.size() < 3)
            powerups.add(powerup);

    }

    /**
     * This method is used to remove a powerup in a specific index
     * @param n is the index of the powerup to be removed
     * @return the removed powerup
     */
    public Powerup removePowerup(Integer n) {

        Powerup returnPowerup = powerups.get(n);
        powerups.remove( (int) n);
        return returnPowerup;

    }
}
