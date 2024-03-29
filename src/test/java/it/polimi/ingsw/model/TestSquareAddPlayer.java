package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the normal functioning of the method addPlayer: it has to add the new player and it doesn't have to modify the other players in the list
 */
class TestSquareAddPlayer {
    Square square;
    Player player;
    ArrayList<Player> players;

    @BeforeEach
    void setUp() {
        square = new Square(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING,0,0);
        players = new ArrayList<>();
        players.add(new Player(Figure.DESTRUCTOR, "nick1"));
        players.add(new Player(Figure.DOZER, "nick2"));
        players.add(new Player(Figure.VIOLET, "nick3"));
        players.add(new Player(Figure.BANSHEE, "nick4"));
        square.setPlayers((ArrayList<Player>) players.clone());
        player = new Player(Figure.SPROG, "nick5");
        players.add(player);

    }

    @Test
    void addPlayer() {
        square.addPlayer(player);
        assertTrue(square.getPlayers().containsAll(players) && players.containsAll(square.getPlayers()));
    }

    @AfterEach
    void tearDown() {
        square = null;
        player = null;
        players = null;
    }
}