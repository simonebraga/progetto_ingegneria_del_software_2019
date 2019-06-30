package it.polimi.ingsw.view.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.model.gamelogic.settings.SettingsJSONParser;
import it.polimi.ingsw.model.mapclasses.GameMap;
import it.polimi.ingsw.model.mapclasses.SpawnSquare;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.mapclasses.TileSquare;
import it.polimi.ingsw.model.smartmodel.*;
import it.polimi.ingsw.view.Client;
import it.polimi.ingsw.view.ViewInterface;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * This class contains the command line interface implementation of the client.
 *
 * @author Draghi96
 */
public class CliMain implements ViewInterface {

    ////////////////////////////////////////////////////////////// cli formatting constants ///////////////

    /**
     * This final attribute represents the ANSI code for font reset.
     */
    public static final String ANSI_RESET = "\u001B[0m";

    /**
     * This final attribute represents the ANSI code black font color.
     */
    public static final String ANSI_BLACK = "\u001B[30m";

    /**
     * This final attribute represents the ANSI code red font color.
     */
    public static final String ANSI_RED = "\u001B[31m";

    /**
     * This final attribute represents the ANSI code green font color.
     */
    public static final String ANSI_GREEN = "\u001B[32m";

    /**
     * This final attribute represents the ANSI code yellow font color.
     */
    public static final String ANSI_YELLOW = "\u001B[33m";

    /**
     * This final attribute represents the ANSI code blue font color.
     */
    public static final String ANSI_BLUE = "\u001B[34m";

    /**
     * This final attribute represents the ANSI code purple font color.
     */
    public static final String ANSI_PURPLE = "\u001B[35m";

    /**
     * This final attribute represents the ANSI code cyan font color.
     */
    public static final String ANSI_CYAN = "\u001B[36m";

    /**
     * This final attribute represents the ANSI code white font color.
     */
    public static final String ANSI_WHITE = "\u001B[37m";

    /**
     * This finale attribute represents the UNICODE code for the "no ammo" symbol.
     */
    private static final String UNICODE_NO_AMMO = "\uD83D\uDEC7";

    /**
     * This finale attribute represents the UNICODE code for a skull symbol.
     */
    private static final String UNICODE_SKULL = "\u2620";

    /**
     * This finale attribute represents the UNICODE code for a perpendicular symbol.
     */
    private static final String UNICODE_UPPER_DOOR_THRESHOLD = "\u27C2";

    /**
     * This final attribute defines squares width in cli printing.
     */
    private static final int SQUARES_WIDTH = 22;

    /**
     * This final attribute defines squares high in cli printing.
     */
    private static final int SQUARES_HIGH = 11;

    /**
     * This final attribute defines the maximum number of character of a player nickname that will be printed.
     */
    private static final int NICK_PRINT_SIZE = 20;

    /**
     * This final attribute defines the maximum number of squares per row.
     */
    private static final int MAX_SQUARES_BY_ROW = 4;

    /**
     * This final attribute defines how many characters compose a full horizontal grid line.
     */
    private static final int TOTAL_GRID_WIDTH = (SQUARES_WIDTH * MAX_SQUARES_BY_ROW) - MAX_SQUARES_BY_ROW + 1;

    /**
     * This final attribute defines the first line index of the content square section.
     */
    private static final int SQUARE_CONTENT_SECTION_STARTING_INDEX = 2;

    /**
     * This final attribute defines the first line index of the figure square section.
     */
    private static final int SQUARE_FIGURE_SECTION_STARTING_INDEX = 5;

    /**
     * This final attribute defines the maximum amount of character a generic info would be printed.
     */
    private static final int MAX_INFO_SIZE = 6;


    ////////////////////////////////////////////// match related constants /////////////////////////////////////////////

    /**
     * This final attribute defines the maximum amount of weapons stored in a single spawn square.
     */
    private static final int MAX_WEAPONS_BY_SQUARE = 3;

    /**
     * This constant represents the number of maps available in the game.
     */
    private static final int GAME_MAPS_NUMBER = 4;

    /**
     * This constant represents the JSON file path containing all game maps.
     */
    private static final String MAPS_RESOURCES_PATH = "maps.json";

    ////////////////////////////////////// network related constants ///////////////////////////////////////

    /**
     * This final attribute defines the value returned from client.login() method when the registration was successful.
     */
    private static final int SUCCESSFUL_REGISTRATION_SIGNAL = 0;

    /**
     * This final attribute defines the value returned from client.login() method when the nickname was already in use.
     */
    private static final int NICK_NOT_AVAILABLE_SIGNAL = 1;

    /**
     * This final attribute defines the value returned from client.login() method when the login was successful.
     */
    private static final int SUCCESSFUL_LOGIN_SIGNAL = 2;

    /**
     * This final attribute defines the value returned from client.login() method when another player is already logged in with that nickname.
     */
    private static final int NICK_ALREADY_LOGGED_IN_SIGNAL = 3;

    /**
     * This final attribute defines the value returned from client.login() method when it fails to create a new client.
     */
    private static final int LOGIN_FAILURE_SIGNAL = 4;

    ////////////////////////////////////////////// class attributes /////////////////////////////////////////////////////

    /**
     * This attribute is the input scanner.
     */
    private static Scanner scannerIn = new Scanner(System.in);

    /**
     * This attribute is the Client object that communicates with the server.
     */
    private static Client client;

    /**
     * This attribute represents the client nickname.
     */
    private String nickname;

    /**
     * This attribute represents the client's simplified view of the model.
     */
    private SmartModel model;

    /**
     * This attribute represents the game map in use for this match.
     */
    private GameMap map;

    /**
     * This attribute contains the game settings loaded from file.
     */
    private SettingsJSONParser settings;

    /**
     * This final attribute defines the 'game_settings.json' file path.
     */
    private static final String GAME_SETTINGS_PATH = "game_settings.json";

////////////////////////////////////////////////////////////// class  methods ////////////////////////////////////////////////

    /**
     * This method clears the console's content.
     */
    public static void clearScreen() throws IOException, InterruptedException{
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }

    //TODO(erase this method after finishing class)
    /**
     * This method is temporarily used to quickly run this class methods.
     */
    public static void main(String[] args) {
        CliMain temp = new CliMain();
        temp.launch();
    }

    /**
     * This method implements the application using CLI.
     */
    public void launch() {

        if (settings == null)
            fetchSettings();

        System.out.println("Welcome to Adrenaline!\n");

        //create client
        try {
            client = new Client(chooseNetworkTechnology(),this);
            System.out.println("Client created");
        } catch (Exception e) {
            System.out.println("Server not responding.\n");
            launch();
        }

        chooseNickName();
    }

    /**
     * This method fetches game settings from a JSON file.
     */
    private void fetchSettings() {

        //load game settings from "game_settings.json" file
        InputStream settingsFile = CliMain.class.getClassLoader().getResourceAsStream(GAME_SETTINGS_PATH);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            settings = objectMapper.readValue(settingsFile, SettingsJSONParser.class);
            settingsFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method asks the user which nickname the user wish to use.
     */
    private void chooseNickName() {

        int loginOutput;
        boolean success = false;
        do {
            System.out.print("Choose a nickname: ");
            if (scannerIn.hasNextLine())
                nickname = scannerIn.nextLine();
            loginOutput = client.login(nickname);

            switch (loginOutput) {
                case SUCCESSFUL_REGISTRATION_SIGNAL : {
                    System.out.println(ANSI_GREEN + "Successful registration" + ANSI_RESET);
                    success = true;
                    break;
                }
                case NICK_NOT_AVAILABLE_SIGNAL : {
                    System.out.println(ANSI_RED + "Nickname already chosen" + ANSI_RESET);
                    break;
                }
                case SUCCESSFUL_LOGIN_SIGNAL : {
                    System.out.println("Logged in");
                    success = true;
                    break;
                }
                case NICK_ALREADY_LOGGED_IN_SIGNAL : {
                    System.out.println(ANSI_RED + "Already logged in" + ANSI_RESET);
                    break;
                }
                case LOGIN_FAILURE_SIGNAL : {
                    System.out.println(ANSI_RED + "Something went wrong" + ANSI_RESET);
                    break;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + loginOutput);
            }
        } while (!success);
    }

    /**
     * This method asks the user which network technology the user wish to use choosing between RMI and Socket technologies.
     *
     * @return 1 if user choose socket technology or 0 if user choose RMI technology.
     */
    private int chooseNetworkTechnology() {

        Integer rmiOrCli = -1;
        do {
            System.out.print("Choose network technology (0 - RMI | 1 - Socket): ");
            if (scannerIn.hasNextInt()){
                rmiOrCli = scannerIn.nextInt();
            }
            scannerIn.nextLine();
            try {
                clearScreen();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (rmiOrCli != 0 && rmiOrCli != 1);
        return rmiOrCli;
    }

    /**
     * This method printf the match to the CLI.
     */
    private void printModel() {

        ArrayList<String> nicknames = new ArrayList<>(model.getSmartPlayerMap().keySet());

        if (map == null)
            fetchMaps();

        printKillShotTrack();
        printSceneSpacing();
        printMap();
        printSceneSpacing();

        for (String nick : nicknames) {
            if (nick.equals(nickname))
                printBoard(nick, model.getSmartPlayerMap().get(nick), true);
            else
                printBoard(nick, model.getSmartPlayerMap().get(nick), false);

            printSceneSpacing();
        }
    }

    /**
     * This methods prints a separation space with a bar in the command line.
     */
    private void printSceneSpacing() {

        System.out.println();
        System.out.println();
        for (int i = 0; i < TOTAL_GRID_WIDTH; i++) {
            System.out.print("-");
        }
        System.out.println();
        System.out.println();
    }

    /**
     * This method fetches maps from JSON and select the current match map.
     */
    private void fetchMaps() {
        ObjectMapper mapper = new ObjectMapper();
        InputStream gameMapsInputStream = CliMain.class.getClassLoader().getResourceAsStream(MAPS_RESOURCES_PATH);
        GameMap[] gameMaps = null;
        try {
            gameMaps = mapper.readValue(gameMapsInputStream, GameMap[].class);
            gameMapsInputStream.close();
        } catch (IOException e) {
            System.out.println("Cannot fetch maps from file.");
            e.printStackTrace();
        }
        map = gameMaps[model.getMapIndex()];
    }

    /**
     * This method prints the maps grid to the command line.
     */
    private void printMap() {

        for (int i = 0; i < map.getGrid().length; i++) {
            if (i == 0)
                printRow(map.getGrid()[i], map.getSpawnSquares(),true);
            else
                printRow(map.getGrid()[i], map.getSpawnSquares(),false);
        }


        //close last line
        ArrayList<Border> closingBorders = new ArrayList<>();
        for (int i = 0; i < MAX_SQUARES_BY_ROW; i++) {
            closingBorders.add(Border.WALL);
        }

        printFirstLine(closingBorders);
    }


    /**
     * This method prints a grid map row to the command line.
     *
     * @param squares an array of Square to be formatted and printed.
     * @param spawnSquares a list of SpawnSquare that contains all squares that are not TileSquare nor void squares.
     * @param isFirstRow a boolean flag that says if the row printed is the first grid row.
     */
    private void printRow(Square[] squares, ArrayList<SpawnSquare> spawnSquares, boolean isFirstRow) {

        //retrieve fundamental info from row status
        ArrayList<Border> upperBorders = retrieveBordersInfo(squares,"up", isFirstRow);
        ArrayList<Border> leftBorders = retrieveBordersInfo(squares,"left", isFirstRow);
        Border rightMostBorder = retrieveLastBorderInfo(squares);
        ArrayList<ArrayList<Figure>> figuresInsideSquares = getFiguresInsideRow(squares);
        ArrayList<String> squareTypes = new ArrayList<>();
        ArrayList<ArrayList<String>> squareContentInfo = getContentOfEachSquare(squares,spawnSquares,squareTypes);

        printFirstLine(upperBorders);
        printSpawnTagsLine(squares, leftBorders, rightMostBorder, spawnSquares, 1);

        //all square content info lines
        for (int i = SQUARE_CONTENT_SECTION_STARTING_INDEX; i < SQUARE_FIGURE_SECTION_STARTING_INDEX; i++) {

            ArrayList<String> contentInThisRow = new ArrayList<>();
            for (ArrayList<String> contents : squareContentInfo) {
                contentInThisRow.add(contents.get(i));
            }
            printContentLine(leftBorders, contentInThisRow, i, rightMostBorder);

        }

        //all players in square nicknames
        for (int i = SQUARE_FIGURE_SECTION_STARTING_INDEX; i < SQUARES_HIGH - 1; i++) {

            ArrayList<Figure> figuresInThisRow = new ArrayList<>();
            for (ArrayList<Figure> figures : figuresInsideSquares) {
                figuresInThisRow.add(figures.get(i));
            }

            printFigureLine(leftBorders, figuresInThisRow, i, rightMostBorder);
        }
    }

    /**
     * This method prints spaces from a row index to another. Indexes are included in the count.
     *
     * @param from integer index from where to start.
     * @param to integer index to where to stop.
     */
    private void printSpacesFromIndexToIndex(int from, int to) {

        for (int j = from; j <= to; j++) {
            System.out.print(" ");
        }
    }

    /**
     * This method prints the first characters line of a map grid row to the command line.
     *
     * @param upperBorders a list of Border containing the upper border of each square by the position of such square in the row.
     */
    private void printFirstLine(ArrayList<Border> upperBorders) {

        for (Border upperBorder : upperBorders) {

            switch (upperBorder) {
                case DOOR: {
                    System.out.print("+--|");
                    printSpacesFromIndexToIndex(4, SQUARES_WIDTH - 5);
                    System.out.print("|--");
                    break;
                }
                case NOTHING: {
                    System.out.print("+-");
                    printSpacesFromIndexToIndex(2, SQUARES_WIDTH - 3);
                    System.out.print("-");
                    break;
                }
                case WALL: {
                    System.out.print("+");
                    for (int k = 0; k < SQUARES_WIDTH - 2; k++) {
                        System.out.print("-");
                    }
                }
            }
        }
        System.out.println("+");
    }

    /**
     * This method prints the border character of a square box depending on the type of the left square's border.
     *
     * @param border a Border of a square.
     * @param rowIndex an integer which is the current printing row index relative to a square box high (0 is the first box line index).
     */
    private void printBorderChar(Border border, int rowIndex) {

        switch (border) {
            case DOOR:{
                if (rowIndex >= 3 && rowIndex <= SQUARES_HIGH - 4)
                    System.out.print(" ");
                else if (rowIndex == 1 || rowIndex == SQUARES_HIGH - 2)
                    System.out.println("|");
                else if (rowIndex == 2)
                    System.out.print(UNICODE_UPPER_DOOR_THRESHOLD);
                else
                    System.out.print("T");
                break;
            }
            case WALL:{
                System.out.print("|");
                break;
            }
            case NOTHING:{
                if (rowIndex == 1)
                    System.out.print("'");
                else if (rowIndex == SQUARES_HIGH - 2)
                    System.out.print(".");
                else
                    System.out.print(" ");
                break;
            }
        }
    }

    /**
     * This method prints a full line that displays the color tag for each spawn square and spaces for each other square in the grid row.
     *
     * @param squares an array of Square that contains all the squares of the grid row.
     * @param leftBorders an ArrayList of Border containing all squares left border from left to right square.
     * @param rightMostBorder a Border which is the last border on the grid row.
     * @param spawnSquares an ArrayList of SpawnSquare containing all map spawn squares.
     * @param rowIndex the current printing line index.
     */
    private void printSpawnTagsLine(Square[] squares, ArrayList<Border> leftBorders, Border rightMostBorder, ArrayList<SpawnSquare> spawnSquares, int rowIndex) {

        //search for spawn square in this row
        int spawnSquareIndex = -1;
        boolean found = false;
        while (!found && spawnSquareIndex < squares.length) {
            spawnSquareIndex++;
            if (spawnSquares.contains(squares[spawnSquareIndex])) found = true;
        }

        SpawnSquare temp = (SpawnSquare) squares[spawnSquareIndex];
        Color color = temp.getColor();

        for (int i = 0; i < squares.length; i++) {

            printBorderChar(leftBorders.get(i), rowIndex);
            System.out.print(" ");

            if (i == spawnSquareIndex) {

                //print color tag
                switch (color) {
                    case RED:{
                        System.out.print("SPAWN:RED   ");
                        break;
                    }
                    case BLUE:{
                        System.out.print("SPAWN:BLUE  ");
                        break;
                    }
                    case YELLOW:{
                        System.out.print("SPAWN:YELLOW");
                        break;
                    }
                }

                //fill to the next border
                printSpacesFromIndexToIndex(14, SQUARES_WIDTH - 2);
            } else {

                //void line to the next border
                printSpacesFromIndexToIndex(2, SQUARES_WIDTH - 2);
            }
        }
        //last border closing
        printBorderChar(rightMostBorder, rowIndex);
    }


    /**
     * This method memorizes the borders of all squares in a row, from left to right square.
     *
     * @param squares an array of Square to be scanned left to right.
     * @param leftOrUp a String flag representing which border to memorize (upper border or left border of the square).
     * @param isFirstRow a boolean that says if this printed grid row is the first grid row.
     * @return an ArrayList of Border that contains all squares border (up or left border) from left to right.
     */
    private ArrayList<Border> retrieveBordersInfo(Square[] squares, String leftOrUp, boolean isFirstRow) {

        ArrayList<Border> borders = new ArrayList<>();

        if (leftOrUp.equals("up")) {

            for (Square square : squares) {
                if (square != null)
                    borders.add(square.getUp());

                else {    //void square

                    if (isFirstRow)
                        borders.add(Border.NOTHING);
                    else
                        borders.add(Border.WALL);
                }
            }

        } else if (leftOrUp.equals("left")) {

            for (int i = 0; i < squares.length; i++) {
                if (squares[i].getUp() != null)
                    borders.add(squares[i].getLeft());
                else {

                    if (i != 0)         //not the first square in the row
                        borders.add(Border.WALL);
                    else
                        borders.add(Border.NOTHING);
                }
            }
        }
        return borders;
    }

    /**
     * This method checks what kind of square a row of the map grid finishes with and associates a closing border to the row.
     *
     * @param squares an array of Square to be scanned.
     * @return a Border associated with the closing border of this map grid row.
     */
    private Border retrieveLastBorderInfo(Square[] squares) {

        if (squares[squares.length - 1].getUp() == null)        //last square is void
            return Border.NOTHING;
        else
            return Border.WALL;
    }

    /**
     * This method collects all players user names inside a map grid row in a list.
     *
     * @param squares an array of Square to be scanned.
     * @return an ArrayList of ArrayList of String that contains all user names by each square.
     */
    private ArrayList<ArrayList<Figure>> getFiguresInsideRow(Square[] squares) {
        
        ArrayList<SmartPlayer> players = new ArrayList<>(model.getSmartPlayerMap().values());

        ArrayList<ArrayList<Figure>> playersBySquareInRow = new ArrayList<>();
        for (Square square : squares) {
            ArrayList<Figure> playersInSquare = new ArrayList<>();
            for (SmartPlayer player : players) {
                if (player.getPosX() == square.getX() && player.getPosY() == square.getY()) 
                    playersInSquare.add(player.getFigure());
            }
            playersBySquareInRow.add(playersInSquare);
        }
        return playersBySquareInRow;
    }

    /**
     * This method parse info from a map grid row to strings and collects them into a list.
     *
     * @param squares an array of Square to be scanned.
     * @param spawnSquares an ArrayList of SpawnSquare that represent all squares that are not TileSquare or void squares.
     * @param squareTypes a list of String representing the type of each square by its position in the row.
     */
    private ArrayList<ArrayList<String>> getContentOfEachSquare(Square[] squares, ArrayList<SpawnSquare> spawnSquares, ArrayList<String> squareTypes) {

        ArrayList<ArrayList<String>> squareContentInfo = new ArrayList<>();
        ArrayList<String> infoArray = new ArrayList<>();
        ArrayList<SmartTile> tiles = new ArrayList<>(model.getMapTiles());
        Map<Color,ArrayList<WeaponName>> weapons = model.getSpawnWeaponMap();
        
        for (Square square : squares) {

            if (square != null) {   //tile or spawn squares

                if (spawnSquares.contains(square)) {    //square is a spawnSquare

                    SpawnSquare spawnSquare = (SpawnSquare) square;

                    for (WeaponName weapon : weapons.get(spawnSquare.getColor())) {
                        infoArray.add(parseWeaponName(weapon,true));
                    }

                    for (int i = 0; i < MAX_WEAPONS_BY_SQUARE - weapons.get(spawnSquare.getColor()).size(); i++) {
                        infoArray.add("      ");    //no weapon
                    }

                    squareContentInfo.add(infoArray);
                    squareTypes.add("spawn");

                } else {    //square is a tile square

                    TileSquare tileSquare = (TileSquare) square;

                    for (SmartTile tile : tiles) {
                        if (tile.getPosX() == tileSquare.getX() && tile.getPosY() == tileSquare.getY()) {
                            
                            infoArray.add(tile.getPowerup() + "PU   ");
                            
                            for (Color color : tile.getAmmo()) {
                                infoArray.add(parseColorName(color));
                            }
                        }
                    }

                    squareContentInfo.add(infoArray);
                    squareTypes.add("tile");
                }
            } else {            //square is void

                for (int i = SQUARE_CONTENT_SECTION_STARTING_INDEX; i < SQUARE_FIGURE_SECTION_STARTING_INDEX; i++)
                    infoArray.add("      ");
                squareContentInfo.add(infoArray);
                squareTypes.add("void");
            }
        }
        return squareContentInfo;
    }

    /**
     * This method parse a weapon name into a shorten version of the same weapon name.
     *
     * @param name a WeaponName to be parsed to a 5 characters string.
     * @param isLoaded a boolean flag saying if the weapon is loaded.
     * @return a MAX_INFO_SIZE - 1 characters long String which represents the shorten version of a weapon name.
     */
    private String parseWeaponName(WeaponName name, boolean isLoaded) {
        switch (name) {
            case FURNACE:{
                if (isLoaded)
                    return "FURNC ";
                else
                    return "FURNC" + UNICODE_NO_AMMO;
            }
            case CYBERBLADE:{
                if (isLoaded)
                    return "CYBLD ";
                else
                    return "CYBLD" + UNICODE_NO_AMMO;
            }
            case ELECTROSCYTHE:{
                if (isLoaded)
                    return "ELECT ";
                else
                    return "ELECT" + UNICODE_NO_AMMO;
            }
            case SHOCKWAVE:{
                if (isLoaded)
                    return "SHOCK ";
                else
                    return "SHOCK" + UNICODE_NO_AMMO;
            }
            case ZX2:{
                if (isLoaded)
                    return "ZX2   ";
                else
                    return "ZX2  " + UNICODE_NO_AMMO;
            }
            case THOR:{
                if (isLoaded)
                    return "THOR  ";
                else
                    return "THOR " + UNICODE_NO_AMMO;
            }
            case SHOTGUN:{
                if (isLoaded)
                    return "SHOTG ";
                else
                    return "SHOTG" + UNICODE_NO_AMMO;
            }
            case WHISPER:{
                if (isLoaded)
                    return "WHISP ";
                else
                    return "WHISP" + UNICODE_NO_AMMO;
            }
            case HEATSEEKER:{
                if (isLoaded)
                    return "HTSKR ";
                else
                    return "HTSKR" + UNICODE_NO_AMMO;
            }
            case MACHINEGUN:{
                if (isLoaded)
                    return "MACHG ";
                else
                    return "MACHG" + UNICODE_NO_AMMO;
            }
            case TRACTORBEAM:{
                if (isLoaded)
                    return "TRTBM ";
                else
                    return "TRTBM" + UNICODE_NO_AMMO;
            }
            case VORTEXCANNON:{
                if (isLoaded)
                    return "VORTX ";
                else
                    return "VORTX" + UNICODE_NO_AMMO;
            }
            case POWERGLOVE:{
                if (isLoaded)
                    return "POWGV ";
                else
                    return "POWGV" + UNICODE_NO_AMMO;
            }
            case SLEDGEHAMMER:{
                if (isLoaded)
                    return "SLGHM ";
                else
                    return "SLGHM" + UNICODE_NO_AMMO;
            }
            case ROCKETLAUNCHER:{
                if (isLoaded)
                    return "RKTLC ";
                else
                    return "RKTLC" + UNICODE_NO_AMMO;
            }
            case HELLION:{
                if (isLoaded)
                    return "HELLN ";
                else
                    return "HELLN" + UNICODE_NO_AMMO;
            }
            case RAILGUN:{
                if (isLoaded)
                    return "RAILG ";
                else
                    return "RAILG" + UNICODE_NO_AMMO;
            }
            case LOCKRIFLE:{
                if (isLoaded)
                    return "LOCKR ";
                else
                    return "LOCKR" + UNICODE_NO_AMMO;
            }
            case PLASMAGUN:{
                if (isLoaded)
                    return "PLASM ";
                else
                    return "PLASM" + UNICODE_NO_AMMO;
            }
            case FLAMETHROWER:{
                if (isLoaded)
                    return "FLMTW ";
                else
                    return "FLMTW" + UNICODE_NO_AMMO;
            }
            case GRENADELAUNCHER:{
                if (isLoaded)
                    return "GNDLC ";
                else
                    return "GNDLC" + UNICODE_NO_AMMO;
            }
        }
        return null;
    }

    /**
     * This method parse a Color into a shorten version of the name of that color.
     *
     * @param color a Color to be parsed to a 5 characters string.
     * @return a MAX_INFO_SIZE characters long String which represents the shorten version of a color name.
     */
    private String parseColorName(Color color) {
        switch (color) {
            case BLUE: return "BLUE ";
            case YELLOW: return "YELLOW";
            case RED: return "RED   ";
        }
        return null;
    }

    /**
     * This method prints a square box line with a figure name inside it.
     *
     * @param figure a Figure which name will be formatted and printed.
     */
    private void printFigure(Figure figure) {
        switch (figure) {
            case DESTRUCTOR:{
                System.out.print(":D-STR");
                break;
            }
            case DOZER:{
                System.out.print("DOZER ");
                break;
            }
            case BANSHEE:{
                System.out.print("BANSHE");
                break;
            }
            case VIOLET:{
                System.out.print("VIOLET");
                break;
            }
            case SPROG:{
                System.out.print("SPROG ");
                break;
            }
        }

        //fill until next border
        printSpacesFromIndexToIndex(8, SQUARES_WIDTH - 1);
    }

    /**
     * This method prints a full line of player line type considering row index, borders and figures inside each square.
     *
     * @param leftBorders an ArrayList of Border containing each square left border from left square to right square.
     * @param figures an ArrayList of Figure containing a figure for each square box.
     * @param rowIndex an integer representing at which command line row idex is the method printing, relative to the square box high.
     * @param rightMostBorder a Border which is the last square's right border.
     */
    private void printFigureLine(ArrayList<Border> leftBorders, ArrayList<Figure> figures, int rowIndex, Border rightMostBorder) {

        for (int i = 0; i < leftBorders.size(); i++) {
            printBorderChar(leftBorders.get(i), rowIndex);
            System.out.print(" ");
            printFigure(figures.get(i));
            printSpacesFromIndexToIndex(8, SQUARES_WIDTH - 1);
        }

        //closing line
        printBorderChar(rightMostBorder, rowIndex);
    }

    /**
     * This method prints a full line of content info about each square of a particular row.
     *
     * @param leftBorders an ArrayList of Border containing each square left border from left square to right square.
     * @param contentBySquare an ArrayList of String containing each square info to be printed.
     * @param rowIndex an integer representing at which command line row idex is the method printing, relative to the square box high.
     * @param rightMostBorder a Border representing the right border of the last square in the row.
     */
    private void printContentLine(ArrayList<Border> leftBorders, ArrayList<String> contentBySquare, int rowIndex, Border rightMostBorder) {

        for (int i = 0; i < leftBorders.size(); i++) {
            printBorderChar(leftBorders.get(i), rowIndex);
            System.out.print(" ");
            System.out.print(contentBySquare.get(i));
            printSpacesFromIndexToIndex(8, SQUARES_WIDTH - 1);  //fill with spaces to the next border
        }

        //closing line
        printBorderChar(rightMostBorder, rowIndex);

    }

    /**
     * This method prints each player board.
     *
     * @param nickname a String which is the users nickname.
     * @param player as SmartPlayer whom board will be printed.
     * @param isOwnBoard a boolean flag that says if the board printed is the current client player's board.
     */
    private void printBoard(String nickname, SmartPlayer player, boolean isOwnBoard) {

        printBoardHeader(nickname, isOwnBoard, player.getPoints());
        printAdrenalineLevel(player.getDeaths());
        System.out.println("| Marks:");
        printMarkTrack(player.getMarks());
        System.out.print("| Damage: ");
        printDamageTrack(player.getDamage());
        System.out.println("| Ammunition:");
        printPlayerAmmo(player.getAmmo());
        System.out.println("| Bounties:");
        printBountyTrack(player.getDeaths());
        System.out.println("| Weapons:");
        printPlayerWeapons(player.getWeapons());
        if (isOwnBoard)
            printPlayerPowerups(player.getPowerups());
        printSceneSpacing();
    }

    /**
     * This method prints a player's board header with nick and points.
     *
     * @param nickname a String which is the username of the printing board. It will be printed inside the header.
     * @param isOwnBoard a boolean flag that says if this board is the current player own board.
     * @param points an integer containing the player's points.
     */
    private void printBoardHeader(String nickname, boolean isOwnBoard, int points) {
        if (isOwnBoard) System.out.print(ANSI_GREEN);
        System.out.print("+ ");
        System.out.print(nickname.substring(0, NICK_PRINT_SIZE));
        System.out.println("   Points: " + points);
        System.out.print(ANSI_RESET);
    }

    /**
     * This method prints the player adrenaline level considering how many times this player died.
     *
     * @param deaths an integer containing how many times this player died.
     */
    private void printAdrenalineLevel(int deaths) {
        if (deaths < 3) System.out.println("| Adrenaline level: 0");
        else if (deaths >=3 && deaths <= 5) System.out.println("| Adrenaline level: 1");
        else System.out.println("| Adrenaline level: " + ANSI_RED + "MAX" + ANSI_RESET);
    }

    /**
     * This method prints the player marks.
     *
     * @param marks a Map of Figure and Integer to be printed.
     */
    private void printMarkTrack(Map<Figure, Integer> marks) {

        for (Figure f : marks.keySet()) {
            System.out.print("| ");
            System.out.println(f.name().substring(0, MAX_INFO_SIZE) + " : " + marks.get(f));
        }
        System.out.println();
    }

    /**
     * This method prints the player damage.
     *
     * @param damage an ArrayList of Figure containing all players that damaged this player in the correct order.
     */
    private void printDamageTrack(ArrayList<Figure> damage) {
        System.out.print(" | ");

        for (Figure f : damage) {
            System.out.print(f.name().substring(0,MAX_INFO_SIZE) + " | ");
        }

        for (int i = 0; i < 13 - damage.size(); i++) {
            System.out.print("      " + " | ");
        }
        System.out.println();
    }

    /**
     * This method prints all players ammo.
     *
     * @param ammo a Map of Color and Integer containing each ammo color amount.
     */
    private void printPlayerAmmo(Map<Color, Integer> ammo) {
        for (Color color : ammo.keySet()) {
            System.out.print("| ");
            System.out.println(color.name() + " : " + ammo.get(color));
        }
        System.out.println();
    }

    /**
     * This method prints all bounties remained on this player.
     *
     * @param deaths an integer saying how many times this player died.
     */
    private void printBountyTrack(int deaths) {

        ArrayList<Integer> bountyValues = new ArrayList<>(Arrays.asList(settings.getBounties()));

        //print skulls
        for (int i = 0; i < deaths; i++) {
            System.out.print("[" + ANSI_RED + UNICODE_SKULL + ANSI_RESET + "] ");
            bountyValues.remove(i);
        }

        //print remaining values
        for (Integer value : bountyValues)
            System.out.print("[" + value + "] ");

        System.out.println();
    }

    /**
     * This method prints all weapons that a player owns.
     *
     * @param weapons an ArrayList of SmartWeapon that contains all player weapons.
     */
    private void printPlayerWeapons(ArrayList<SmartWeapon> weapons) {
        for (SmartWeapon weapon : weapons) {
            if (weapon.getLoaded())
                System.out.println(weapon.getWeaponName().name());
            else
                System.out.println(ANSI_RED + weapon.getWeaponName().name() + UNICODE_NO_AMMO + ANSI_RESET);
        }
    }

    /**
     * This method prints all powerups that a player owns.
     *
     * @param powerups an ArrayList of SmartPowerup that contains all player powerups.
     */
    private void printPlayerPowerups(ArrayList<SmartPowerup> powerups) {

        System.out.println("| Power-ups: ");
        for (SmartPowerup pu : powerups)
            System.out.println(pu.getColor() + " " + pu.getPowerupName().name());
    }

    private void printKillShotTrack() {

        Map<String, SmartPlayer> users = model.getSmartPlayerMap();
        Integer maxKills = settings.getMaxKills();
        Integer currentKills = 0;

        if (!model.getDomination()) {

            //count number of kills
            for (SmartPlayer player : users.values()) {
                currentKills += player.getDeaths();
            }
            //TODO(print also killers on KillShotTrack)
            System.out.print("KILL SHOT TRACK: ");
            for (int i = 0; i < maxKills - currentKills; i++) {
                System.out.print(" | " + ANSI_RED + UNICODE_SKULL + ANSI_RESET);
            }
        } else {
            //TODO(print spawn damage track)
        }
    }

    /////////////////////////////////////////// ViewInterface implementations //////////////////////////////////////////////

    @Override
    public void logout() {
        System.out.println(ANSI_RED + "You were forcefully disconnected" + ANSI_RESET);
        launch();
    }

    @Override
    public void sendMessage(String s) {
        System.out.println(s);
    }

    @Override
    public void notifyEvent(String s) {
        sendMessage(s);
    }

    @Override
    public int choosePlayer(Figure[] f) {

        int choice = -1;

        System.out.println();
        while (choice < 1 || choice > f.length) {

            for (int i = 0; i < f.length; i++) {
                System.out.println((i + 1) + " - " + f[i].name());
            }
            System.out.print("\nChoose a figure by its number: ");
            choice = scannerIn.nextInt();
            if (choice < 1 || choice > f.length)
                System.out.println(ANSI_RED + "Invalid input." + ANSI_RESET);
        }
        return choice - 1;
    }

    @Override
    public int chooseWeapon(WeaponName[] w) {

        int choice = -1;

        System.out.println();
        while (choice < 1 || choice > w.length) {

            for (int i = 0; i < w.length; i++) {
                System.out.println((i + 1) + " - " + w[i].name());
            }
            System.out.print("\nChoose a weapon by its number: ");
            choice = scannerIn.nextInt();
            if (choice < 1 || choice > w.length)
                System.out.println(ANSI_RED + "Invalid input." + ANSI_RESET);
        }
        return choice - 1;
    }

    @Override
    public int chooseString(String[] s) {

        int choice = -1;

        System.out.println();
        while (choice < 1 || choice > s.length) {

            for (int i = 0; i < s.length; i++) {
                System.out.println(i + 1 + " - " + s[i]);
            }
            System.out.print("\nChoose effect usage by its number: ");
            choice = scannerIn.nextInt();
            if (choice < 1 || choice > s.length)
                System.out.println(ANSI_RED + "Invalid input." + ANSI_RESET);
        }
        return choice - 1;
    }

    @Override
    public int chooseDirection(Character[] c) {

        int choice = -1;

        System.out.println();
        while (choice < 1 || choice > c.length) {

            for (int i = 0; i < c.length; i++) {

                switch (c[i]) {
                    case 'N':{
                        System.out.println(i + 1  + "North");
                        break;
                    }
                    case 'S':{
                        System.out.println(i + 1 + "South");
                        break;
                    }
                    case 'E':{
                        System.out.println(i + 1 + "East");
                        break;
                    }
                    case 'W':{
                        System.out.println(i + 1 + "West");
                        break;
                    }
                }
            }
            System.out.print("\nChoose direction by its number: ");
            choice = scannerIn.nextInt();
            if (choice < 1 || choice > c.length)
                System.out.println(ANSI_RED + "Invalid input." + ANSI_RESET);
        }
        return choice - 1;
    }

    @Override
    public int chooseColor(Color[] c) {
        int choice = -1;

        System.out.println();
        while (choice < 1 || choice > c.length) {

            for (int i = 0; i < c.length; i++) {
                System.out.println(i + 1 + " - " + c[i].name());
            }
            System.out.print("\nChoose color by its number: ");
            choice = scannerIn.nextInt();
            if (choice < 1 || choice > c.length)
                System.out.println(ANSI_RED + "Invalid input." + ANSI_RESET);
        }
        return choice - 1;
    }

    @Override
    public int choosePowerup(Powerup[] p) {
        int choice = -1;

        while (choice < 1 || choice > p.length) {

            for (int i = 0; i < p.length; i++) {
                System.out.println(i + 1 + " - " + p[i].getColor().name() + " " + p[i].getName().name());
            }
            System.out.print("\nChoose power up by its number: ");
            choice = scannerIn.nextInt();
            if (choice < 1 || choice > p.length)
                System.out.println(ANSI_RED + "Invalid input." + ANSI_RESET);
        }
        return choice - 1;
    }

    @Override
    public int chooseMap(int[] m) {

        int choice = -1;

        //boxing from primitive int[] to Integer[]
        Integer[] boxedArray = new Integer[m.length];
        for (int i = 0; i < m.length; i++) {
            boxedArray[i] = m[i];
        }

        List<Integer> maps = Arrays.asList(boxedArray);

        System.out.println();
        while (!maps.contains(choice - 1)) {

            for (int i = 0; i < m.length; i++) {
                System.out.println(m[i] + 1);
            }
            System.out.print("\nChoose map by its number: ");

            if (scannerIn.hasNextInt())
                choice = scannerIn.nextInt();

            if (!maps.contains(choice - 1))
                System.out.println(ANSI_RED + "Invalid input." + ANSI_RESET);
        }
        return choice - 1;
    }

    @Override
    public int chooseMode(Character[] c) {

        String fullChoice;
        Character choice = '0';

        while (choice != 'n' && choice != 'd' && choice != 'N' && choice != 'D') {

            System.out.println("\nNormal Mode | Domination Mode");
            System.out.print("\nChoose game mode (n/d): ");

            fullChoice = scannerIn.nextLine();  //allow user to type in also the full case name
            choice = fullChoice.toCharArray()[0];

            if (choice != 'n' && choice != 'd' && choice != 'N' && choice != 'D') {
                System.out.println(ANSI_RED + "Invalid input." + ANSI_RESET);
                scannerIn.nextLine();
            }
        }
        for (int i = 0; i < c.length; i++) {
            if ((c[i]) == choice || (c[i] == choice + 32)) return i;    //check upper and lower case
        }
        return -1;
    }

    @Override
    public int chooseSquare(Square[] s) {
        return 0;
    }

    @Override
    public Boolean booleanQuestion(String s) {

        System.out.println(s);
        System.out.print("Yes or No: ");
        String choice = scannerIn.nextLine();

        if (choice.equals("Yes") || choice.equals("YES") || choice.equals("Y") || choice.equals("y") || choice.equals("Yeah"))
            return true;
        else if (choice.equals("No") || choice.equals("NO") || choice.equals("N") || choice.equals("n") || choice.equals("Nope"))
            return false;
        else
            return booleanQuestion(s);      //wrong input then ask again
    }

    @Override
    public int[] chooseMultiplePowerup(Powerup[] p) {

        char wantToContinue;
        Powerup[] temp = new Powerup[p.length - 1];
        int[] out = new int[p.length];
        int i = 0;

        do {
            out[i] = choosePowerup(p);
            System.out.print("Do you want to pick another powerup? Yes or No: ");
            wantToContinue = (char) scannerIn.nextInt();
            if (wantToContinue == 'y') {
                for (int j = 0; j < p.length - 1; j++) {
                    temp[j] = p[j + 1];
                    if (j == p.length - 2)
                        temp[j + 1] = p[p.length - 1];
                }
                p = temp;
            }
            i++;
        } while (wantToContinue == 'y' && p.length != 0);

        return out;
    }

    @Override
    public int[] chooseMultipleWeapon(WeaponName[] w) {

        char wantToContinue;
        WeaponName[] temp = new WeaponName[w.length - 1];
        int[] out = new int[w.length];
        int i = 0;

        do {
            out[i] = chooseWeapon(w);
            System.out.print("Do you want to pick another weapon? Yes or No: ");
            wantToContinue = (char) scannerIn.nextInt();
            if (wantToContinue == 'y') {
                for (int j = 0; j < w.length - 1; j++) {
                    temp[j] = w[j + 1];
                    if (j == w.length - 2)
                        temp[j + 1] = w[w.length - 1];
                }
                w = temp;
            }
            i++;
        } while (wantToContinue == 'y' && w.length != 0);

        return out;
    }

    @Override
    public void notifyModelUpdate() {

        try {
            model = client.getModelUpdate();
            printModel();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
