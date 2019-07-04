package it.polimi.ingsw.view.gui;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.CustomStream;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.model.smartmodel.*;
import it.polimi.ingsw.view.Client;
import it.polimi.ingsw.view.ViewInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class contains the graphic implementation of the view to use the application
 */
public class GuiMain extends Application implements ViewInterface {

    private double height;
    private double width;
    private double spacing;
    private int textSize;
    private Stage primaryStage;
    private Scene primaryScene;
    private StackPane rootPane;
    private StackPane requestPane;
    private Pane gamePane;
    private Client client;
    private String nickname;
    private Text textEvent;
    private AtomicInteger currentScenario;
    private AtomicBoolean pendingRequest;
    private SmartModel smartModel;
    private Properties properties;
    private Gson gson;
    private static String serverIp;
    private static String clientIp;

    /**
     * @return a StackPane containing a button that allows the user to logout
     */
    private StackPane getTopBar() {
        Button buttonLogout = new Button("Logout");
        buttonLogout.setOnAction(behavior -> {
            setLogoutScenario();
            client.logout();
        });
        AnchorPane.setTopAnchor(buttonLogout,10.0);
        AnchorPane.setRightAnchor(buttonLogout,10.0);
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(buttonLogout);

        return new StackPane(anchorPane);
    }

    /**
     * @return a top bat with also a button that allows the user to switch to the map scenario
     */
    private StackPane getTopBarLinkedMap() {
        Button buttonLogout = new Button("Logout");
        buttonLogout.setOnAction(behavior -> {
            setLogoutScenario();
            client.logout();
        });
        AnchorPane.setTopAnchor(buttonLogout,10.0);
        AnchorPane.setRightAnchor(buttonLogout,10.0);

        Button buttonGameMap = new Button("Show table");
        buttonGameMap.setOnAction(behavior -> {
            setGameMapScenario();
        });
        AnchorPane.setTopAnchor(buttonGameMap,10.0);
        AnchorPane.setLeftAnchor(buttonGameMap,10.0);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().addAll(buttonLogout,buttonGameMap);

        return new StackPane(anchorPane);
    }

    /**
     * @return a top bat with also a button that allows the user to switch to the request scenario
     */
    private StackPane getTopBarLinkedRequest() {
        Button buttonLogout = new Button("Logout");
        buttonLogout.setOnAction(behavior -> {
            setLogoutScenario();
            client.logout();
        });
        AnchorPane.setTopAnchor(buttonLogout,10.0);
        AnchorPane.setRightAnchor(buttonLogout,10.0);

        Button buttonRequest = new Button("Show request");
        buttonRequest.setOnAction(behavior -> {
            setRequestScenario();
        });
        AnchorPane.setTopAnchor(buttonRequest,10.0);
        AnchorPane.setLeftAnchor(buttonRequest,10.0);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().addAll(buttonLogout,buttonRequest);

        return new StackPane(anchorPane);
    }

    /**
     * @return a StackPane containing the notifications from the server
     */
    private StackPane getBottomBar() {
        return new StackPane(textEvent);
    }

    /**
     * This method sets the game pane with all the useful information to the user
     * @throws Exception if something goes wrong with the smartmodel parsing
     */
    private void setupGamePane() throws Exception {
        cleanGamePane();

        double mapWidth = width * 0.5 - spacing;
        double mapHeight = (height - 36 - 16) * 0.73 - spacing;
        double mapOffsetX = spacing;
        double mapOffsetY = spacing;

        double myBoardWidth = width * 0.5 - spacing;
        double myBoardHeight = (height - 36 - 16) * 0.27 - spacing;
        double myBoardOffsetX = spacing;
        double myBoardOffsetY = (height - 36 - 16) * 0.73 + spacing;

        double playerBoardWidth = (width * 0.5 * 0.65) - spacing;
        double playerBoardHeight = (height - 36 - 16) * 0.73 * 0.25 - spacing;
        double playerBoardOffsetX = width * 0.5 + spacing;
        double playerBoardOffsetY = spacing;

        double myWeaponWidth = ((width * 0.5 - spacing) / 6) - spacing;
        double myWeaponHeight = myBoardHeight;
        double myWeaponOffsetX = playerBoardOffsetX;
        double myWeaponOffsetY = myBoardOffsetY;

        double myPowerupWidth = myWeaponWidth;
        double myPowerupHeight = myWeaponHeight;
        double myPowerupOffsetX = myWeaponOffsetX;
        double myPowerupOffsetY = myWeaponOffsetY;

        double playerWeaponWidth = (width * 0.5 * 0.35 - spacing) / 3 - spacing;
        double playerWeaponHeight = playerBoardHeight;
        double playerWeaponOffsetX = (width * 0.5 * 1.65) + spacing;
        double playerWeaponOffsetY = spacing;

        double mapIconHeight = (1318.0 / 1931) * mapHeight;
        double mapIconWidth = (1733.0 / 2551) * mapWidth;
        double mapIconOffsetX = (414.0 / 2551) * mapWidth + spacing;
        double mapIconOffsetY = (402.0 / 1931) * mapHeight + spacing;

        double squareHeight = mapIconWidth / 4;
        double squareWidth = mapIconHeight / 3;

        double tileHeight = (mapIconWidth / 4) / 3;
        double tileWidth = tileHeight;

        double figureHeight = tileHeight;
        double figureWidth = figureHeight;

        double weaponWidth = (820.0 / (3 * 2551)) * mapWidth;
        double weaponHeight = weaponWidth * 1.7;

        Pane collectorPane = new Pane();

        // Setup the map
        ImagePane imagePaneMap = new ImagePane(properties.getProperty("mapsRoot").concat(properties.getProperty("map" + (smartModel.getMapIndex() + 1))), "-fx-background-size: contain; -fx-background-repeat: no-repeat;");
        imagePaneMap.setPrefHeight(mapHeight);
        imagePaneMap.setPrefWidth(mapWidth);
        imagePaneMap.setLayoutX(mapOffsetX);
        imagePaneMap.setLayoutY(mapOffsetY);
        collectorPane.getChildren().add(imagePaneMap);

        // Setup the tiles
        for (SmartTile smartTile : smartModel.getMapTiles()) {
            ImagePane imagePaneTile = new ImagePane(properties.getProperty("tilesRoot").concat(properties.getProperty(getTileFileName(smartTile))), "-fx-background-size: contain; -fx-background-repeat: no-repeat;");
            imagePaneTile.setPrefHeight(tileHeight);
            imagePaneTile.setPrefWidth(tileWidth);
            imagePaneTile.setLayoutX(mapIconOffsetX + smartTile.getPosY() * (squareWidth) + squareWidth * 0.15);
            imagePaneTile.setLayoutY(mapIconOffsetY + smartTile.getPosX() * (squareHeight) + squareHeight * 0.15);
            collectorPane.getChildren().add(imagePaneTile);
        }

        // Setup blue spawn weapons
        int weaponSpawnCounter = 0;
        for (WeaponName weaponName : smartModel.getSpawnWeaponMap().get(it.polimi.ingsw.model.enumeratedclasses.Color.BLUE)) {
            double weaponOffsetX = (1344.0 / 2551) * mapWidth + spacing;
            double weaponOffsetY = (375.0 / 1931) * mapHeight + spacing - weaponHeight;

            ImagePane imagePaneWeapon = new ImagePane(properties.getProperty("weaponsRoot").concat(properties.getProperty("weapon" + weaponName.toString())),"-fx-background-size: contain; -fx-background-repeat: no-repeat;");
            imagePaneWeapon.setPrefHeight(weaponHeight);
            imagePaneWeapon.setPrefWidth(weaponWidth);
            imagePaneWeapon.setLayoutX(weaponOffsetX + weaponWidth * weaponSpawnCounter);
            imagePaneWeapon.setLayoutY(weaponOffsetY);
            collectorPane.getChildren().add(imagePaneWeapon);
            weaponSpawnCounter++;
        }

        // Setup red spawn weapons
        weaponSpawnCounter = 0;
        for (WeaponName weaponName : smartModel.getSpawnWeaponMap().get(it.polimi.ingsw.model.enumeratedclasses.Color.RED)) {
            double weaponOffsetX = (320.0 / 2551) * mapWidth + spacing - weaponWidth;
            double weaponOffsetY = (698.0 / 1931) * mapHeight + spacing;

            ImagePane imagePaneWeapon = new ImagePane(properties.getProperty("weaponsRoot").concat(properties.getProperty("weapon" + weaponName.toString())),"-fx-background-size: contain; -fx-background-repeat: no-repeat;");
            imagePaneWeapon.setPrefHeight(weaponHeight);
            imagePaneWeapon.setPrefWidth(weaponWidth);
            imagePaneWeapon.setLayoutX(weaponOffsetX);
            imagePaneWeapon.setLayoutY(weaponOffsetY + weaponSpawnCounter * weaponWidth);
            collectorPane.getChildren().add(imagePaneWeapon);
            weaponSpawnCounter++;
        }

        // Setup yellow spawn weapons
        weaponSpawnCounter = 0;
        for (WeaponName weaponName : smartModel.getSpawnWeaponMap().get(it.polimi.ingsw.model.enumeratedclasses.Color.YELLOW)) {
            double weaponOffsetX = (2240.0 / 2551) * mapWidth + spacing;
            double weaponOffsetY = (950.0 / 1931) * mapHeight + spacing;

            ImagePane imagePaneWeapon = new ImagePane(properties.getProperty("weaponsRoot").concat(properties.getProperty("weapon" + weaponName.toString())),"-fx-background-size: contain; -fx-background-repeat: no-repeat;");
            imagePaneWeapon.setPrefHeight(weaponHeight);
            imagePaneWeapon.setPrefWidth(weaponWidth);
            imagePaneWeapon.setLayoutX(weaponOffsetX);
            imagePaneWeapon.setLayoutY(weaponOffsetY + weaponSpawnCounter * weaponWidth);
            collectorPane.getChildren().add(imagePaneWeapon);
            weaponSpawnCounter++;
        }

        if (!smartModel.getDomination()) {

            // Setup killshot track in normal mode
            int figureCounter = 0;
            for (Figure figure : smartModel.getKillshotTrack()) {
                ImagePane imagePaneFigure = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blob" + figure.toString())),"-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                imagePaneFigure.setPrefHeight((128.0 / 1931) * mapHeight);
                imagePaneFigure.setPrefWidth((854.0 / 2551) * mapWidth / 8);
                imagePaneFigure.setLayoutX(mapOffsetX + (182.0 / 2551) * mapWidth + figureCounter * (854.0 / 2551) * mapWidth / 8 / 2);
                imagePaneFigure.setLayoutY(mapOffsetY + (103.0 / 1931) * mapHeight);
                collectorPane.getChildren().add(imagePaneFigure);
                figureCounter++;
            }

            // Setup frenxy counter in normal mode
            for (int i = 0; i < smartModel.getKillCount() ; i++) {
                ImagePane imagePaneSkull = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blobSKULL")),"-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                imagePaneSkull.setPrefHeight((128.0 / 1931) * mapHeight);
                imagePaneSkull.setPrefWidth((854.0 / 2551) * mapWidth / 8);
                imagePaneSkull.setLayoutX(mapOffsetX + (182.0 / 2551) * mapWidth + (854.0 / 2551) * mapWidth * 7 / 8 - i * (854.0 / 2551) * mapWidth / 8);
                imagePaneSkull.setLayoutY(mapOffsetY + (103.0 / 1931) * mapHeight);
                collectorPane.getChildren().add(imagePaneSkull);
            }

        } else {

            double dominationBoardHeight = (364.0 / 1931) * mapHeight;
            double dominationBoardWidth = (1124.0 / 2551) * mapWidth;
            double dominationBoardOffsetX = mapOffsetX + (163.0 / 2551) * mapWidth;
            double dominationBoardOffsetY = mapOffsetY + (7.0 / 1931) * mapHeight;
            double dominationBlobHeight = (78.0 / 364) * dominationBoardHeight;
            double dominationBlobWidth = (452.0 / 1124 / 7) * dominationBoardWidth;
            double dominationKilltrackOffsetX = mapOffsetX + (1010.0 / 2551) * mapWidth;
            double dominationKilltrackOffsetY = mapOffsetY + (49.0 / 1931) * mapHeight;

            // Setup domination board
            ImagePane imagePaneDominationBoard = new ImagePane(properties.getProperty("boardDominationRoot").concat(properties.getProperty("boardDomination")),"-fx-background-size: contain; -fx-background-repeat: no-repeat;");
            imagePaneDominationBoard.setPrefHeight(dominationBoardHeight);
            imagePaneDominationBoard.setPrefWidth(dominationBoardWidth);
            imagePaneDominationBoard.setLayoutX(dominationBoardOffsetX);
            imagePaneDominationBoard.setLayoutY(dominationBoardOffsetY);
            collectorPane.getChildren().add(imagePaneDominationBoard);

            // Setup spawn damage tracks in domination mode
            int colorCounter = 0;
            for (it.polimi.ingsw.model.enumeratedclasses.Color color : smartModel.getSpawnDamageTrack().keySet()) {
                double i = 0;
                double j = 0;
                for (Figure figure : smartModel.getSpawnDamageTrack().get(color)) {
                    ImagePane imagePaneDamagePoint = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blob" + figure.toString())),"-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                    imagePaneDamagePoint.setPrefHeight(dominationBlobHeight);
                    imagePaneDamagePoint.setPrefWidth(dominationBlobWidth);
                    imagePaneDamagePoint.setLayoutX(mapOffsetX + (179.0 / 2551) * mapWidth + (i + j) * dominationBlobWidth);
                    imagePaneDamagePoint.setLayoutY(mapOffsetY + (29.0 / 1931) * mapHeight + colorCounter * (dominationBlobHeight + (40.0 / 364) * dominationBoardHeight));
                    collectorPane.getChildren().add(imagePaneDamagePoint);
                    if (i < 8)
                        i += 1;
                    else
                        j += 0.3;
                }
                colorCounter++;
            }

            // Setup the killshot track in domination mode
            for (int i = 0 ; (i < smartModel.getKillshotTrack().size() && i < 8) ; i++) {
                ImagePane imagePaneBlob = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blob" + smartModel.getKillshotTrack().get(i).toString())), "-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                imagePaneBlob.setPrefHeight(dominationBlobHeight);
                imagePaneBlob.setPrefWidth(dominationBlobWidth);
                imagePaneBlob.setLayoutX(dominationKilltrackOffsetX + i / 2.0 * dominationBlobWidth);
                imagePaneBlob.setLayoutY(dominationKilltrackOffsetY);
                collectorPane.getChildren().add(imagePaneBlob);
            } for (int i = 8 ; i < smartModel.getKillshotTrack().size() ; i++) {
                ImagePane imagePaneBlob = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blob" + smartModel.getKillshotTrack().get(i).toString())), "-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                imagePaneBlob.setPrefHeight(dominationBlobHeight);
                imagePaneBlob.setPrefWidth(dominationBlobWidth);
                imagePaneBlob.setLayoutX(dominationKilltrackOffsetX + (i - 8) / 2.0 * dominationBlobWidth);
                imagePaneBlob.setLayoutY(dominationKilltrackOffsetY + dominationBlobHeight);
                collectorPane.getChildren().add(imagePaneBlob);
            }

            // Setup the frenzy counter track in domination mode
            for (int i = 0 ; (i < smartModel.getKillCount() && i < 4) ; i++) {
                ImagePane imagePaneSkull = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blobSKULL")),"-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                imagePaneSkull.setPrefHeight(dominationBlobHeight);
                imagePaneSkull.setPrefWidth(dominationBlobWidth);
                imagePaneSkull.setLayoutX(dominationKilltrackOffsetX + 3 * dominationBlobWidth - i * dominationBlobWidth);
                imagePaneSkull.setLayoutY(dominationKilltrackOffsetY + dominationBlobHeight);
                collectorPane.getChildren().add(imagePaneSkull);
            } for (int i = 4 ; i < smartModel.getKillCount() ; i++) {
                ImagePane imagePaneSkull = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blobSKULL")),"-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                imagePaneSkull.setPrefHeight(dominationBlobHeight);
                imagePaneSkull.setPrefWidth(dominationBlobWidth);
                imagePaneSkull.setLayoutX(dominationKilltrackOffsetX + 3 * dominationBlobWidth - (i - 4) * dominationBlobWidth);
                imagePaneSkull.setLayoutY(dominationKilltrackOffsetY);
                collectorPane.getChildren().add(imagePaneSkull);
            }

        }

        // Setup the players informations
        int playerCounter = 0;
        int playerTotalCounter = 0;
        for (String nickname : smartModel.getSmartPlayerMap().keySet()) {

            SmartPlayer smartPlayer = smartModel.getSmartPlayerMap().get(nickname);

            if (nickname.equals(this.nickname)) {

                // Setup principal player board
                ImagePane imagePaneMyBoard = new ImagePane(properties.getProperty("boardsRoot").concat(properties.getProperty("board" + smartPlayer.getFigure().toString())), "-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                imagePaneMyBoard.setPrefHeight(myBoardHeight);
                imagePaneMyBoard.setPrefWidth(myBoardWidth);
                imagePaneMyBoard.setLayoutX(myBoardOffsetX);
                imagePaneMyBoard.setLayoutY(myBoardOffsetY);
                collectorPane.getChildren().add(imagePaneMyBoard);

                // Setup principal player weapons
                int weaponCounter = 0;
                for (SmartWeapon smartWeapon : smartPlayer.getWeapons()) {
                    ImagePane imagePaneWeapon;
                    if (smartWeapon.getLoaded())
                        imagePaneWeapon = new ImagePane(properties.getProperty("weaponsRoot").concat(properties.getProperty("weapon" + smartWeapon.getWeaponName().toString())), "-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                    else
                        imagePaneWeapon = new ImagePane(properties.getProperty("weaponsRoot").concat(properties.getProperty("weaponBack")), "-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                    imagePaneWeapon.setPrefHeight(myWeaponHeight);
                    imagePaneWeapon.setPrefWidth(myWeaponWidth);
                    imagePaneWeapon.setLayoutX(myWeaponOffsetX + (spacing + myWeaponWidth) * weaponCounter);
                    imagePaneWeapon.setLayoutY(myWeaponOffsetY);
                    collectorPane.getChildren().add(imagePaneWeapon);

                    weaponCounter++;
                }

                // Setup principal player powerups
                int powerupCounter = 0;
                for (SmartPowerup smartPowerup : smartPlayer.getPowerups()) {
                    ImagePane imagePanePowerup = new ImagePane(properties.getProperty("powerupsRoot").concat(properties.getProperty("powerup" + smartPowerup.getPowerupName().toString() + "_" + smartPowerup.getColor().toString())), "-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                    imagePanePowerup.setPrefHeight(myPowerupHeight);
                    imagePanePowerup.setPrefWidth(myPowerupWidth);
                    imagePanePowerup.setLayoutX(myPowerupOffsetX + (spacing + myWeaponWidth) * weaponCounter + (spacing + myPowerupWidth) * powerupCounter);
                    imagePanePowerup.setLayoutY(myPowerupOffsetY);
                    collectorPane.getChildren().add(imagePanePowerup);

                    powerupCounter++;
                }

                // Setup principal player damage
                ArrayList<Figure> damage = smartPlayer.getDamage();
                for (int i = 0 ; i < damage.size() ; i++) {
                    ImagePane imagePaneDamagePoint = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blob" + damage.get(i).toString())),"-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                    imagePaneDamagePoint.setPrefHeight((76.0 / 277) * myBoardHeight);
                    imagePaneDamagePoint.setPrefWidth((64.0 / 1124) * myBoardWidth);
                    imagePaneDamagePoint.setLayoutX(myBoardOffsetX + (98.0 / 1124) * myBoardWidth + i * (64.0 / 1124) * myBoardWidth);
                    imagePaneDamagePoint.setLayoutY(myBoardOffsetY + (92.0 / 277) * myBoardHeight);
                    collectorPane.getChildren().add(imagePaneDamagePoint);
                }

                // Setup principal player marks
                ArrayList<Figure> marks = new ArrayList<>();
                for (Figure figure : smartPlayer.getMarks().keySet()) {
                    for (int i = 0; i < smartPlayer.getMarks().get(figure) ; i++) {
                        marks.add(figure);
                    }
                }
                for (int i = 0; i < marks.size() ; i++) {
                    ImagePane imagePaneDamagePoint = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blob" + damage.get(i).toString())),"-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                    imagePaneDamagePoint.setPrefHeight((76.0 / 277) * myBoardHeight);
                    imagePaneDamagePoint.setPrefWidth((64.0 / 1124) * myBoardWidth);
                    imagePaneDamagePoint.setLayoutX(myBoardOffsetX + (543.0 / 1124) * myBoardWidth + i * (64.0 / 1124) * myBoardWidth / 2);
                    imagePaneDamagePoint.setLayoutY(myBoardOffsetY + (57.0 / 277) * myBoardHeight - (76.0 / 277) * myBoardHeight);
                    collectorPane.getChildren().add(imagePaneDamagePoint);
                }

                // Setup principal player ammo
                int colorCounter = 0;
                for (it.polimi.ingsw.model.enumeratedclasses.Color color : smartPlayer.getAmmo().keySet()) {
                    for (int i = 0 ; i < smartPlayer.getAmmo().get(color) ; i++) {
                        ImagePane imagePane = new ImagePane(properties.getProperty("ammoRoot").concat(properties.getProperty("ammo" + color.toString())),"-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                        imagePane.setPrefHeight((54.0 / 277) * myBoardHeight);
                        imagePane.setPrefWidth((54.0 / 1124) * myBoardWidth);
                        imagePane.setLayoutX(myBoardOffsetX + (916.0 / 1124) * myBoardWidth + i * (54.0 / 1124) * myBoardWidth);
                        imagePane.setLayoutY(myBoardOffsetY + (48.0 / 277) * myBoardHeight + colorCounter * (54.0 / 277) * myBoardHeight);
                        collectorPane.getChildren().add(imagePane);
                    }
                    colorCounter++;
                }

                // Setup principal player pointtrack
                for (int i = 0 ; i < smartPlayer.getDeaths() ; i++) {
                    ImagePane imagePaneSkull = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blobSKULL")),"-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                    imagePaneSkull.setPrefHeight((76.0 / 277) * myBoardHeight);
                    imagePaneSkull.setPrefWidth((360.0 / 6 / 1124) * myBoardWidth);
                    imagePaneSkull.setLayoutX(myBoardOffsetX + (232.0 / 1124) * myBoardWidth + i * (360.0 / 6 / 1124) * myBoardWidth);
                    imagePaneSkull.setLayoutY(myBoardOffsetY + (176.0 / 277) * myBoardHeight);
                    collectorPane.getChildren().add(imagePaneSkull);
                }

            } else {

                // Setup other players boards
                ImagePane imagePanePlayerBoard = new ImagePane(properties.getProperty("boardsRoot").concat(properties.getProperty("board" + smartPlayer.getFigure().toString())), "-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                imagePanePlayerBoard.setPrefHeight(playerBoardHeight);
                imagePanePlayerBoard.setPrefWidth(playerBoardWidth);
                imagePanePlayerBoard.setLayoutX(playerBoardOffsetX);
                imagePanePlayerBoard.setLayoutY(playerBoardOffsetY + (playerBoardOffsetY + playerBoardHeight) * playerCounter);
                collectorPane.getChildren().add(imagePanePlayerBoard);

                // Setup other players weapons
                int weaponCounter = 0;
                for (SmartWeapon smartWeapon : smartPlayer.getWeapons()) {
                    ImagePane imagePaneWeapon;
                    if (smartWeapon.getLoaded())
                        imagePaneWeapon = new ImagePane(properties.getProperty("weaponsRoot").concat(properties.getProperty("weapon" + smartWeapon.getWeaponName().toString())), "-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                    else
                        imagePaneWeapon = new ImagePane(properties.getProperty("weaponsRoot").concat(properties.getProperty("weaponBack")), "-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                    imagePaneWeapon.setPrefHeight(playerWeaponHeight);
                    imagePaneWeapon.setPrefWidth(playerWeaponWidth);
                    imagePaneWeapon.setLayoutX(playerWeaponOffsetX + (playerWeaponWidth + spacing) * weaponCounter);
                    imagePaneWeapon.setLayoutY(playerWeaponOffsetY + (playerWeaponHeight + spacing) * playerCounter);
                    collectorPane.getChildren().add(imagePaneWeapon);
                    weaponCounter++;
                }

                // Setup other players damage
                ArrayList<Figure> damage = smartPlayer.getDamage();
                for (int i = 0 ; i < damage.size() ; i++) {
                    ImagePane imagePaneDamagePoint = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blob" + damage.get(i).toString())),"-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                    imagePaneDamagePoint.setPrefHeight((76.0 / 277) * playerBoardHeight);
                    imagePaneDamagePoint.setPrefWidth((64.0 / 1124) * playerBoardWidth);
                    imagePaneDamagePoint.setLayoutX(playerBoardOffsetX + (98.0 / 1124) * playerBoardWidth + i * (64.0 / 1124) * playerBoardWidth);
                    imagePaneDamagePoint.setLayoutY(playerBoardOffsetY + (playerBoardOffsetY + playerBoardHeight) * playerCounter + (92.0 / 277) * playerBoardHeight);
                    collectorPane.getChildren().add(imagePaneDamagePoint);
                }

                // Setup other players marks
                ArrayList<Figure> marks = new ArrayList<>();
                for (Figure figure : smartPlayer.getMarks().keySet()) {
                    for (int i = 0; i < smartPlayer.getMarks().get(figure) ; i++) {
                        marks.add(figure);
                    }
                }
                for (int i = 0; i < marks.size() ; i++) {
                    ImagePane imagePaneDamagePoint = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blob" + damage.get(i).toString())),"-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                    imagePaneDamagePoint.setPrefHeight((76.0 / 277) * playerBoardHeight);
                    imagePaneDamagePoint.setPrefWidth((64.0 / 1124) * playerBoardWidth);
                    imagePaneDamagePoint.setLayoutX(playerBoardOffsetX + (543.0 / 1124) * playerBoardWidth + i * (64.0 / 1124) * playerBoardWidth / 2);
                    imagePaneDamagePoint.setLayoutY(playerBoardOffsetY + (playerBoardOffsetY + playerBoardHeight) * playerCounter + (57.0 / 277) * playerBoardHeight - (76.0 / 277) * playerBoardHeight);
                    collectorPane.getChildren().add(imagePaneDamagePoint);
                }

                // Setup other players ammo
                int colorCounter = 0;
                for (it.polimi.ingsw.model.enumeratedclasses.Color color : smartPlayer.getAmmo().keySet()) {
                    for (int i = 0 ; i < smartPlayer.getAmmo().get(color) ; i++) {
                        ImagePane imagePane = new ImagePane(properties.getProperty("ammoRoot").concat(properties.getProperty("ammo" + color.toString())),"-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                        imagePane.setPrefHeight((54.0 / 277) * playerBoardHeight);
                        imagePane.setPrefWidth((54.0 / 1124) * playerBoardWidth);
                        imagePane.setLayoutX(playerBoardOffsetX + (916.0 / 1124) * playerBoardWidth + i * (54.0 / 1124) * playerBoardWidth);
                        imagePane.setLayoutY(playerBoardOffsetY + (playerBoardOffsetY + playerBoardHeight) * playerCounter + (48.0 / 277) * playerBoardHeight + colorCounter * (54.0 / 277) * playerBoardHeight);
                        collectorPane.getChildren().add(imagePane);
                    }
                    colorCounter++;
                }

                // Setup other players pointtrack
                for (int i = 0 ; i < smartPlayer.getDeaths() ; i++) {
                    ImagePane imagePaneSkull = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blobSKULL")),"-fx-background-size: contain; -fx-background-repeat: no-repeat;");
                    imagePaneSkull.setPrefHeight((76.0 / 277) * playerBoardHeight);
                    imagePaneSkull.setPrefWidth((360.0 / 6 / 1124) * playerBoardWidth);
                    imagePaneSkull.setLayoutX(playerBoardOffsetX + (232.0 / 1124) * playerBoardWidth + i * (360.0 / 6 / 1124) * playerBoardWidth);
                    imagePaneSkull.setLayoutY(playerBoardOffsetY  + (playerBoardOffsetY + playerBoardHeight) * playerCounter + (176.0 / 277) * playerBoardHeight);
                    collectorPane.getChildren().add(imagePaneSkull);
                }

                playerCounter++;
            }

            // Setup players positions on the map
            ImagePane imagePaneFigure = new ImagePane(properties.getProperty("figuresRoot").concat(properties.getProperty("figure" + smartPlayer.getFigure().toString())),"-fx-background-size: contain; -fx-background-repeat: no-repeat;");
            if ((smartPlayer.getPosX() >= 0) && (smartPlayer.getPosY() >= 0)) {

                imagePaneFigure.setPrefHeight(figureHeight);
                imagePaneFigure.setPrefWidth(figureWidth);
                imagePaneFigure.setLayoutX(mapIconOffsetX + smartPlayer.getPosY() * (squareWidth) + squareWidth * 0.1 + spacing * 1.4 * playerTotalCounter);
                imagePaneFigure.setLayoutY(mapIconOffsetY + smartPlayer.getPosX() * (squareHeight) + squareHeight * 0.55);
                collectorPane.getChildren().add(imagePaneFigure);
                playerTotalCounter++;
            }

        }

        Platform.runLater(() -> gamePane.getChildren().add(collectorPane));
    }

    /**
     * This method is used to get the correct file name of a given tile
     * @param smartTile is the tile to be parsed
     * @return the correct file name
     */
    private String getTileFileName(SmartTile smartTile) {
        String fileName = "tile";
        for (it.polimi.ingsw.model.enumeratedclasses.Color color : smartTile.getAmmo())
            if (Objects.equals(color.toString(), "RED"))
                fileName += "R";
        for (it.polimi.ingsw.model.enumeratedclasses.Color color : smartTile.getAmmo())
            if (Objects.equals(color.toString(), "BLUE"))
                fileName += "B";
        for (it.polimi.ingsw.model.enumeratedclasses.Color color : smartTile.getAmmo())
            if (Objects.equals(color.toString(), "YELLOW"))
                fileName += "Y";
        for (int i = 0 ; i < smartTile.getPowerup() ; i++)
            fileName += "P";
        return fileName;
    }

    /**
     * This method is used to reset the root pane
     */
    private void setCleanScenario() {
        Platform.runLater(() -> rootPane.getChildren().clear());
    }

    /**
     * This method is used to reset the pane that contain the request from the server
     */
    private void cleanRequestPane() {
        pendingRequest.set(false);
        Platform.runLater(() -> requestPane.getChildren().clear());
    }

    /**
     * This method is used to reset the pane that contains the game map
     */
    private void cleanGamePane() {
        Platform.runLater(() -> gamePane.getChildren().clear());
    }

    /**
     * This method is used to set a scenario where the used ca select the info to login and start the game
     */
    private void setLoginScenario() {
        currentScenario.set(1);

        Text textWelcome = new Text("Welcome to Adrenaline!");
        textWelcome.setFont(Font.font("Tahoma", FontWeight.NORMAL,textSize));

        TextField textFieldNickname = new TextField();

        AtomicInteger networkType = new AtomicInteger();
        ToggleGroup toggleGroupNetworkChoice = new ToggleGroup();
        toggleGroupNetworkChoice.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> obsValue, Toggle oldToggle, Toggle newToggle) -> networkType.set((int) toggleGroupNetworkChoice.getSelectedToggle().getUserData()));

        RadioButton radioButtonRMI = new RadioButton("RMI");
        radioButtonRMI.setUserData(0);
        radioButtonRMI.setToggleGroup(toggleGroupNetworkChoice);
        radioButtonRMI.setSelected(true);
        RadioButton radioButtonSocket = new RadioButton("Socket");
        radioButtonSocket.setUserData(1);
        radioButtonSocket.setToggleGroup(toggleGroupNetworkChoice);

        HBox hBoxNetworkChoice = new HBox(radioButtonRMI,radioButtonSocket);
        hBoxNetworkChoice.setSpacing(10);
        hBoxNetworkChoice.setAlignment(Pos.CENTER);

        Button buttonLogin = new Button("Login");
        buttonLogin.setOnAction(behavior -> tryLogin(networkType.get(),textFieldNickname.getText()));

        HBox hBoxTextEvent = new HBox(textEvent);
        hBoxTextEvent.setAlignment(Pos.CENTER);

        GridPane gridPaneLogin = new GridPane();
        gridPaneLogin.setAlignment(Pos.CENTER);
        gridPaneLogin.setVgap(10);
        gridPaneLogin.setHgap(10);
        gridPaneLogin.add(textWelcome,0,0,3,1);
        gridPaneLogin.add(textFieldNickname,0,1,2,1);
        gridPaneLogin.add(buttonLogin,2,1);
        gridPaneLogin.add(hBoxNetworkChoice,0,2,3,1);
        gridPaneLogin.add(hBoxTextEvent,0,3,3,1);

        setCleanScenario();
        Platform.runLater(() -> textEvent.setText(""));
        Platform.runLater(() -> rootPane.getChildren().add(gridPaneLogin));
    }

    /**
     * This method is used to show a text that displays that the game is aboout to start
     */
    private void setStartWaitScenario() {
        currentScenario.set(2);

        Text textWait = new Text("Waiting for the game to start");
        textWait.setFont(Font.font("Tahoma",FontWeight.NORMAL,textSize));

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(textWait);
        borderPane.setTop(getTopBar());
        borderPane.setBottom(getBottomBar());

        setCleanScenario();
        Platform.runLater(() -> textEvent.setText(""));
        Platform.runLater(() -> rootPane.getChildren().add(borderPane));
    }

    /**
     * This method is used to set the scenario that contains the game map
     */
    private void setGameMapScenario() {
        currentScenario.set(3);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(gamePane);
        borderPane.setBottom(getBottomBar());
        if (pendingRequest.get())
            borderPane.setTop(getTopBarLinkedRequest());
        else
            borderPane.setTop(getTopBar());

        setCleanScenario();
        Platform.runLater(() -> textEvent.setText(""));
        Platform.runLater(() -> rootPane.getChildren().add(borderPane));
    }

    /**
     * This method is used to show a text that displays that the logout is being performed
     */
    private void setLogoutScenario() {
        currentScenario.set(4);

        Text textWait = new Text("Logging out...");
        textWait.setFont(Font.font("Tahoma",FontWeight.NORMAL,textSize));

        setCleanScenario();
        Platform.runLater(() -> textEvent.setText(""));
        Platform.runLater(() -> rootPane.getChildren().add(textWait));
    }

    /**
     * This method is used to set the scenario that contains the last request from the server
     */
    private void setRequestScenario() {
        currentScenario.set(5);
        pendingRequest.set(true);

        setCleanScenario();
        Platform.runLater(() -> rootPane.getChildren().add(requestPane));
    }

    /**
     * This method is used to create a new Client instance and to login with the specified network type and nickname
     * @param networkType This parameter determines which type of connection must be used
     *          0 - RMI
     *          1 - Socket
     * @param nickname is the nickname used to login
     */
    private void tryLogin(int networkType, String nickname) {
        try {
            client = new Client(networkType,this);
            this.nickname = nickname;
            int retVal = client.login(nickname);
            switch (retVal) {
                case 0: {
                    setStartWaitScenario();
                    break;
                }
                case 1: {
                    Platform.runLater(() -> textEvent.setText("Nickname already chosen"));
                    break;
                }
                case 2: {
                    smartModel = client.getModelUpdate();
                    if (smartModel != null) {
                        try {
                            setupGamePane();
                            setGameMapScenario();
                        } catch (Exception e) {
                            client.logout();
                        }
                    } else setStartWaitScenario();
                    break;
                }
                case 3: {
                    Platform.runLater(() -> textEvent.setText("Nickname already logged in"));
                    break;
                }
                case 4: {
                    Platform.runLater(() -> textEvent.setText("Nickname not registered"));
                    break;
                }
                default:
                    Platform.runLater(() -> textEvent.setText("Something very bad went wrong"));
            }
        } catch (Exception e) {
            Platform.runLater(() -> textEvent.setText("Server not available"));
        }
    }

    /**
     * This method is used to display a message opening a new windows
     * @param s is the message to be shown
     */
    private void popup(String s) {
        StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(10,10,10,10));

        Text textPopup = new Text(s);
        textPopup.setFont(Font.font("Tahoma",FontWeight.NORMAL,textSize));
        stackPane.getChildren().add(textPopup);


        Scene scene = new Scene(stackPane);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        if (args[0] != null)
            serverIp = args[0];
        else
            serverIp = null;
        if (args[1] != null)
            clientIp = args[1];
        else
            clientIp = null;
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        properties = new Properties();
        try {
            properties.load(Objects.requireNonNull(Client.class.getClassLoader().getResourceAsStream("graphics/references.properties")));
        } catch (Exception e) {
            System.err.println("Error reading graphics/references.properties");
            throw new Exception();
        }

        Properties networkProperties = new Properties();
        try {
            networkProperties.load(Objects.requireNonNull(Client.class.getClassLoader().getResourceAsStream("network_settings.properties")));
        } catch (Exception e) {
            System.err.println("Error reading network_settings.properties");
            throw new Exception();
        }

        if (serverIp == null)
            serverIp = networkProperties.getProperty("serverIp");
        if (clientIp == null)
            clientIp = networkProperties.getProperty("clientIp");
        this.height = 720;
        this.width = (this.height - 52) * 320/167;
        this.spacing = this.height / 72;
        this.textSize = 20;
        this.textEvent = new Text();
        this.textEvent.setFill(Color.FIREBRICK);
        this.currentScenario = new AtomicInteger();
        this.pendingRequest = new AtomicBoolean();
        this.gson = new Gson();
        this.rootPane = new StackPane();
        this.requestPane = new StackPane();
        this.gamePane = new Pane();
        this.smartModel = null;
        this.primaryScene = new Scene(this.rootPane,this.width,this.height);
        this.primaryStage = stage;
        this.primaryStage.setScene(this.primaryScene);
        this.primaryStage.setTitle("Adrenaline");
        this.primaryStage.setResizable(false);
        this.primaryStage.show();
        setLoginScenario();
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    @Override
    public void logout() {
        if (currentScenario.get() != 1)
            Platform.runLater(this::setLoginScenario);
        Platform.runLater(this::cleanRequestPane);
        client = null;
    }

    @Override
    public void sendMessage(String s) {
        Platform.runLater(() -> popup(s));
    }

    @Override
    public void notifyEvent(String s) {
        Platform.runLater(() -> textEvent.setText(s));
    }

    @Override
    public int choosePlayer(Figure[] f) {
        CustomStream customStream = new CustomStream();

        HBox hBoxChoice = new HBox();
        hBoxChoice.setAlignment(Pos.CENTER);
        hBoxChoice.setSpacing(10);

        for (int i = 0 ; i < f.length ; i++) {

            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(
                    new ImagePane(properties.getProperty("figuresRoot").concat(properties.getProperty("figure" + f[i].toString())),"-fx-background-size: contain; -fx-background-repeat: no-repeat;")
            );
            stackPane.setPrefHeight(((double) this.width) / 8);
            stackPane.setPrefWidth(((double) this.width) / 8);

            Button button = new Button();
            button.setGraphic(stackPane);
            button.setUserData(i);
            button.setOnAction(behavior -> {
                customStream.putLine(Integer.toString((int)button.getUserData()));
                cleanRequestPane();
                setGameMapScenario();
            });

            hBoxChoice.getChildren().add(button);
        }

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBarLinkedMap());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(hBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return Integer.parseInt(customStream.getLine());
    }

    @Override
    public int chooseWeapon(WeaponName[] w) {
        CustomStream customStream = new CustomStream();

        HBox hBoxChoice = new HBox();
        hBoxChoice.setAlignment(Pos.CENTER);
        hBoxChoice.setSpacing(10);

        for (int i = 0 ; i < w.length ; i++) {

            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(
                    new ImagePane(properties.getProperty("weaponsRoot").concat(properties.getProperty("weapon" + w[i].toString())),"-fx-background-size: contain; -fx-background-repeat: no-repeat;")
            );
            stackPane.setPrefHeight((((double) this.width) / 8) * 1.7);
            stackPane.setPrefWidth((((double) this.width) / 8));

            Button button = new Button();
            button.setGraphic(stackPane);
            button.setUserData(i);
            button.setOnAction(behavior -> {
                customStream.putLine(Integer.toString((int)button.getUserData()));
                cleanRequestPane();
                setGameMapScenario();
            });

            hBoxChoice.getChildren().add(button);
        }

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBarLinkedMap());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(hBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return Integer.parseInt(customStream.getLine());
    }

    @Override
    public int chooseString(String[] s) {
        CustomStream customStream = new CustomStream();

        VBox vBoxChoice = new VBox();
        vBoxChoice.setSpacing(10);
        vBoxChoice.setAlignment(Pos.CENTER);

        ListView<String> listView = new ListView<>();
        listView.setPrefHeight((double) this.height * 0.6);
        listView.setMaxWidth((double) this.width / 2);
        listView.setMinWidth((double) this.width / 2);

        ObservableList<String> viewItems = FXCollections.observableArrayList(s);
        listView.setItems(viewItems);

        Button buttonSelect = new Button("Select");
        buttonSelect.setOnAction(behavior -> {
            String selection = listView.getFocusModel().getFocusedItem();

            for (int i = 0 ; i < s.length ; i++) {
                if (selection.equals(s[i])) {
                    customStream.putLine(Integer.toString(i));
                    cleanRequestPane();
                    setGameMapScenario();
                }
            }
        });

        vBoxChoice.getChildren().addAll(listView,buttonSelect);

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBarLinkedMap());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(vBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return Integer.parseInt(customStream.getLine());
    }

    @Override
    public int chooseDirection(Character[] c) {
        CustomStream customStream = new CustomStream();

        HBox hBoxChoice = new HBox();
        hBoxChoice.setAlignment(Pos.CENTER);
        hBoxChoice.setSpacing(10);

        for (int i = 0 ; i < c.length ; i++) {

            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(
                    new ImagePane(properties.getProperty("directionsRoot").concat(properties.getProperty("direction_" + c[i].toString().toLowerCase())),"-fx-background-size: contain; -fx-background-repeat: no-repeat;")
            );
            stackPane.setPrefHeight((double) this.width / 10);
            stackPane.setPrefWidth((double) this.width / 10);

            Button button = new Button();
            button.setGraphic(stackPane);
            button.setUserData(i);
            button.setOnAction(behavior -> {
                customStream.putLine(Integer.toString((int)button.getUserData()));
                cleanRequestPane();
                setGameMapScenario();
            });

            hBoxChoice.getChildren().add(button);
        }

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBarLinkedMap());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(hBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return Integer.parseInt(customStream.getLine());
    }

    @Override
    public int chooseColor(it.polimi.ingsw.model.enumeratedclasses.Color[] c) {
        CustomStream customStream = new CustomStream();

        HBox hBoxChoice = new HBox();
        hBoxChoice.setAlignment(Pos.CENTER);
        hBoxChoice.setSpacing(10);

        for (int i = 0 ; i < c.length ; i++) {

            Button button = new Button();
            button.setStyle("-fx-background-color: " + c[i].toString() + ";");
            button.setPrefHeight((double) this.width / 8);
            button.setPrefWidth((double) this.width / 8);
            button.setUserData(i);
            button.setOnAction(behavior -> {
                customStream.putLine(Integer.toString((int)button.getUserData()));
                cleanRequestPane();
                setGameMapScenario();
            });

            hBoxChoice.getChildren().add(button);
        }

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBarLinkedMap());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(hBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return Integer.parseInt(customStream.getLine());
    }

    @Override
    public int choosePowerup(Powerup[] p) {
        CustomStream customStream = new CustomStream();

        HBox hBoxChoice = new HBox();
        hBoxChoice.setAlignment(Pos.CENTER);
        hBoxChoice.setSpacing(10);

        for (int i = 0 ; i < p.length ; i++) {

            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(
                    new ImagePane(properties.getProperty("powerupsRoot").concat(properties.getProperty("powerup" + p[i].getName().toString() + "_" + p[i].getColor().toString())),"-fx-background-size: contain; -fx-background-repeat: no-repeat;")
            );
            stackPane.setPrefHeight((((double) this.width) / 8) * 1.56);
            stackPane.setPrefWidth((((double) this.width) / 8));

            Button button = new Button();
            button.setGraphic(stackPane);
            button.setUserData(i);
            button.setOnAction(behavior -> {
                customStream.putLine(Integer.toString((int)button.getUserData()));
                cleanRequestPane();
                setGameMapScenario();
            });

            hBoxChoice.getChildren().add(button);
        }

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBarLinkedMap());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(hBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return Integer.parseInt(customStream.getLine());
    }

    @Override
    public int chooseMap(int[] m) {
        CustomStream customStream = new CustomStream();

        Text textQuestion = new Text("Select the map");
        textQuestion.setFont(Font.font("Tahoma",FontWeight.NORMAL,textSize));

        HBox hBoxChoice = new HBox();
        hBoxChoice.setAlignment(Pos.CENTER);
        hBoxChoice.setSpacing(10);

        for (int i = 0 ; i < m.length ; i++) {

            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(
                    new ImagePane(properties.getProperty("mapsiconsRoot").concat(properties.getProperty("mapicon" + (m[i]+1))),"-fx-background-size: contain; -fx-background-repeat: no-repeat;")
            );
            stackPane.setPrefHeight((((double) this.width) / 6) / 1.3);
            stackPane.setPrefWidth((((double) this.width) / 6));

            Button button = new Button();
            button.setGraphic(stackPane);
            button.setUserData(i);
            button.setOnAction(behavior -> {
                customStream.putLine(Integer.toString((int)button.getUserData()));
                cleanRequestPane();
                setStartWaitScenario();
            });

            hBoxChoice.getChildren().add(button);
        }

        VBox vBoxChoice = new VBox();
        vBoxChoice.setSpacing(10);
        vBoxChoice.setAlignment(Pos.CENTER);
        vBoxChoice.getChildren().addAll(textQuestion,hBoxChoice);

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBar());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(vBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return Integer.parseInt(customStream.getLine());
    }

    @Override
    public int chooseMode(Character[] c) {
        CustomStream customStream = new CustomStream();

        Text textQuestion = new Text("Select the game mode");
        textQuestion.setFont(Font.font("Tahoma",FontWeight.NORMAL,textSize));

        HBox hBoxChoice = new HBox();
        hBoxChoice.setAlignment(Pos.CENTER);
        hBoxChoice.setSpacing(10);

        for (int i = 0 ; i < c.length ; i++) {

            Button button = new Button();

            switch (c[i]) {
                case 'N': {
                    button.setText("Normal");
                    break;
                }
                case 'D': {
                    button.setText("Domination");
                    break;
                }
                default:
                    button.setText(c[i].toString());
            }

            button.setPrefWidth(120);
            button.setUserData(i);
            button.setOnAction(behavior -> {
                customStream.putLine(Integer.toString((int)button.getUserData()));
                cleanRequestPane();
                setStartWaitScenario();
            });

            hBoxChoice.getChildren().add(button);
        }

        VBox vBoxChoice = new VBox();
        vBoxChoice.setSpacing(10);
        vBoxChoice.setAlignment(Pos.CENTER);
        vBoxChoice.getChildren().addAll(textQuestion,hBoxChoice);

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBar());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(vBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return Integer.parseInt(customStream.getLine());
    }

    @Override
    public int chooseSquare(int[][] s) {
        CustomStream customStream = new CustomStream();

        Pane choicePane = new Pane();

        ImagePane imagePaneMap = new ImagePane(properties.getProperty("mapsiconsRoot").concat(properties.getProperty("mapicon" + (smartModel.getMapIndex()+1))),"-fx-background-size: contain; -fx-background-repeat: no-repeat;");
        imagePaneMap.setPrefHeight((height-36-16) * 0.7);
        imagePaneMap.setPrefWidth(imagePaneMap.getPrefHeight()*1.3);
        imagePaneMap.setLayoutX((width-imagePaneMap.getPrefWidth())/2);
        imagePaneMap.setLayoutY((height-36-16-imagePaneMap.getPrefHeight())/2);

        choicePane.getChildren().add(imagePaneMap);

        for (int i = 0 ; i < s[0].length ; i++) {

            Button button = new Button();
            button.setStyle("-fx-background-color: rgba(0,128,0,0.35); -fx-border-color: green; -fx-border-width: 2;");
            button.setPrefHeight(imagePaneMap.getPrefHeight()*((double) 1318 / 1488) / 3);
            button.setPrefWidth(imagePaneMap.getPrefWidth()*((double) 1733 / 1932) / 4);
            double offsetX = ((width - imagePaneMap.getPrefWidth()) / 2 ) + (imagePaneMap.getPrefWidth() * (85.0 / 1488)) ;
            button.setLayoutX(offsetX + button.getPrefWidth() * s[1][i]);
            double offsetY = (((height - 36 -16) - imagePaneMap.getPrefHeight()) / 2 ) + (imagePaneMap.getPrefHeight() * (99.5 / 1932)) ;
            button.setLayoutY(offsetY + button.getPrefHeight() * s[0][i]);
            button.setUserData(i);
            button.setOnAction(behavior -> {
                customStream.putLine(Integer.toString((int)button.getUserData()));
                cleanRequestPane();
                setGameMapScenario();
            });

            choicePane.getChildren().add(button);
        }

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBarLinkedMap());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(choicePane);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return Integer.parseInt(customStream.getLine());
    }

    @Override
    public int booleanQuestion(String s) {
        CustomStream customStream = new CustomStream();

        Text textQuestion = new Text(s);
        textQuestion.setFont(Font.font("Tahoma",FontWeight.NORMAL,textSize));

        Button buttonTrue  = new Button("Yes");
        buttonTrue.setPrefWidth(80);
        buttonTrue.setOnAction(behavior -> {
            customStream.putLine(Integer.toString(1));
            cleanRequestPane();
            setGameMapScenario();
        });

        Button buttonFalse = new Button("No");
        buttonFalse.setPrefWidth(80);
        buttonFalse.setOnAction(behavior -> {
            customStream.putLine(Integer.toString(0));
            cleanRequestPane();
            setGameMapScenario();
        });

        HBox hBoxChoice = new HBox();
        hBoxChoice.setAlignment(Pos.CENTER);
        hBoxChoice.setSpacing(10);
        hBoxChoice.getChildren().addAll(buttonTrue,buttonFalse);

        VBox vBoxChoice = new VBox();
        vBoxChoice.setSpacing(10);
        vBoxChoice.setAlignment(Pos.CENTER);
        vBoxChoice.getChildren().addAll(textQuestion,hBoxChoice);

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBarLinkedMap());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(vBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return Integer.parseInt(customStream.getLine());
    }

    @Override
    public int[] chooseMultiplePowerup(Powerup[] p) {
        CustomStream customStream = new CustomStream();

        HBox hBoxButtonGroup = new HBox();
        hBoxButtonGroup.setSpacing(10);
        hBoxButtonGroup.setAlignment(Pos.CENTER);

        ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        for (int i = 0 ; i < p.length ; i++) {

            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(
                    new ImagePane(properties.getProperty("powerupsRoot").concat(properties.getProperty("powerup" + p[i].getName().toString() + "_" + p[i].getColor().toString())),"-fx-background-size: contain; -fx-background-repeat: no-repeat;")
            );
            stackPane.setPrefHeight((((double) this.width) / 8) * 1.56);
            stackPane.setPrefWidth((((double) this.width) / 8));
            stackPane.setPadding(new Insets(10,10,10,10));

            CheckBox checkBox = new CheckBox();
            checkBox.setUserData(i);
            checkBox.setGraphic(stackPane);

            checkBoxes.add(checkBox);
            hBoxButtonGroup.getChildren().add(checkBox);
        }

        Button buttonSelect = new Button("Select");
        buttonSelect.setOnAction(behavior -> {
            ArrayList<Integer> arrayList = new ArrayList<>();
            for (CheckBox checkBox : checkBoxes)
                if (checkBox.isSelected())
                    arrayList.add((Integer) checkBox.getUserData());
            Integer[] array = arrayList.toArray(new Integer[arrayList.size()]);
            customStream.putLine(gson.toJson(array));
            cleanRequestPane();
            setGameMapScenario();
        });

        VBox vBoxChoice = new VBox();
        vBoxChoice.setSpacing(10);
        vBoxChoice.setAlignment(Pos.CENTER);
        vBoxChoice.getChildren().addAll(hBoxButtonGroup,buttonSelect);

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBarLinkedMap());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(vBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return gson.fromJson(customStream.getLine(),int[].class);
    }

    @Override
    public int[] chooseMultipleWeapon(WeaponName[] w) {
        CustomStream customStream = new CustomStream();

        HBox hBoxButtonGroup = new HBox();
        hBoxButtonGroup.setSpacing(10);
        hBoxButtonGroup.setAlignment(Pos.CENTER);

        ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        for (int i = 0 ; i < w.length ; i++) {

            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(
                    new ImagePane(properties.getProperty("weaponsRoot").concat(properties.getProperty("weapon" + w[i].toString())),"-fx-background-size: contain; -fx-background-repeat: no-repeat;")
            );
            stackPane.setPrefHeight((((double) this.width) / 8) * 1.7);
            stackPane.setPrefWidth((((double) this.width) / 8));
            stackPane.setPadding(new Insets(10,10,10,10));

            CheckBox checkBox = new CheckBox();
            checkBox.setUserData(i);
            checkBox.setGraphic(stackPane);

            checkBoxes.add(checkBox);
            hBoxButtonGroup.getChildren().add(checkBox);
        }

        Button buttonSelect = new Button("Select");
        buttonSelect.setOnAction(behavior -> {
            ArrayList<Integer> arrayList = new ArrayList<>();
            for (CheckBox checkBox : checkBoxes)
                if (checkBox.isSelected())
                    arrayList.add((Integer) checkBox.getUserData());
            Integer[] array = arrayList.toArray(new Integer[arrayList.size()]);
            customStream.putLine(gson.toJson(array));
            cleanRequestPane();
            setGameMapScenario();
        });

        VBox vBoxChoice = new VBox();
        vBoxChoice.setSpacing(10);
        vBoxChoice.setAlignment(Pos.CENTER);
        vBoxChoice.getChildren().addAll(hBoxButtonGroup,buttonSelect);

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBarLinkedMap());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(vBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return gson.fromJson(customStream.getLine(),int[].class);
    }

    @Override
    public void notifyModelUpdate() {
        try {
            smartModel = client.getModelUpdate();
            if (smartModel != null) {
                setupGamePane();
                if (currentScenario.get() == 2)
                    Platform.runLater(this::setGameMapScenario);
            }
        } catch (Exception ignored) {
        }
    }

}

class ImagePane extends Pane {

    ImagePane(String imageRef) {
        this(imageRef,"-fx-background-size: cover; -fx-background-repeat: no-repeat;");
    }

    ImagePane(String imageRef, String style) {
        this(new SimpleStringProperty(imageRef),new SimpleStringProperty(style));
    }

    ImagePane(StringProperty imageRefProperty, StringProperty styleProperty) {
        styleProperty().bind(
                new SimpleStringProperty("-fx-background-image: url(\"")
                        .concat(imageRefProperty)
                        .concat(new SimpleStringProperty("\");"))
                        .concat(styleProperty)
        );
    }
}