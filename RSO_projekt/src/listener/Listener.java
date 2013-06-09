package listener;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

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
	
	private static InetAddress listenerIP;

	public Listener(int _port) 
	{
		//trzeba ogarnac jakiego interfejsu uzyc
	    try {
	        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
	        while (interfaces.hasMoreElements()) {
	            NetworkInterface iface = interfaces.nextElement();
	            //sprawdzamy ktory NIC jest uruchomiony
	            if (iface.isLoopback() || !iface.isUp())
	                continue;
	            Enumeration<InetAddress> addresses = iface.getInetAddresses();
	            while(addresses.hasMoreElements()) {
	                InetAddress addr = addresses.nextElement();
	                //pomijamy adresy IPv6
	                if (addr instanceof Inet6Address) continue;
	                listenerIP = addr;
	                System.out.println("Listening on: " + iface.getDisplayName() + " " + listenerIP + ":27017");
	            }
	        }
	    } catch (SocketException e) {
	        throw new RuntimeException(e);
	    }
		port = _port;
		if (setupSocket(port))
		{
			initAndListen();
		}
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
			serverSocket = new ServerSocket(port, 0, listenerIP);
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