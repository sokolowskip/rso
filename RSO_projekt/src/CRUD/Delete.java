package CRUD;

import java.io.File;

import bson.BSONDocument;
import messages.DeleteMessage;

public class Delete {
	String dbDirectory = "exampleDB/";

	void deleteDocument(DeleteMessage deleteMessage) {
		String collectionName = deleteMessage.fullCollectionName;
		BSONDocument bsonDocument = deleteMessage.selector;

		//pobieranie nazwy pliku do usuniecia z deleteMessage
		String fileNameToDelete = FileOperations.findIdElement(bsonDocument);
		
		System.out.println(fileNameToDelete);
		System.out.println(collectionName);
		
		//wyszukiwanie pliku na dysku
		File fileToDelete = FileOperations.findFile(fileNameToDelete, collectionName);
		
		if (fileToDelete!=null){
			fileToDelete.delete();
		}
	}
}
