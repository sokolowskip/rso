package balancer;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import CRUD.FileInfo;

/**
 * Klasa przechowujaca informacje o shardzie.
 * Implementuje java.io.Serializable, poniewaz
 * jest argumentem w wywolaniach RMI.
 * 
 * @author Piotr Cebulski
 *
 */
public class ShardInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2781342700477736396L;
	//maksymalny rozmiar sharda
	//jak to bedzie okreslane??
    private long maxSize;
    //aktualne obciazenie sharda
    private long currSize;


	public ArrayList<FileInfo> documents;
	
    //adres sharda
    private InetAddress shardIP;

	//port na ktorym dziala balancer
    int balancerPort;
    
    public ShardInfo(InetAddress shardIP)
    {
    	this.shardIP = shardIP;
    	documents = new ArrayList<FileInfo>();
    }
    
    
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

    
    public void setCurrSize(long currSize) {
		this.currSize = currSize;
	}
    
	public InetAddress getShardIP() {
		return shardIP;
	}
}
