package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test suit verifies all Weapon class methods.
 *
 * @author Draghi96
 */
class TestWeapon {

    /**
     * This attribute is a list of Color that represent a price for a weapon that will be used for testing.
     */
    private static ArrayList<Color> price;

    /**
     * This attribute is a Weapon object that will be used for testing.
     */
    private static Weapon shotgun;

    /**
     * This method creates all objects used to test all methods in this test suit.
     */
    @BeforeEach
    void setUp(){
        //creating unloaded shotgun
        price = new ArrayList<>();
        price.add(Color.YELLOW);
        price.add(Color.RED);
        shotgun = new Weapon(price, WeaponName.SHOTGUN,false);
    }

    /**
     * This test verifies that reload() set the isLoaded attribute to true.
     */
    @Test
    void reloadSetsIsLoadedToTrue() {

        shotgun.reload(); //method usage

        assertTrue(shotgun.getIsLoaded());
        tearDown();
    }

    /**
     * This test verifies that equals() compares two weapons correctly.
     */
    @Test
    void testEquals() {
        ArrayList<Color> priceZx2 = new ArrayList<>();
        priceZx2.add(Color.YELLOW);
        priceZx2.add(Color.RED);
        Weapon zx2 = new Weapon(priceZx2,WeaponName.ZX2,true);
        assertFalse(shotgun.equals(zx2));
        Weapon anotherShotgun = new Weapon(price,WeaponName.SHOTGUN,false);
        assertTrue(shotgun.equals(anotherShotgun));
    }

    /**
     * This method frees all objects used to test all methods in this test suit.
     */
    @AfterAll
    static void tearDown(){
        price=null;
        shotgun=null;
    }
}