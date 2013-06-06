package configserver;

import java.net.InetAddress;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
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
	private HashMap<InetAddress, ShardInfo> shards;
	
	public RemImpl() throws RemoteException {
		shards = new HashMap<InetAddress, ShardInfo>();
	}

	
	public String registerToConfigServer(InetAddress shardIP)
			throws RemoteException {
		shards.put(shardIP, new ShardInfo(shardIP));
		System.out.println("Registered new shard: " + shardIP.getHostAddress());
		return("Shard registered successfully");
	}

	public String updateShardInfo(ShardInfo shard)
		throws RemoteException {
		shards.put(shard.getShardIP(), shard);
		return("ShardInfo updated");
	}
	
	public HashMap<InetAddress, ShardInfo> getShards() {
		return shards;
	}
}