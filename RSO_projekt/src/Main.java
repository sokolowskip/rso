import java.io.IOException;
import java.net.InetAddress;

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
			if (args[0].equals("-start_conf"))
				new ConfigServer();
		}
		if (args.length == 2) {
			//lub podlaczyc sie do istniejacego serwera
			if (args[0].equals("-conf"))
			{
				InetAddress configServer = InetAddress.getByName(args[1]);
				//tworzymy objekt klienta, ktory rozpoczyna nowy watek
				RemClient client = new RemClient(configServer);
				ShardMonitor monitor = new ShardMonitor(client);
			}
			if (args[0].equals("-balancer_mode"))
			{
				InetAddress configServer = InetAddress.getByName(args[1]);
				//tworzymy objekt balancera
				new Balancer(configServer);
			}
		}
		else
			new Listener(27017);
	}


}
