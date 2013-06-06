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
	//adres serwera konfiguracyjnego
	private InetAddress connServIP;


	//interfejs serwera konfiguracyjnego
	private Rem service;


	public RemClient(InetAddress connServIP) {
		this.connServIP = connServIP;
		try {
			this.shard = new ShardInfo(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			System.err.println("UnknownHostException: " + e);
		}
		Thread client = new Thread(this);
		client.start();
	}

	//inicjalizacja kilenta
	//zarejestrowanie w serwerze konfiguracjnym
	public void run() {
		try {

			Registry r = LocateRegistry.getRegistry(
					connServIP.getHostAddress(), 1099);
			//Tworzymy STUB
			service = (Rem) r.lookup("//" + connServIP.getHostName()
					+ "/Rem");
			System.out.println(service.registerToConfigServer(InetAddress.getLocalHost()));
			update();
		} catch (RemoteException re) {
			System.err.println("RemoteException: " + re);
		} catch (NotBoundException nbe) {
			System.err.println("NotBoundException: " + nbe);
		} catch (UnknownHostException e) {
			System.err.println("UnknownHostException: " + e);
		}
	}

	//funkcja wysyla aktualne inforamcje o shadzie do serwera konfiguracyjnego
	private synchronized void update() {
		while(true)
		{
			//czekamy na zmiany w bazie danych
			//kiedy objekt ShardMonitor wywola notify()
			//to jedziemy dalej
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				connServIP.isReachable(400);
			} catch (IOException e) {
				System.err.println("Config server unreachable: " + e);
			}
			try {
				System.out.println(service.updateShardInfo(shard));
			} catch (RemoteException e) {
				System.err.println("Update unsuccessfull: " + e);
				e.printStackTrace();
			}
		}
	}
}