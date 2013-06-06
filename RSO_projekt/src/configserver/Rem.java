package configserver;

import java.net.InetAddress;
import java.rmi.*;
import java.util.HashMap;
import balancer.ShardInfo;

/**
 * Interfejs serwera konfiguracyjnego.
 * 
 * @author Piotr Cebulski
 * 
 */

public interface Rem extends Remote {
	
	public String registerToConfigServer(InetAddress shard)
			throws RemoteException;
	
	public String updateShardInfo(ShardInfo shard)
			throws RemoteException;
	
	public HashMap<InetAddress, ShardInfo> getShards(InetAddress balancer)
			throws RemoteException;

	public String registerBalancer(InetAddress balancer)
			throws RemoteException;

}