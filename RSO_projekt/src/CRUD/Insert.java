package CRUD;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import bson.BSONDocument;

public class Insert {
	String dbDirectory = "exampleDB/";

	// dodanie dokumentu - "wiersza"
	void insertDocument(String collectionName, BSONDocument bsonDocument) {
		// sprawdzenie czy kolekcja - "tabela" ju¿ nie istnieje
		if (checkIfCollection(collectionName)) {
			// dodaj dokument do kolekcji
			dbDirectory = dbDirectory + collectionName;
			File folder = new File(dbDirectory);
			int index = folder.listFiles().length;
			createFile(bsonDocument, dbDirectory + "/" + Integer.toString(index));

		} else {
			// stwórz nowa kolekcje
			System.out.println("nowa kolekcja");
			File dir = new File(dbDirectory + collectionName);
			dir.mkdir();
			dbDirectory = dbDirectory + collectionName;
			createFile(bsonDocument, dbDirectory + "/" + "0");
		}
	}

	private void createFile(BSONDocument bsonDocument, String name) {
		File newRecord = new File(name);
		try {
			if (newRecord.createNewFile()) {
				FileWriter fw = new FileWriter(newRecord.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				//TODO: poprawic jak Marek doda parsowanie bsona do bajtów
				bw.write(bsonDocument.toString());
				bw.close();

			} else {
				System.out.println("File already exists.");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private boolean checkIfCollection(String collectionName) {
		// sprawdzenie czy nie istnieje folder

		File folder = new File(dbDirectory);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isDirectory()) {
				if (listOfFiles[i].getName().equals(collectionName)) {
					// System.out.println(collectionName);
					return true;
				}
			}
		}
		return false;
	}
}
