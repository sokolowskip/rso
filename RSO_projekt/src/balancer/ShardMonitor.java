package balancer;

import java.io.File;
import java.util.ArrayList;
import CRUD.FileInfo;
import CRUD.FileOperations;
import configserver.RemClient;

/**
 * Klasa monitorujaca zmiany w bazie.
 * Powiadamia klienta serwera konfiguracyjnego o zmianach.
 * 
 * @author Piotr Cebuslki
 *
 */
public class ShardMonitor implements Runnable {

	//klient serwera konfiguracyjnego
	RemClient rem;
	
	public ShardMonitor(RemClient rem)
	{
		this.rem = rem;
		Thread conn = new Thread(this);
		conn.start();
	}

	public synchronized void run() {
		while (true){
			try {
				//monitorujemy zmiany co 5s 	:-D
				Thread.sleep(5000);
				//no to teraz trzeba jakos ogarnac co siedzi w bazie
				ArrayList<FileInfo> documents = new ArrayList<FileInfo>();
				long load = 0;
				//to cos powinno zwrocic wszystkie kolekcje
				File collections[] = FileOperations.openCollection("/home/gibbon/exampleDB/");
				//no dobra to tera lecimy po wszystkich kolekcjach
				for (File file : collections) {
					//pobieramy liste plikow
					File docsInCollection[] = FileOperations.openCollection(file.getAbsolutePath());
					for (File file2 : docsInCollection) {
						FileInfo info = new FileInfo(file2);
						//robimy plaska liste plikow we wszystkich kolekcjach
						documents.add(info);
						load += info.size;						
					}
				}
				//aktualizujemy info
				rem.shard.documents = documents;
				rem.shard.setCurrSize(load);
			} catch (InterruptedException e) {
				System.err.println("ShardMonitor interrupted: " + e);
			}
			//i budzimy klienta serwera konfiguracyjnego
			synchronized (rem)
			{
				rem.notify();
			}
		}
	}
}
