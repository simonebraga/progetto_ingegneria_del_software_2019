package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.mapclasses.GameMap;
import it.polimi.ingsw.model.mapclasses.Square;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the normal functioning of the method getDistance.
 */
class TestGameMapGetDistance {
    private GameMap gamemap;
    private ArrayList<Square> expectedDistance;
    private ArrayList<Square> distance;

    @BeforeEach
    void setUp() {
        Square[][] grid;
        grid = new Square[4][4];
        expectedDistance = new ArrayList<>();
        grid[0][0] = new Square(Border.WALL, Border.WALL, Border.WALL, Border.NOTHING,0,0);
        grid[0][1] = new Square(Border.WALL, Border.DOOR, Border.NOTHING, Border.NOTHING,0,1);
        grid[0][2] = new Square(Border.WALL, Border.WALL, Border.NOTHING, Border.NOTHING,0,2);
        grid[0][3] = new Square(Border.WALL, Border.NOTHING, Border.NOTHING, Border.WALL,0,3);
        grid[1][0] = new Square(Border.NOTHING, Border.NOTHING, Border.WALL, Border.WALL,1,0);
        grid[1][1] = new Square(Border.DOOR, Border.NOTHING, Border.WALL, Border.WALL,1,1);
        grid[1][2] = new Square(Border.WALL, Border.NOTHING, Border.NOTHING, Border.WALL,1,2);
        grid[1][3] = new Square(Border.NOTHING, Border.NOTHING, Border.WALL, Border.WALL,1,3);
        grid[2][0] = new Square(Border.NOTHING, Border.WALL, Border.WALL, Border.WALL,2,0);
        grid[2][1] = new Square(Border.NOTHING, Border.DOOR, Border.WALL, Border.NOTHING,2,1);
        grid[2][2] = new Square(Border.NOTHING, Border.DOOR, Border.NOTHING, Border.WALL,2,2);
        grid[2][3] = new Square(Border.NOTHING, Border.WALL, Border.WALL, Border.WALL,2,3);
        grid[3][0] = new Square(Border.WALL, Border.WALL, Border.WALL, Border.NOTHING,3,0);
        grid[3][1] = new Square(Border.DOOR, Border.WALL, Border.NOTHING, Border.WALL,3,1);
        grid[3][2] = new Square(Border.DOOR, Border.WALL, Border.WALL, Border.NOTHING,3,2);
        grid[3][3] = new Square(Border.WALL, Border.WALL, Border.NOTHING, Border.WALL,3,3);
        gamemap = new GameMap(grid, new ArrayList<>(), new ArrayList<>());
        expectedDistance.add(grid[0][1]);
        expectedDistance.add(grid[3][0]);
        distance = gamemap.getDistance(grid[2][2], 3);
    }

    @AfterEach
    void tearDown() {
        gamemap = null;
        expectedDistance = null;
        distance = null;
    }

    @Test
    void getDistance() {
        assertTrue(distance.containsAll(expectedDistance));
        assertTrue(expectedDistance.containsAll(distance));
    }
}