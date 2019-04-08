package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the test suite for StartingPlayerMarker class
 *
 * @author simonebraga
 */
class TestStartingPlayerMarker {

    StartingPlayerMarker marker;
    Player player1 = new Player(Figure.DESTRUCTOR,"nickname1");

    @Test
    void testNormal() {

        marker = new StartingPlayerMarker(player1);
        assertEquals(player1 , marker.getTarget());

    }

}