package balancer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;

import configserver.Rem;

public class Balancer implements Runnable
{
	//adres serwera konfiguracyjnego
	private InetAddress configServer;
	
	// info o bazie
	private HashMap<InetAddress, ShardInfo> shards;
	
	//dane do przeniesienia
	MigrateInfo candidateChunk;
	
	BalancerPolicy policy;
	
	//interfejs serwera konfiguracyjnego
	private Rem service;


	public Balancer(InetAddress configServer) {
		shards = new HashMap<InetAddress, ShardInfo>();
		this.configServer = configServer;
		Thread client = new Thread(this);
		client.start();
	}

	
	public void run() {
		try {

			Registry r = LocateRegistry.getRegistry(
					configServer.getHostAddress(), 1099);
			//Tworzymy STUB
			service = (Rem) r.lookup("//" + configServer.getHostName()
					+ "/Rem");
			System.out.println(service.registerBalancer(InetAddress.getLocalHost()));
			doBalanceRound();
		} catch (RemoteException re) {
			System.err.println("RemoteException: " + re);
		} catch (NotBoundException nbe) {
			System.err.println("NotBoundException: " + nbe);
		} catch (UnknownHostException e) {
			System.err.println("UnknownHostException: " + e);
		}
	}

	//runda balancera
	void doBalanceRound() 
	{
		try {
			shards = service.getShards(configServer);
		} catch (RemoteException e) {
			System.err.println("Unable to fetch shard info: " + e);
		}
	}
	
	//przeniesienie danych
	void moveData(MigrateInfo candidateChunks) 
	{
	}
	
	//sprawdza dostepnosc serwera
	Boolean ping() 
	{
		try {
			InetAddress.getByAddress(configServer.getAddress()).isReachable(400);
			return true;
		} catch (UnknownHostException e) {
			System.err.println("Config Server: " + configServer.getHostAddress() + " is unreachable.");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			System.err.println("Config Server: " + configServer.getHostAddress() + " is unreachable.");
			e.printStackTrace();
			return false;
		}
	}
	
	//pobiera liste pozostalych shardow
	Boolean checkOthers() 
	{
		return null;
	}
}
