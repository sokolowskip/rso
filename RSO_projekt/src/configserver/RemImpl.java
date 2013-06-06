package configserver;

import java.net.InetAddress;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import balancer.ShardInfo;

/**
 * Klasa implementujace funkcje serwera konfiguracyjnego. Zawiera metody
 * wywolywane zarowno przez shardy, jak i balancer.
 * 
 * @author Piotr Cebulski
 */

public class RemImpl extends UnicastRemoteObject implements Rem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5370129700470960855L;
	// info o bazie
	private HashMap<InetAddress, ShardInfo> shards;
	// blokada na info
	//zeby shardy nic nie nadpisywaly jak balancer pobiera
	private final Lock lock = new ReentrantLock();
	
	private InetAddress balancer;

	public RemImpl() throws RemoteException {
		shards = new HashMap<InetAddress, ShardInfo>();
	}

	public String registerToConfigServer(InetAddress shardIP)
			throws RemoteException {
		try {
			this.lock.lock();
		} finally {
			if (!shards.containsKey(shardIP))
			{
				shards.put(shardIP, new ShardInfo(shardIP));
				this.lock.unlock();
			}
			else
			{
				this.lock.unlock();
				return ("Shard already registered");
			}
		}
		System.out.println("Registered new shard: " + shardIP.getHostAddress());
		return ("Shard registered successfully");
	}

	public String updateShardInfo(ShardInfo shard) throws RemoteException {
		try {
			this.lock.lock();
		} finally {
			shards.put(shard.getShardIP(), shard);
			this.lock.unlock();
		}
		return ("ShardInfo updated");
	}

	public HashMap<InetAddress, ShardInfo> getShards(InetAddress _balancer) {
		HashMap<InetAddress, ShardInfo> currentShards = new HashMap<InetAddress, ShardInfo>();
		try{
			this.lock.lock();
		} finally {
			if (balancer.getHostAddress().equals(balancer.getHostAddress()))
			{
				currentShards = shards;
				this.lock.unlock();
			}
			else
			{
				this.lock.unlock();
				return new HashMap<InetAddress, ShardInfo>();
			}
		}
		return currentShards;
	}

	public String registerBalancer(InetAddress _balancer)
			throws RemoteException {
		balancer = _balancer; 
		System.out.println("Balancer registered: " + _balancer.getHostAddress());
		return ("Balancer registered");
	}
}