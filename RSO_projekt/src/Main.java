import java.io.IOException;
import java.net.InetAddress;

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
		if (args.length == 3) {
			//mozemy utworzyc serwer konfiguracyjny
		if (args[0].equals("-start_conf"))
		{
			int port = 0;
			if (args[1].equals("-port"))
				port = Integer.parseInt(args[2]);
			new ConfigServer(port);
		}
		}
		if (args.length == 4) {
			//lub podlaczyc sie do istniejacego serwera
		if (args[0].equals("-conf"))
		{
			InetAddress configServer = InetAddress.getByName(args[1]);
			@SuppressWarnings("unused")
			int port = 0;
			if (args[2].equals("-port"))
				port = Integer.parseInt(args[3]);
			new RemClient(configServer);
		}
		}
		new Listener(27017);
		
	}

}
