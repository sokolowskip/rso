package configserver;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Enumeration;

/**
 * Klasa sluzy do uruchamiani rejestru RMI na domyslnym porcie. Tworzy obiekt
 * RemImpl.
 * 
 * @author Piotr Cebulski
 */

public class RemServer {
		
	private String serverIP;

	public RemServer() {
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
	                serverIP = addr.getCanonicalHostName();
	                System.out.println("Config server interface: " + iface.getDisplayName() + " " + serverIP + ":1099");
	            }
	        }
	    } catch (SocketException e) {
	        throw new RuntimeException(e);
	    }
	    //ustawiamy cos tam
	    System.setProperty("java.rmi.server.hostname", serverIP);
//	    System.setProperty("java.rmi.server.codebase", System.getProperty("user.dir")) + ".jar");
		try { // special exception handler for registry creation
			Registry r = LocateRegistry.createRegistry(1099);
			// Tworzymy SKELTON
			r.rebind("//" + serverIP + "/Rem", new RemImpl());
			System.out.println("java RMI registry created.");
		} catch (RemoteException e) {
			// do nothing, error means registry already exists
			System.err.println("java RMI registry already exists.");
		} 
	}
}