package server.networking;

import server.model.UserModel;
import transferobjects.ErrorMessage;
import transferobjects.LoginRequest;
import transferobjects.User;
import util.LogInException;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

/**
 * <p>The <code>ServerHandler</code> is having a role of handling a client connection on the server side
 * of the <b>CanEat</b> application.
 * </p>
 * <p>
 *   As a <code>ServerHandler</code> object can handle one client connection in it's lifetime,
 *   therefore for each connection request from the clients, a new instance is created.
 * </p>
 * <p>
 *   To fully take advantage of the features of the <code>ServerHandler</code>, it must be put on a new Thread,
 *   so that it can listen simultaneously to the client while the other parts of the program can work
 *   on their job.
 * </p>
 */
public class ServerHandler implements Runnable
{
  private Socket clientSocket; //Client socket
  private ObjectInputStream fromClient; //Stream to receive objects from client
  private ObjectOutputStream toClient; //Stream to send objects to client

  private UserModel userModel;

  /**
   * Constructs the ServerHandler object, sets up the base streams.
   * @param clientSocket Client Socket to be taken care of.
   * @param userModel User model to forward data to.
   */
  public ServerHandler(Socket clientSocket, UserModel userModel) {
    try{
      System.out.printf("Client connected (%s:%s)%n", clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());

      this.clientSocket = clientSocket;
      toClient = new ObjectOutputStream(clientSocket.getOutputStream());
      fromClient = new ObjectInputStream(clientSocket.getInputStream());
      this.userModel = userModel;

    } catch (IOException e){
      // If an IOException happens during the initialization of the streams, it means that there is
      // something wrong with the socket, and therefore should close the client.
      closeClient();
    }
  }

  /**
   * Send a <code>Serializable</code> object to the Client.
   * @param o Object to be sent.
   */
  public void sendObject(Serializable o){
    try{
      // Send the object here
      System.out.println(o);
      toClient.writeObject(o);

    }
    catch (IOException | NullPointerException e) {
      // If an IOException happens during the sending process, it means that there is something
      // wrong with the stream, and therefore should close the client.
      e.printStackTrace();
      closeClient();
    }
  }

  /**
   * Listen to client and handle receiving object.
   */
  @Override public void run() {
    try{
      while(true){
        //Object variable to load the object we get from the client
        Object receivedObj;

        try{
          //read an object from the client
          receivedObj = fromClient.readObject();

        } catch (ClassNotFoundException e){
          // Catch the exception above. (Catch it here, because this exception should not happen.
          //  If it happens, then don't close the whole stream, just skip to read the next
          //  object from the client.)
          e.printStackTrace();
          continue;
        }

        // in case the received object is a LoginRequest object
        if(receivedObj instanceof LoginRequest){
          LoginRequest request = (LoginRequest) receivedObj;

          try{
            // User variable to load the object we receive from the model
            User user;

            // Check if the request is actually a login or a register request
            if(request.getIsRegister())
              user = userModel.register(request);
            else
              user = userModel.login(request);

            // Send the Logged in - User object back to the client
            sendObject(user);
          } catch (SQLException | LogInException e){
            // This exception type above should be more specific after we implement the model

            // Constructing the Error Message object
            ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
            // Sending the Error Message object back to client
            sendObject(errorMessage);
          }
        }
      }
    } catch (IOException | NullPointerException e){
      // If an IOException happens during the reading process, it means that there is something
      // wrong with the stream (e.g. lost connection), and therefore should close the client.
      e.printStackTrace();
      closeClient();
    }
  }

  /**
   * Close client and fire closing event.
   */
  public void closeClient(){

    // todo fire "Closing" event here for the connection pool (later)
    //      so the pool can remove it form the list when the connection is closed

    // Close streams
    closeObject(toClient);
    closeObject(fromClient);
    closeObject(clientSocket);

    //Set the objects to null, so the Garbage Collector can collect the unused streams.
    toClient = null;
    fromClient = null;
    clientSocket = null;
  }

  /**
   * Close a <code>Closeable</code> (e.g. streams or socket) without raising an exception.
   * @param closeable Object to close
   */
  private void closeObject(Closeable closeable){
    try{
      //Try to close the object
      closeable.close();
    }catch (IOException | NullPointerException ignored){
      // Ignore the exception part, as it should NOT raise any exception
    }
  }
}
