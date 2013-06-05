package configserver;

import java.rmi.*;
import java.util.ArrayList;

import balancer.ShardInfo;

/**
 * Interfejs serwera konfiguracyjnego.
 * 
 * @author Piotr Cebulski
 * 
 */

public interface Rem extends Remote {
	
	public String registerToConfigServer(ShardInfo shard)
			throws RemoteException;
	
	public ArrayList<ShardInfo> getShards()
			throws RemoteException;

}