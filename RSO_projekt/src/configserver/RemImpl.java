package configserver;

import java.net.InetAddress;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import balancer.ShardInfo;

/**
 * This is the actual implementation of Rem that the RMI server uses. The server
 * builds an instance of this then registers it with a URL. The client accesses
 * the URL and binds the result to a Rem (not a RemImpl; it doesn't have this).
 */

public class RemImpl extends UnicastRemoteObject implements Rem {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static ArrayList<ShardInfo> shards;
	
	public RemImpl() throws RemoteException {
	}

	public String registerToConfigServer(InetAddress shardIP)
			throws RemoteException {
		shards.add(new ShardInfo(shardIP));
		System.out.println("Registering new shard: " + shardIP.getHostAddress());
		return("Shard registered successfully");
	}

	public ArrayList<ShardInfo> getShards() {
		return shards;
	}
}