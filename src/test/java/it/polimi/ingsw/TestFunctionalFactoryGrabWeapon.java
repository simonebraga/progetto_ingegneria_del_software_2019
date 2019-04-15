package it.polimi.ingsw;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the normal functioning of the function that grabs a Weapon: the Weapon gets added
 * to the Pocket of the Player and the Square contains no more the Weapon.
 * Also tests that, when the Player has already 3 Weapon, the FunctionalEffect throws a
 * FullPocketException.
 */
class TestFunctionalFactoryGrabWeapon {

    SpawnSquare square;
    Weapon weapon;
    FunctionalEffect effect;
    Player player;

    @BeforeEach
    void setUp() {
        player = new Player(Figure.BANSHEE, "Player1");
        square = new SpawnSquare(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING, Color.BLUE);
        player.move(square);
        weapon = new Weapon(new ArrayList<>(), WeaponName.ELECTROSCYTHE, Boolean.TRUE);
        square.addWeapon(weapon);
        effect = new FunctionalFactory().createGrabWeapon(player, weapon);
    }

    @AfterEach
    void tearDown() {
        square = null;
        weapon = null;
        effect = null;
        player = null;
    }

    @Test
    void createGrabWeapon() {
        try {
            effect.doAction();
        }catch (FullPocketException e){
            fail();
        }
        assertTrue(player.getWeaponPocket().getWeapons().contains(weapon));
        assertFalse(square.getWeapons().contains(weapon));
    }
    @Test
    void createGrabWeaponException(){
        try {
            player.getWeaponPocket().addWeapon(new Weapon(new ArrayList<>(), WeaponName.WHISPER, Boolean.TRUE));
        }catch (FullPocketException e){
            fail();
        }

        try {
            player.getWeaponPocket().addWeapon(new Weapon(new ArrayList<>(), WeaponName.FURNACE, Boolean.TRUE));
        }catch (FullPocketException e){
            fail();
        }

        try {
            player.getWeaponPocket().addWeapon(new Weapon(new ArrayList<>(), WeaponName.SHOCKWAVE, Boolean.TRUE));
        }catch (FullPocketException e){
            fail();
        }

        try{
            effect.doAction();
        }catch (FullPocketException e){
            return ;
        }
        fail();
    }
}