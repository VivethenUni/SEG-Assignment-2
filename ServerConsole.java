// Assignment 2 Done By Vivethen Balachandiran (ID: 300245080)
// New file not provided made in assignment made 100% by me

import java.io.*;
import common.*;
import java.util.Scanner;
import java.net.BindException;

// (similar/almost identical to clientConsole implementation)
public class ServerConsole implements ChatIF {

    //Instance variables

    EchoServer servClient;

    Scanner fromConsole;

    final public static int DEFAULT_PORT = 5555; //same as client console

    //Constructors

    public ServerConsole(int portNum) {
        try {
            servClient = new EchoServer(portNum);
        }
        catch (Exception exception) {
            System.out.println("Error: Can't setup connection! Terminating server");
            System.exit(1);
        }

        fromConsole = new Scanner(System.in);
    }

    //Instance methods

    public void accept(){
		try {
			String message;
			while (true) {
				try {
					servClient.listen();
				}
                catch (BindException e) {
					display("The " + servClient.getPort() + " port is already being used");
					servClient.setPort(DEFAULT_PORT);
                    display("Switching to default port 5555");
				}
				message = fromConsole.nextLine();
				display("SERVER MESSAGE> "+ message);
				servClient.sendToAllClients("SERVER MESSAGE> " + message);
				if (message.startsWith("#")){
					handleCommand(message);
				}
			}
		}
        catch (Exception ex) {
		    System.out.println("Unexpected error while reading from console!");
            ex.printStackTrace();
		}
	}

    public void display(String message) {
        System.out.println("> " + message);
    }

    private void handleCommand (String command) {
        String[] commandParams = command.split(" ");
        boolean canComplete = true;
        if (commandParams[0].equals("#quit")) {
            try {
                servClient.close();
            }
            catch(IOException e) {}
            System.exit(0);
        }
        else if (commandParams[0].equals("#stop")) {
            servClient.stopListening();
        }
        else if (commandParams[0].equals("#close")) {
            try {
                servClient.close();
            }
            catch(IOException e) {}
        }
        else if (commandParams[0].equals("#setport")) {
            if (servClient.isListening() == false) {
              if ((commandParams[1] != null) && (commandParams[1] != "")){
                try {
                  Integer.parseInt(commandParams[1]);
                }
                catch (NumberFormatException e) {
                  display("Port number must be a number");
                  canComplete = false;
                }
                if (canComplete == true) {
                  servClient.setPort(Integer.parseInt(commandParams[1]));
                  display("The port is now set to: " + commandParams[1]);
                }
              }
              else {
                display("A port number was not provided to change to");
              }
              
            }
            else {
              display("Port can only be changed if logged off");
            }
        }
        else if (commandParams[0].equals("#start")) {
            try {
                servClient.listen();
            }
            catch(IOException e) {}
        }
        else if (commandParams[0].equals("#getport")) {
            display("The port number is: " + Integer.toString(servClient.getPort()));
        }
        else {
            display("The " + commandParams[0] + " command does not exist");
        }

    }

    public static void main(String[] args) {
    int port = 0;
    try {
      port = Integer.parseInt(args[0]);
    }
    catch(ArrayIndexOutOfBoundsException e) {
      port = DEFAULT_PORT;
    }
    catch(NumberFormatException ne) {
      port = DEFAULT_PORT;
    }
    ServerConsole servClient= new ServerConsole(port);
    servClient.accept();  //Wait for console data
  }

}