package balancer;

import java.net.InetAddress;

public class MigrateInfo 
{
	private InetAddress from;
	private InetAddress to;
	
	private long loadDiff;
	
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
