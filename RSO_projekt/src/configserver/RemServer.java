package configserver;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.net.*;

/**
 * The server creates a RemImpl (which implements the Rem interface), then
 * registers it with the URL Rem, where clients can access it.
 */

public class RemServer {

	public RemServer() {
         try { //special exception handler for registry creation
            
        	LocateRegistry.createRegistry(1099); 
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            //do nothing, error means registry already exists
            System.out.println("java RMI registry already exists.");
        }
		try {
			RemImpl localObject = new RemImpl();
			Naming.rebind("//localhost/Rem", localObject);
		} catch (RemoteException re) {
			System.out.println("RemoteException: " + re);
		} catch (MalformedURLException mfe) {
			System.out.println("MalformedURLException: " + mfe);
		}
	}
}