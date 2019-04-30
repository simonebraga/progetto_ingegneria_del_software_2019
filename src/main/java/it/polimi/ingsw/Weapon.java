package it.polimi.ingsw;

import java.util.ArrayList;

/**
 * This class represent all in-game weapons.
 * <p>Instances of this class will be used as components for Run-Time combinations of effects.</p>
 *
 * @author Draghi96
 * @see FunctionalFactory
 */
public class Weapon implements Card{

    /**
     * This attribute is an arraylist containing the list of ammo required to load the weapon.
     * It is initialized when the weapon is created, and it is final because weapon price does not change.
     */
    private final ArrayList<Color> price;

    /**
     * This attribute is the enumeration representing the weapon identifier.
     * <p>Only one instance per identifier must exist in the game.</p>
     */
    private WeaponName name;

    /**
     * This attribute is a boolean that knows if the weapon is loaded or not.
     */
    private Boolean isLoaded;

    /**
     * This constructor initializes a weapon card instance.
     * <p>This constructor should only be invoked in a game setup phase.</p>
     *
     * @param price the arraylist of ammunition.
     * @param name the weapon identifier.
     * @param isLoaded a boolean that indicates if a weapon is loaded or not.
     */
    public Weapon(ArrayList<Color> price, WeaponName name, Boolean isLoaded) {
        this.price = price;
        this.name = name;
        this.isLoaded = isLoaded;
    }

    /**
     * This method return an arraylist containing all ammo boxes to be paid for a full reload of a weapon.
     * <p>The purchase price for a weapon shouldn't consider the ammo box in the first position of the list.</p>
     *
     * @return an arraylist of a Color enumeration representing the full reload price of a weapon.
     */
    public ArrayList<Color> getPrice() {
        return price;
    }

    /**
     * This method returns the weapon identifier.
     *
     * @return a WeaponName enumeration to identify all weapons.
     */
    public WeaponName getName() {
        return name;
    }

    /**
     * This method returns the loaded state of a gun.
     * <p>If a isLoaded attribute is false the weapon is not loaded and vice versa.</p>
     *
     * @return a boolean that indicates if the weapon is loaded or not.
     */
    public Boolean getLoaded() {
        return isLoaded;
    }

    /**
     * This method reloads a weapon.
     */
    public void reload() {
        isLoaded=true;
    }
}
