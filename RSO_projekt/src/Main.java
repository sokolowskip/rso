import java.io.IOException;
import java.net.InetAddress;

import configserver.ConfigServer;

import listener.Listener;

/**
 * Tutaj startujemy baze.
 * 
 *
 */
public class Main {

	/**MAIN**/
	public static void main(String[] args) throws IOException
	{
		if (args.length == 3) {
		if (args[0].equals("-start_conf"))
		{
			int port = 0;
			if (args[1].equals("-port"))
				port = Integer.parseInt(args[2]);
			System.out.println(args[2]);
			new ConfigServer(port);
		}
		}
		if (args.length == 4) {
		if (args[0].equals("-conf"))
		{
			InetAddress configServer = InetAddress.getByName(args[1]);
			int port = 0;
			if (args[2].equals("-port"))
				port = Integer.getInteger(args[3]);
			registerToConfigServer(configServer, port);
		}
		}
		new Listener(27017);
		
	}

	private static void registerToConfigServer(InetAddress configServer,
			Integer port) {
		System.out.println("Connecting to config server...");
		
	}
}
