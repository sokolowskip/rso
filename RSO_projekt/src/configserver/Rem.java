package configserver;

import java.net.InetAddress;
import java.rmi.*;
import java.util.ArrayList;

import balancer.ShardInfo;

/**
 * The RMI client will use this interface directly. The RMI server will make a
 * real remote object that implements this, then register an instance of it with
 * some URL.
 */

public interface Rem extends Remote {
	
	public String registerToConfigServer(InetAddress shardIP)
			throws RemoteException;
	
	public ArrayList<ShardInfo> getShards()
			throws RemoteException;

}