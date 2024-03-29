package it.polimi.ingsw.model.playerclasses;

import it.polimi.ingsw.model.cardclasses.Weapon;

import java.util.ArrayList;

/**
 * This class represents the set of the weapons available to the player
 *
 * @author simonebraga
 */
public class WeaponPocket {

    /**
     * This attribute contains the weapons of the player
     */
    private ArrayList<Weapon> weapons;

    public WeaponPocket() {
        this.weapons = new ArrayList<>();
    }

    public ArrayList<Weapon> getWeapons() {
        return weapons;
    }

    /**
     * This method sets a new value for weapons attribute.<br>
     *     It's mainly used for Jackson JSON files fetching.
     *
     * @param weapons an ArrayList of Weapons enumerated objects that will be the new attribute value.
     * @author Draghi96
     */
    public void setWeapons(ArrayList<Weapon> weapons) {
        this.weapons = weapons;
    }

    /**
     * This method tries to add a weapon in the weapon pocket of the player
     * @param weapon is the weapon to be added
     */
    public void addWeapon(Weapon weapon) {

        if (weapons.size() < 3)
            weapons.add(weapon);

    }

    /**
     * This method is used to add a weapon in the weapon pocket of the player when this is full
     * @param weapon is the weapon to be added
     * @param n is the position of the weapon to be removed
     * @return the weapon to be removed
     */
    public Weapon switchWeapon(Weapon weapon, Integer n) {

        Weapon returnWeapon = weapons.get(n);
        weapons.remove( (int) n);
        weapons.add(weapon);
        return returnWeapon;

    }

}
