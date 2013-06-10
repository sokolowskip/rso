package CRUD;

import java.io.File;

import messages.InsertMessage;
import bson.BSONDocument;
import CRUD.FileOperations;

public class Insert {
	static String dbDirectory = FileOperations.dbDirectory;

	public void insertDocumentList(InsertMessage insertMessage) {
		System.out.println("jestem w funkcji INSERT");
		String collectionName = insertMessage.fullCollectionName;
		System.out.println("nazwa kolekcji: " + collectionName);
		BSONDocument[] bsonDocumentList = insertMessage.documents;
		System.out.println("bson documentList length: " + bsonDocumentList.length);
		for (int i = 0; i < bsonDocumentList.length; i++) {
			insertDocument(collectionName, bsonDocumentList[i]);
		}
	}

	// dodanie dokumentu - "wiersza"
	void insertDocument(String collectionName, BSONDocument bsonDocument) {
		System.out.println("robie INSERT-a w dokumencie");
		// sprawdzenie czy kolekcja - "tabela" ju� nie istnieje
		if (FileOperations.checkIfCollection(collectionName)) {
			// dodaj dokument do kolekcji
			String fileName = FileOperations.findIdElement(bsonDocument);
			FileOperations.createFile(bsonDocument, dbDirectory + collectionName + "/" + fileName);

		} else {
			// stw�rz nowa kolekcje
			System.out.println("nowa kolekcja");
			File dir = new File(dbDirectory + collectionName);
			dir.mkdir();
			String fileName = FileOperations.findIdElement(bsonDocument);
			FileOperations.createFile(bsonDocument, dbDirectory + collectionName + "/" + fileName);
		}
	}
}
