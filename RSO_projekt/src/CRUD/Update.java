package CRUD;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import messages.UpdateMessage;
import bson.BSONDocument;
import bson.BSONElement;

public class Update {
	String dbDirectory = "exampleDB/";

	void updateDocument(UpdateMessage updateMessage) {
		String collectionName = updateMessage.fullCollectionName;
		BSONDocument bsonDocument = updateMessage.selector;
		BSONDocument updateBsonDocument = updateMessage.update;

		// pobieranie nazwy pliku do zmodyfikowania z updateMessage
		String fileNameToUpdate = FileOperations.findIdElement(bsonDocument);

		// wyszukiwanie pliku na dysku
		File fileToUpdate = FileOperations.findFile(fileNameToUpdate,
				collectionName);

		if (fileToUpdate != null) {
			// update
			System.out.println(fileNameToUpdate);
			System.out.println(collectionName);
			updateElements(updateBsonDocument, fileToUpdate);

		}
	}

	public void updateElements(BSONDocument updateDocument, File fileToUpdate) {
		// pobieranie danych z updateMessage
		Iterator<BSONElement<?>> iterator = updateDocument.getElems()
				.iterator();

		// przeszukiwanie updateMessage
		while (iterator.hasNext()) {
			BSONElement<?> updateElement = iterator.next();
			if (updateElement.getName() != null) {
				compareWithFile(updateElement, fileToUpdate);
			}
		}
	}

	public void compareWithFile(BSONElement<?> updateElement, File fileToUpdate) {
		// pobieranie danych z pliku
		BSONDocument fileBsonDocument = FileOperations
				.readFromFile(fileToUpdate);
		Iterator<BSONElement<?>> iterator2 = fileBsonDocument.getElems()
				.iterator();
		// przeszukiwanie BSON-ow w pliku
		while (iterator2.hasNext()) {
			BSONElement<?> fileElement = iterator2.next();
			// porownanie nazwy z updateMessage i z pliku
			if (updateElement.getName().equals(fileElement.getName())) {
				System.out.println("znaleziono pole do aktualizacji"
						+ updateElement.getName());
				// aktualizacja elementu - "wiersza"
				fileElement = updateElement;
			}
		}
		// TODO: save fileBsonDocument jako bajty do pliku
	}
}
