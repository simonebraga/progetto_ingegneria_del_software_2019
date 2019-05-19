package it.polimi.ingsw.model.gamelogic.effectscreator;

import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;

/**
 * Creates and sets the effect that shoots around the player to targets on different squares
 * or to all the targets.
 */
public class ShootShockWaveCreator implements EffectsCreator{

    /**
     * The player that shoots.
     */
    private Player player;

    /**
     * Represents if the shoot is is normal mode or in tsunami mode.
     */
    private Boolean tsunami;

    /**
     * Default constructor. Sets all the attributes.
     */
    public ShootShockWaveCreator(Player shooter, Boolean tsunami) {
        this.player = shooter;
        this.tsunami = tsunami;
    }

    public ShootShockWaveCreator() {
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public ArrayList<FunctionalEffect> run(Controller controller, GameTable table, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<FunctionalEffect> effects =new ArrayList<>();
        ArrayList<Square> squaresTarget = new ArrayList<>(table.getGameMap().getDistance(player.getPosition(), 1));
        ArrayList<Player> playersTarget = new ArrayList<>();

        squaresTarget.forEach(square -> playersTarget.addAll(square.getPlayers()));
        if(playersTarget.isEmpty()){
            throw new IllegalActionException();
        }

        if(tsunami){
            playersTarget.forEach(playerTarget -> {
                effects.add(new FunctionalFactory().createDamagePlayer(player, playerTarget, 1, 0));
                if(!targets.getPlayersTargeted().contains(playerTarget)){
                    targets.getPlayersTargeted().add(playerTarget);
                }
                if(!targets.getPlayersDamaged().contains(playerTarget)){
                    targets.getPlayersDamaged().add(playerTarget);
                }
            });
        }else{
            Player target1;
            Player target2;
            Player target3;
            target1 = controller.choosePlayer(player, playersTarget);
            effects.add(new FunctionalFactory().createDamagePlayer(player, target1, 1, 0));
            targets.getPlayersTargeted().add(target1);
            targets.getPlayersDamaged().add(target1);
            playersTarget.removeAll(target1.getPosition().getPlayers());

            if(!playersTarget.isEmpty()){
                target2 = controller.choosePlayer(player, playersTarget);
                effects.add(new FunctionalFactory().createDamagePlayer(player, target2, 1, 0));
                targets.getPlayersTargeted().add(target2);
                targets.getPlayersDamaged().add(target2);
                playersTarget.removeAll(target1.getPosition().getPlayers());

                if (!playersTarget.isEmpty()){
                    target3 = controller.choosePlayer(player, playersTarget);
                    effects.add(new FunctionalFactory().createDamagePlayer(player, target3, 1, 0));
                    targets.getPlayersTargeted().add(target3);
                    targets.getPlayersDamaged().add(target3);
                }
            }
        }
        return effects;
    }

    public Player getPlayer() {
        return player;
    }

    public Boolean getTsunami() {
        return tsunami;
    }

    public void setTsunami(Boolean tsunami) {
        this.tsunami = tsunami;
    }
}