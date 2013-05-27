package listener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Klasa nasluchiwacza. 
 * Tworzy socket nasluchujacy na porcie 27017.
 * Dla kazdego przychodzacego polaczenia tworzy nowy watek.
 * Nowy wantek rozpoczyna ClientHandler.
 * 
 * @author Piotr Cebulski
 *
 */
public class Listener 
{
	private static int port;
	private static ServerSocket serverSocket;
	

	public Listener(int _port) 
	{
		port = _port;
		if (setupSocket(port))
		{
			initAndListen();
		}
	}

	/**MAIN**/
	public static void main(String[] args) throws IOException
	{
		new Listener(27017);
	}
	
	
	/**
	 * Tworzy obiekt serverSocket.
	 * @param addr
	 * @param port
	 * @return
	 * @throws IOException 
	 */
	static boolean setupSocket(int port)
	{
		 serverSocket = null;
		   try {
			serverSocket = new ServerSocket(port);
		   } catch (IOException e) {
		       System.err.println("Could not listen on port: " + port);
		       System.exit(1);
		   }
		   return true;
	}

	/**
	 * Nasluchuje nadchodzacych polaczen i inicjuje nowe watki.
	 */
	void initAndListen()
	{
	   while (true)
	   {
		   try
		   {
			   Socket clientSocket = serverSocket.accept();
			   //Startuje nowy watek z odbieraniem wiadomosci.
			   new ClientHandler(clientSocket, this);
		   }
		   catch (IOException e)
		   {
				System.err.println("Accept failed.");
				System.exit(1);
		   }
	   }
	}
	
}