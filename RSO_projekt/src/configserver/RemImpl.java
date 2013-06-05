package configserver;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import balancer.ShardInfo;

/**
 * Klasa implementujace funkcje serwera konfiguracyjnego.
 * Zawiera metody wywolywane zarowno przez shardy, jak i balancer.
 * 
 * @author Piotr Cebulski
 */

public class RemImpl extends UnicastRemoteObject implements Rem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5370129700470960855L;
	private ArrayList<ShardInfo> shards;
	
	public RemImpl() throws RemoteException {
		shards = new ArrayList<ShardInfo>();
	}

	
	public String registerToConfigServer(ShardInfo shard)
			throws RemoteException {
		shards.add(shard);
		System.out.println("Registered new shard: " + shard.getShardIP().getHostAddress());
		return("Shard registered successfully");
	}

	public ArrayList<ShardInfo> getShards() {
		return shards;
	}
}