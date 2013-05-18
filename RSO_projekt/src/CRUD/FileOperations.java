package CRUD;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileOperations {
	

	//TODO: dodaæ sens
	public void findFiles(String directory) {
		File folder = new File(directory);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("File " + listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}
	}
	
	//zwraca listê plików nale¿¹cych do danej kolekcji
	public static File[] openCollection(String collectionName)
	{
		//zak³adam ¿e pliki le¿¹ p³asko w kolekcji, je¿eli oka¿e siê to ma³o wydajne trzeba bêdzie to skomplikowaæ
		File folder = new File(collectionName);
		File[] listOfFiles = folder.listFiles();
		
		return listOfFiles;
	}

	//TODO: dodaæ sens
	public void addToFile() {
		try {
			FileWriter fstream = new FileWriter("out.txt", true); // true tells
																	// to append
																	// data
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("\nsue");
			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
