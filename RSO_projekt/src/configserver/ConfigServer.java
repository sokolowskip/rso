package configserver;
/**
* Klasa implementujaca serwer konfiguracyjny 
* Korzysta z Remote Method Invocation
*/
public class ConfigServer implements Runnable
{

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
