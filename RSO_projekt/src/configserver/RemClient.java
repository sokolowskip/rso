package configserver;

import java.rmi.*; // For Naming, RemoteException, etc.
import java.net.*; // For MalformedURLException
import java.net.UnknownHostException;

/**
 * Get a Rem object from the specified remote host. Use its methods as though it
 * were a local object.
 * 
 * @see Rem
 */

public class RemClient implements Runnable {

	private InetAddress host;
	private int port;

	public RemClient(InetAddress _host) {
		host = _host;
		Thread rmi = new Thread(this);
		rmi.start();
	}

	public void run() {
		try {
			// Get remote object and store it in remObject:
			Rem remObject = (Rem) Naming.lookup("//" + host.getHostName() + "/Rem");
			// Call methods in remObject:
			System.out.println("Connecting to config server...");
			System.out.println(remObject.registerToConfigServer(InetAddress.getLocalHost()));
		} catch (RemoteException re) {
			System.out.println("RemoteException: " + re);
		} catch (NotBoundException nbe) {
			System.out.println("NotBoundException: " + nbe);
		} catch (MalformedURLException mfe) {
			System.out.println("MalformedURLException: " + mfe);
		} catch (UnknownHostException e) {
			System.out.println("UnknownHostException: " + e);
			e.printStackTrace();
		}
	}
}