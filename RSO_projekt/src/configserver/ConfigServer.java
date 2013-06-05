package configserver;

/**
* Klasa tworzy watek serwera konfiguracyjnego.
* Tworzy objekt RemServer korzystajacy z RMI.
* 
* @author Piotr Cebulski
*/
public class ConfigServer implements Runnable
{

	@SuppressWarnings("unused")
	private int port;

	public ConfigServer(int _port) 
	{
		System.out.println("Start config server...");
		port = _port;
		Thread conn = new Thread(this);
		conn.start();
	}
	
	public void run() 
	{
		new RemServer();
	}
	
}
