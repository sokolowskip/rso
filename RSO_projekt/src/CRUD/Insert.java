package CRUD;

import java.io.File;

import messages.InsertMessage;
import bson.BSONDocument;
import CRUD.FileOperations;

public class Insert {
	String dbDirectory = "exampleDB/";

	public void insertDocumentList(InsertMessage insertMessage) {
		String collectionName = insertMessage.fullCollectionName;
		BSONDocument[] bsonDocumentList = insertMessage.documents;
		for (int i = 0; i < bsonDocumentList.length; i++) {
			insertDocument(collectionName, bsonDocumentList[i]);
		}
	}

	// dodanie dokumentu - "wiersza"
	void insertDocument(String collectionName, BSONDocument bsonDocument) {
		// sprawdzenie czy kolekcja - "tabela" ju¿ nie istnieje
		if (FileOperations.checkIfCollection(collectionName)) {
			// dodaj dokument do kolekcji
			dbDirectory = dbDirectory + collectionName;
			String fileName = FileOperations.findIdElement(bsonDocument);
			FileOperations.createFile(bsonDocument, dbDirectory + "/" + fileName);

		} else {
			// stwórz nowa kolekcje
			System.out.println("nowa kolekcja");
			File dir = new File(dbDirectory + collectionName);
			dir.mkdir();
			dbDirectory = dbDirectory + collectionName;
			String fileName = FileOperations.findIdElement(bsonDocument);
			FileOperations.createFile(bsonDocument, dbDirectory + "/" + fileName);
		}
	}
}
