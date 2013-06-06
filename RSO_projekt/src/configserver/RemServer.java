package configserver;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Klasa sluzy do uruchamiani rejestru RMI na domyslnym porcie.
 * Tworzy obiekt RemImpl.
 * 
 * @author Piotr Cebulski
 */

public class RemServer {

	public RemServer() {
         try { //special exception handler for registry creation
            Registry r = LocateRegistry.createRegistry(1099);
            //Tworzymy SKELTON
            r.rebind("//localhost/Rem", new RemImpl());
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            //do nothing, error means registry already exists
            System.err.println("java RMI registry already exists.");
        }
	}
}