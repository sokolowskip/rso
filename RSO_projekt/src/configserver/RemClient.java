package configserver;

import java.rmi.*; 
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Enumeration;
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
	//adres z ktorego sie laczymy
	private InetAddress clientIP;

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
		//trzeba ogarnac jakiego interfejsu uzyc
	    try {
	        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
	        while (interfaces.hasMoreElements()) {
	            NetworkInterface iface = interfaces.nextElement();
	            //sprawdzamy ktory NIC jest uruchomiony
	            if (iface.isLoopback() || !iface.isUp())
	                continue;
	            Enumeration<InetAddress> addresses = iface.getInetAddresses();
	            while(addresses.hasMoreElements()) {
	                InetAddress addr = addresses.nextElement();
	                //pomijamy adresy IPv6
	                if (addr instanceof Inet6Address) continue;
	                clientIP = addr;
	                System.out.println("ConfigServer client: " + iface.getDisplayName() + " " + clientIP);
	            }
	        }
	    } catch (SocketException e) {
	        throw new RuntimeException(e);
	    }
		try {
			Registry r = LocateRegistry.getRegistry(
					connServIP.getHostAddress(), 1099);
			//Tworzymy STUB
			service = (Rem) r.lookup("//" + connServIP.getHostAddress()
					+ "/Rem");
			System.out.println(service.registerToConfigServer(clientIP));
			update();
		} catch (RemoteException re) {
			System.err.println("RemoteException: " + re);
		} catch (NotBoundException nbe) {
			System.err.println("NotBoundException: " + nbe);
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