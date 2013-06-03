package balancer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Balancer 
{
	//adres serwera konfiguracyjnego
	private InetAddress configServer;
	
	public Balancer(InetAddress configServer) 
	{
		this.configServer = configServer;
	}
	
	//dane do przeniesienia
	MigrateInfo candidateChunk;
	
	BalancerPolicy policy;
	
	//inicjalizacja sharda (rejestracja w configServer)
	Boolean init() 
	{
		return null;
	}

	//runda balancera
	void doBalanceRound() 
	{
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
