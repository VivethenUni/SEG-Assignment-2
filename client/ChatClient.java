// Assignment 2 Done By Vivethen Balachandiran (ID: 300245080)
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI;

  //stores loginID of client
  private String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      if (message.startsWith("#")) {
        handleCommand(message);
      }
      else {
        sendToServer(getLoginID() + "> " + message);
      }
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  /**
   * This method handles all commands coming from the UI            
   *
   * @param command The command from the UI.    
   */
  private void handleCommand (String command) {
    String[] commandParams = command.split(" ");
    boolean canComplete = true;
    String logID;
    	    
    try {
    	logID = commandParams[1];
    }
    catch(ArrayIndexOutOfBoundsException e){ 	    	
    	logID = null;
    }

    if (commandParams[0].equals("#quit")) {
      quit();
    }
    else if (commandParams[0].equals("#logoff")) {
      if (isConnected() == true) {
        try {
          closeConnection();
        }
        catch(IOException e) {
          clientUI.display("Can not log off since not logged in");
        }
      }
      else {
        clientUI.display("Can not log off since not logged in");
      }
    }
    else if (commandParams[0].equals("#sethost")) {
      if (isConnected() == false) {
        if ((commandParams[1] != null) && (commandParams[1] != "")){
          setHost(commandParams[1]);
          clientUI.display("The host is now set to: " + commandParams[1]);
        }
        else {
          clientUI.display("A host name was not provided to change to");
        }
        
      }
      else {
        clientUI.display("Host can only be changed if logged off");
      }
    }
    else if (commandParams[0].equals("#setport")) {
      if (isConnected() == false) {
        if ((commandParams[1] != null) && (commandParams[1] != "")){
          try {
            Integer.parseInt(commandParams[1]);
            //canComplete stays true if no catch
          }
          catch (NumberFormatException e) {
            clientUI.display("Port number must be a number");
            canComplete = false;
          }
          if (canComplete == true) {
            setPort(Integer.parseInt(commandParams[1]));
            clientUI.display("The port is now set to: " + commandParams[1]);
          }
        }
        else {
          clientUI.display("A port number was not provided to change to");
        }
        
      }
      else {
        clientUI.display("Port can only be changed if logged off");
      }
    }
    else if (commandParams[0].equals("#login")) {
      if (isConnected() == false) {
        try {
          if (logID != null) {
            openConnection(); 
            loginID = commandParams[1];
            sendToServer("#login " + commandParams[1]);
            //clientUI.display("Log in successful");
          }
          else {
            clientUI.display("Can not login without a login ID");
          }
        }
        catch (IOException ex) {}
        catch(ArrayIndexOutOfBoundsException e){}
      }
      else {
        clientUI.display("You are already logged in");
      }
    }
    else if (commandParams[0].equals("#gethost")) {
      clientUI.display("The host name is: " + getHost());
    }
    else if (commandParams[0].equals("#getport")) {
      clientUI.display("The port number is: " + Integer.toString(getPort()));
    }
    else {
      clientUI.display("The " + commandParams[0] + " command does not exist");
    }
  }

  //returns loginID
  public String getLoginID() {
    return loginID;
  }

  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  /**
	 * Implements the hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  @Override
	protected void connectionException(Exception exception) {
    clientUI.display("The server has shut down");
    quit();
	}

  /**
	 * Implements the hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
  @Override
	protected void connectionClosed() {
    clientUI.display("The connection closed");
	}

}
//End of ChatClient class
