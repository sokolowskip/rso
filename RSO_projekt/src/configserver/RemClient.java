package configserver;

import java.rmi.*; 
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.IOException;
import java.net.*;
import java.net.UnknownHostException;

import balancer.ShardInfo;

/**
 * Watek klienta serwera konfiguracyjnego. 
 * Stworzenie objektu RemClient i wywolanie
 * registerToConfigServer() jest rownowazne 
 * dolaczeniu shardu do rozproszonej bazy.
 * 
 * @author Piotr Cebulski
 */

public class RemClient implements Runnable {

	//objekt ShardInfo przechowujacy inforamcje o tym shardzie
	public ShardInfo shard;
	
	//flaga oznaczajaca aktualizacje stanu bazy
	public boolean infoUpdated;
	
	private InetAddress connServIP;
	@SuppressWarnings("unused")
	private int port;


	public RemClient(InetAddress connServIP) {
		this.connServIP = connServIP;
		this.shard = new ShardInfo(connServIP);
		Thread client = new Thread(this);
		client.start();
	}

	public void run() {
		try {

			Registry r = LocateRegistry.getRegistry(
					connServIP.getHostAddress(), 1099);
			//Tworzymy STUB
			Rem service = (Rem) r.lookup("//" + connServIP.getHostName()
					+ "/Rem");
			System.out.println(service.registerToConfigServer(InetAddress.getLocalHost()));
			while(true)
			{
				try {
					connServIP.isReachable(400);
				} catch (IOException e) {
					System.out.println("Config server unreachable: " + e);
				}
				if (infoUpdated)
				{
					System.out.println(service.updateShardInfo(shard));
					infoUpdated = false;
				}
				//czekamy na zmiany w bazie danych
				Thread.sleep(5000);
			}
		} catch (RemoteException re) {
			System.out.println("RemoteException: " + re);
		} catch (NotBoundException nbe) {
			System.out.println("NotBoundException: " + nbe);
		} catch (UnknownHostException e) {
			System.out.println("UnknownHostException: " + e);
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}