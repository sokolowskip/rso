package configserver;

import java.rmi.*; // For Naming, RemoteException, etc.
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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

	private InetAddress connServIP;
	@SuppressWarnings("unused")
	private int port;

	public RemClient(InetAddress connServIP) {
		this.connServIP = connServIP;
		Thread rmi = new Thread(this);
		rmi.start();
	}

	public void run() {
		try {

			Registry r = LocateRegistry.getRegistry(
					connServIP.getHostAddress(), 1099);
			//Tworzymy STUB
			Rem service = (Rem) r.lookup("//" + connServIP.getHostName()
					+ "/Rem");
			System.out.println(service.registerToConfigServer(new ShardInfo(
					InetAddress.getLocalHost())));
		} catch (RemoteException re) {
			System.out.println("RemoteException: " + re);
		} catch (NotBoundException nbe) {
			System.out.println("NotBoundException: " + nbe);
		} catch (UnknownHostException e) {
			System.out.println("UnknownHostException: " + e);
			e.printStackTrace();
		}
	}
}