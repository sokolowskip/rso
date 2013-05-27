package CRUD;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import bson.BSONDocument;

public class FileOperations {
	String directory = "C:/Users/Tomek/Desktop/test/";

	// TODO: dodaæ sens
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

	// zwraca listê plików nale¿¹cych do danej kolekcji
	public static File[] openCollection(String collectionName) {
		// zak³adam ¿e pliki le¿¹ p³asko w kolekcji, je¿eli oka¿e siê to ma³o
		// wydajne trzeba bêdzie to skomplikowaæ
		File folder = new File(collectionName);
		File[] listOfFiles = folder.listFiles();

		return listOfFiles;
	}

	public void addToFile(String fileName, BSONDocument doc) {
		try {
			FileWriter fstream = new FileWriter(directory + fileName, true); // true tells
																	// to append
																	// data
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(doc.toString());
			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	// public void removeFile(String fileName) {
	// for(int i= 0; i<openCollection(collectionName); )
	// }
}
