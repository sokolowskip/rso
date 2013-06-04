package configserver;

import java.io.IOException;
import java.net.ServerSocket;

import balancer.ShardInfo;

public class ConfigServer implements Runnable
{

	ShardInfo shards[];
	private int port;
	
	public void run() 
	{
		try 
		{
		    ServerSocket serverSocket = new ServerSocket(port);
		} 
		catch (IOException e) 
		{
		    System.out.println("Could not listen on port: " + port);
		    System.exit(-1);
		}
	}
	
	public ConfigServer(int _port) 
	{
		System.out.println("Start config server...");		
		port = _port;
		Thread conn = new Thread(this);
		conn.start();
	}
	
	
}
