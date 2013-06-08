import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import CRUD.FileOperations;
import balancer.Balancer;
import balancer.ShardMonitor;

import listener.Listener;

import configserver.ConfigServer;
import configserver.RemClient;

/**
 * Tutaj startujemy baze.
 * 
 *
 */
public class Main {

	/**MAIN**/
	public static void main(String[] args) throws IOException
	{
		if (args.length == 1) {
			//mozemy utworzyc serwer konfiguracyjny
			if (args[0].equals("--start_conf"))
				new ConfigServer();
			else if (args[0].equals("--info"))
			{
				System.out.print(
						"RsoDB\n" +
						"usage: java -jar rso.jar [options]\n" +
						"options:\n" +
						"--start_conf\t\t\t\trun configuration server\n" +
						"--shard [host]\t\t\t\tcreate data base shard\n" +
						"--balancer_mode [host]\t\t\tstart balancer service\n"+
						"--info\t\t\t\t\tshow this usage information\n"
						);
			}
		}
		else if (args.length == 2) {
			//lub podlaczyc sie do istniejacego serwera
			if (args[0].equals("--shard"))
			{
				InetAddress configServer = InetAddress.getByName(args[1]);
				//tworzymy klienta serwera konfiguracyjnego, ktory rozpoczyna nowy watek
				RemClient client = new RemClient(configServer);
				//monitorujemy zmiany w bazie
				new ShardMonitor(client);
				//odpalamy baze
				File theDir = new File(FileOperations.dbDirectory);
				//tworzymy folder z baza w home directory
				if (!theDir.exists())
				{
					System.out.println("creating directory: " + FileOperations.dbDirectory);
					boolean result = theDir.mkdir();  
					if(result){    
						System.out.println("DIR created");  
					}
				}
				new Listener(27017);
			}
			//host bedzie robil za balancer
			if (args[0].equals("--balancer_mode"))
			{
				InetAddress configServer = InetAddress.getByName(args[1]);
				//tworzymy objekt balancera
				new Balancer(configServer);
			}
		}
		else
		{
			//odpalamy baze
			File theDir = new File(FileOperations.dbDirectory);
			//tworzymy folder z baza w home directory
			if (!theDir.exists())
			{
				System.out.println("creating directory: " + FileOperations.dbDirectory);
				boolean result = theDir.mkdir();  
				if(result){    
					System.out.println("DIR created");  
				}
			}
			new Listener(27017);
		}
	}


}
