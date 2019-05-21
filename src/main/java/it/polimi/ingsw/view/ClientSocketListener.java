package it.polimi.ingsw.view;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class keeps running during the game and receives the socket method invocations from controller to client
 * @author simonebraga
 */
public class ClientSocketListener implements Runnable {

    private Client client;
    private ControllerSocket controllerSocket;
    private Scanner in;


    /**
     * This method initializes the class with the correct parameters
     * @param client is the client associated to the listener
     * @param controllerSocket is the class used to communicate with the controller
     */
    public ClientSocketListener(Client client, ControllerSocket controllerSocket) {
        this.client = client;
        this.controllerSocket = controllerSocket;
        System.out.println("ClientSocketListener created");
    }

    /**
     * This method keeps running waiting messages from the socket input stream
     */
    @Override
    public void run() {

        try {
            in = new Scanner(controllerSocket.getSocket().getInputStream());

            while (true) {

                String method = "";
                String parameters = "";
                String line = in.nextLine();
                int pos = 0;

                if (line.equals("quit")) {
                    break;
                }

                while ((pos < line.length() && (line.charAt(pos) != ';'))) {
                    pos++;
                }

                if (pos >= line.length()) {
                    System.out.println("Received invalid syntax message: " + line);
                } else {
                    method = line.substring(0,pos);
                    parameters = line.substring(pos + 1);

                    // This switch-case must be configured to invoke all the remote methods of Client with the correct parameters
                    switch (method) {
                        case "printMessage": {
                            client.printMessage(parameters);
                            break;
                        }
                        case "singleChoice": {
                            // TODO
                        }
                        case "multipleChoice": {
                            // TODO
                        }
                        case "booleanQuestion": {
                            // TODO
                        }
                        default: {
                            System.out.println("Received: " + line);
                        }
                    }
                }
            }
            in.close();
            controllerSocket.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
