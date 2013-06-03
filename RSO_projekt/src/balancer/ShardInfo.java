package balancer;

import java.net.InetAddress;

public class ShardInfo 
{
	//maksymalny rozmiar sharda
	//jak to bedzie okreslane??
    private static long maxSize;
    //aktualne obciazenie sharda
    private static long currSize;
	
    //adres sharda
    InetAddress shardIP;
    //port na ktorym dziala balancer
    int balancerPort;
    
	Boolean isSizeMaxed() 
	{
		if (maxSize == currSize) return true;
		else return false;
	}
	
	long getMaxSize() 
	{ 
		return maxSize; 
	}
	
	long getCurrSize() 
	{ 
		return currSize; 
	}
}
