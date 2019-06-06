package it.polimi.ingsw.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerRemote extends Remote {

    int ping(ClientRemote c) throws RemoteException;
    int login(String s, ClientRemote c) throws RemoteException;
    void logout(ClientRemote c) throws RemoteException;
    String getModelUpdate() throws RemoteException;
}