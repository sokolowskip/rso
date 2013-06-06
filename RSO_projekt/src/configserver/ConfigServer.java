package configserver;

/**
* Klasa tworzy watek serwera konfiguracyjnego.
* Tworzy objekt RemServer korzystajacy z RMI.
* 
* @author Piotr Cebulski
*/
public class ConfigServer implements Runnable
{

	public ConfigServer() 
	{
		System.out.println("Start config server...");
		Thread conn = new Thread(this);
		conn.start();
	}
	
	public void run() 
	{
		new RemServer();
	}
	
}
