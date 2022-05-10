package client.networking;

import transferobjects.ErrorMessage;
import transferobjects.Request;
import transferobjects.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;


/**
 *  A class that is responsible for handling all the new incoming client connections
 *  @author Beatricia
 *  @version 1
 */


public class ClientHandler implements Runnable {
    private final Socket socket; // An unconnected socket
    private final ObjectInputStream fromServer; //Stream to receive objects from server
    private final ObjectOutputStream toServer; //Stream to send objects to server
    private final SocketClient client;

    /**
     * Creates a stream socket and connects it to the 1111 port number at the "localhost" address
     * Sets up the base streams
     * @param client to be taken care of
     * @throws IOException thrown if an I/O error occurs when creating the socket
     */
    public ClientHandler(SocketClient client) throws IOException {
        socket = new Socket("localhost", 1111);
        fromServer = new ObjectInputStream(socket.getInputStream());
        toServer = new ObjectOutputStream(socket.getOutputStream());
        this.client = client;
    }

    /**
     * Listen to Server and handle the receiving object
     */
    @Override
    public void run() {
        try {
            while(true){
                Object obj = fromServer.readObject();

                //in case the received object is a User
                if(obj instanceof User){
                    client.userReceivedFromServer((User)obj);
                }
                // in case the received object is a ErrorMessage
                else if(obj instanceof ErrorMessage){
                    client.errorReceivedFromServer((ErrorMessage) obj);
                }
                else if(obj instanceof Request)
                {
                    Request request = (Request) obj;
                    if(request.getRequestName().equals("MenuItemRequest"))
                    {
                        client.listOfMenuItemsReceived(request);
                    }

                    else if(request.getRequestName().equals("PendingUserRequest"))
                    {
                        client.listOfEmployeeReceived(request);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    /**
     * Sends the Serializable object to the Server
     * @param serializable to be sent
     */
    public void send(Serializable serializable) {
        try {
            toServer.writeObject(serializable);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
