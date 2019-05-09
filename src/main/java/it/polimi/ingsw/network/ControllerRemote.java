package it.polimi.ingsw.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface lists all the methods of the controller accessible from the client
 */
public interface ControllerRemote extends Remote {

    void login(String s, ClientRemote c) throws RemoteException;
}