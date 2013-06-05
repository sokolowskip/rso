package CRUD;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import messages.InsertMessage;

import bson.BSONDocument;
import bson.ObjectID;

public class Insert {
	String dbDirectory = "exampleDB/";

	void insertDocumentList(InsertMessage insertMessage) {
		String collectionName = insertMessage.fullCollectionName;
		BSONDocument[] bsonDocumentList = insertMessage.documents;
		for (int i = 0; i < bsonDocumentList.length; i++) {
			insertDocument(collectionName, bsonDocumentList[i]);
		}
	}

	// dodanie dokumentu - "wiersza"
	void insertDocument(String collectionName, BSONDocument bsonDocument) {
		// sprawdzenie czy kolekcja - "tabela" ju¿ nie istnieje
		if (checkIfCollection(collectionName)) {
			// dodaj dokument do kolekcji
			dbDirectory = dbDirectory + collectionName;
			String fileName = findIdElement(bsonDocument);
			createFile(bsonDocument, dbDirectory + "/" + fileName);

		} else {
			// stwórz nowa kolekcje
			System.out.println("nowa kolekcja");
			File dir = new File(dbDirectory + collectionName);
			dir.mkdir();
			dbDirectory = dbDirectory + collectionName;
			String fileName = findIdElement(bsonDocument);
			createFile(bsonDocument, dbDirectory + "/" + fileName);
		}
	}

	// wyszukiwanie elementu zawieraj¹cego pole "_id"
	String findIdElement(BSONDocument bsonDocument) {
		String fileName = null;
		for (int i = 0; i < bsonDocument.getElems().size(); i++) {
			if (bsonDocument.getElems().get(i).getName().equals("_id")) {
				ObjectID objectID = (ObjectID) bsonDocument.getElems().get(i)
						.getData();
				if (objectID != null) {
					fileName = Integer.toString(objectID.getCounter())
							+ Integer.toString(objectID.getMachine())
							+ Integer.toString(objectID.getProcID())
							+ Integer.toString(objectID.getTime());
					// System.out.println(fileName);
				}
				return fileName;
			}
		}
		return fileName;
	}

	private void createFile(BSONDocument bsonDocument, String name) {
		File newRecord = new File(name);
		try {
			if (newRecord.createNewFile()) {
				FileWriter fw = new FileWriter(newRecord.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				// TODO: poprawic jak Marek doda parsowanie bsona do bajtów
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
