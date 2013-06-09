package balancer;

import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;

import CRUD.FileInfo;

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
	
	HashMap<InetAddress, ShardInfo> shards;
	
	//roznica MB od ktorej zaczynamy balansowac
	private int threshold;
	
	public BalancerPolicy(HashMap<InetAddress, ShardInfo> shards) {
		migrate = new MigrateInfo();
		this.shards = shards;
		calculatePolicy();
	}

	//funkcja znajduje shard o najmniejszym i najwiekszym obciazeniu
	//i ustala czy nalezy rownowazyc baze
	private void calculatePolicy() {
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

	//funkcja znajduje dokumenty ktore najlepiej przeniesc
	//z sharda o najwiekszym obciazeniu
	public void findCandidates() {
		ShardInfo fromShard = shards.get(migrate.getFrom());
		//obliczamy srednia wielkosc pliku w shardzie
		long avgLength = (fromShard.getCurrSize() / fromShard.documents.size());
		//suma dlugosci wybranych kandydatow
		long candidates = 0;
		//wybieramy kandydatow dopoki ich dlugosc bedzie
		//mniejsza niz polowa roznicy obciazenia
		while (candidates < migrate.getLoadDiff() / 2)
		{
			FileInfo currCandidate = new FileInfo(fromShard.documents.get(0));
			int cand = 0;
			//przegladamy dokumenty
			for (int i = 1; i < fromShard.documents.size(); i++) {
				//wybieramy najbardziej zblizonego do sredniej dlugosci
				if (Math.abs(fromShard.documents.get(i).size - avgLength)
						< Math.abs(currCandidate.size - avgLength))
				{
					currCandidate = fromShard.documents.get(i);
					cand = i;
				}
			}
			candidates += currCandidate.size;
			//przenosimy kandydata do migrate
			migrate.documents.add(currCandidate);
			//i usuwamy go z listy pozostalych
			fromShard.documents.remove(cand);
		}
	}
	
	public MigrateInfo getMigrate() {
		return migrate;
	}


}
