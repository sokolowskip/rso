package balancer;

import java.net.InetAddress;
import java.util.ArrayList;

import CRUD.FileInfo;

/**
 * Klasa przchowuje informacje o plikach
 * do przeniesienia.
 * 
 *
 */
public class MigrateInfo 
{
	//shard z ktorego zabieramy
	private InetAddress from;
	//shard do ktorego przezucamy
	private InetAddress to;
	
	//pliki do przeniesienia
	public ArrayList<FileInfo> documents;
	
	//roznica w obciazeniu
	private long loadDiff;
	
	public MigrateInfo()
	{
		documents = new ArrayList<FileInfo>();
	}
	
	public InetAddress getFrom() {
		return from;
	}
	public void setFrom(InetAddress from) {
		this.from = from;
	}
	public InetAddress getTo() {
		return to;
	}
	public void setTo(InetAddress to) {
		this.to = to;
	}
	public long getLoadDiff() {
		return loadDiff;
	}
	public void setLoadDiff(long loadDiff) {
		this.loadDiff = loadDiff;
	}
	
}
