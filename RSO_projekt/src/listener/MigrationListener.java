package listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class MigrationListener implements Runnable {

	private static int port;
	private static ServerSocket serverSocket;
	
	private static InetAddress listenerIP;

	public MigrationListener(int _port)
	{
		port = _port;
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
	                System.out.println("MigrationListener on: " + iface.getDisplayName() + " " + listenerIP + ":" + port);
	            }
	        }
	    } catch (SocketException e) {
	        throw new RuntimeException(e);
	    }
		Thread t = new Thread(this);
		t.start();
	}
	
	public void run() 
	{
		try {
		    serverSocket = new ServerSocket(port, 0, listenerIP);
		} 
		catch (IOException e) {
		    System.out.println("Could not listen on port: " + port);
		}
		
		while (true)
		{
			Socket clientSocket = null;
			try {
			    clientSocket = serverSocket.accept();
			} 
			catch (IOException e) {
			    System.out.println("Accept failed");
			    System.exit(-1);
			}
			
			BufferedReader in;
			try {
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {   
					System.out.println(inputLine);
					if (inputLine.equals("Bye."))
						break;
				}
				in.close();
				clientSocket.close();
			} catch (IOException e) {
				System.err.println("IOException: " + e);
				e.printStackTrace();
			}
		}
	}
}
