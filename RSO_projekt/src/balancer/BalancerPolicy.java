package balancer;

import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;

/**
 * Klasa sluzaca do sprawdzania czy shardy sa 
 * rownomiernie obciazone i jesli nie, to gdzie co
 * nalezy przesunac.
 * 
 * @author Piotr Cebulski
 *
 */
public class BalancerPolicy 
{

	private MigrateInfo migrate;
	
	//roznica MB od ktorej zaczynamy balansowac
	private int threshold;
	
	public BalancerPolicy(HashMap<InetAddress, ShardInfo> shards) {
		migrate = new MigrateInfo();
		calculatePolicy(shards);
	}

	private void calculatePolicy(HashMap<InetAddress, ShardInfo> shards) {
		InetAddress maxLoad = null, minLoad = null;
		long max = 0, min = Long.MAX_VALUE;
		Collection<ShardInfo> shardIter = shards.values();
		//sprawdzamy obciazenie shardow
		for (ShardInfo shard : shardIter) {
			//zapisujemy ten o najmniejszym obciazeniu
			if (shard.getCurrSize() < min)
			{
				min = shard.getCurrSize();
				minLoad = shard.getShardIP();
			}
			//zapisujemy ten o najwiekszym obciazeniu
			if (shard.getCurrSize() > max)
			{
				max = shard.getCurrSize();
				maxLoad = shard.getShardIP();
			}
		}
		//sprawdzamy czy roznica jest wieksza niz threshold
		if (max - min > threshold)
		{
			migrate.setFrom(maxLoad);
			migrate.setTo(minLoad);
			migrate.setLoadDiff(max - min);
		}
	}

	public MigrateInfo getMigrate() {
		return migrate;
	}

}
