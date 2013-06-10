package balancer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import CRUD.FileInfo;

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

	private InetAddress balancerIP;

	public Balancer(InetAddress configServer) {
		shards = new HashMap<InetAddress, ShardInfo>();
		this.configServer = configServer;
		Thread client = new Thread(this);
		client.start();
	}

	
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
                balancerIP = addr;
                System.out.println("Balancer client: " + iface.getDisplayName() + " " + balancerIP);
            }
        }
	    } catch (SocketException e) {
	        throw new RuntimeException(e);
	    }
		try {
			Registry r = LocateRegistry.getRegistry(
					configServer.getHostAddress(), 1099);
			//Tworzymy STUB
			service = (Rem) r.lookup("//" + configServer.getHostAddress() + "/Rem");
			System.out.println(service.registerBalancer(balancerIP));
			doBalanceRound();
		} catch (RemoteException re) {
			System.err.println("RemoteException: " + re);
		} catch (NotBoundException nbe) {
			System.err.println("NotBoundException: " + nbe);
		}
	}

	//runda balancera
	void doBalanceRound() 
	{
		while(true)
		{
			try {
				shards.putAll(service.getShards(balancerIP));
				System.out.println("Shard info acquired");
				//jak dostaniemy info o shardach to sprawdzamy polityke balancera
				policy = new BalancerPolicy(shards);
				//jesli jest nierowno to patrzymy co mozna przeniesc
				if (policy.getMigrate().getFrom() != null)
				{
					System.out.println("From: " + policy.getMigrate().getFrom());
					System.out.println("To: " + policy.getMigrate().getTo());
					System.out.println("Load diff: " + policy.getMigrate().getLoadDiff());
					policy.findCandidates();
					ArrayList<FileInfo> files = policy.getMigrate().documents;
					for (FileInfo fileInfo : files) {
						System.out.println(fileInfo.name +"\t\t"+ fileInfo.size);
					}
					moveData();
				}
			} catch (RemoteException e) {
				System.err.println("Unable to fetch shard info: " + e);
			}
			try {
				//balansujemy co 5s
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				 System.err.println("InterruptedException: " + e);
			}
		}
	}
	
	//przeniesienie danych
	//laczymy sie do odpowiedniego sharda na port mongo (27017)
	//i wysylamy mu MigrateInfo
	void moveData()  
	{
		MigrateInfo migr = policy.getMigrate();
		Socket  sock = null;
		PrintWriter out = null;
		//otwieramy socketa
		try {
			sock = new Socket(migr.getFrom(), 28017);
			out = new PrintWriter(sock.getOutputStream(), true);
		} catch (UnknownHostException e) {
            System.err.println("Cannot connect to shard: " + migr.getFrom().getHostAddress());
        } catch (IOException e) {
            System.err.println("IOExeption: " + e);
        }
		
		out.println(migr.getTo().getHostAddress());
		int i = 0;
		while(i  < migr.documents.size())
		{
			out.println(migr.documents.get(i).name);
			i++;
		}
		out.println("Bye.");
		
		out.close();
		try {
			sock.close();
		} catch (IOException e) {
			System.err.println("IOExeption: " + e);
			e.printStackTrace();
		}
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
