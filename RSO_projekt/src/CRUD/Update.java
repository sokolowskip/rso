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
		BSONDocument updateBsonDocument = updateMessage.update;

		// pobieranie nazwy pliku do zmodyfikowania z updateMessage
		// String fileNameToUpdate = FileOperations.findIdElement(bsonDocument);
		List<String> fileNamesToUpdate = Selector.getFilesIDs(
				updateMessage.selector, collectionName);

		for (int i = 0; i < fileNamesToUpdate.size(); i++) {
			// wyszukiwanie pliku na dysku
			File fileToUpdate = FileOperations.findFile(
					fileNamesToUpdate.get(i), collectionName);
			if (fileToUpdate != null) {
				// update
				updateElements(updateBsonDocument, fileToUpdate, collectionName);
			} else {
				System.out.println("Nie znaleziono pliku");
			}
		}
	}

	public void updateElements(BSONDocument updateDocument, File fileToUpdate,
			String collectionName) {
		// pobieranie danych z updateMessage
		Iterator<BSONElement<?>> iterator = updateDocument.getElems()
				.iterator();
		// przeszukiwanie updateMessage
		while (iterator.hasNext()) {
			BSONElement<?> updateElement = iterator.next();
			if (updateElement.getName() != null) {
				compareWithFile(updateElement, fileToUpdate, dbDirectory
						+ collectionName + "/" + fileToUpdate.getName());
			}
		}
	}

	public void compareWithFile(BSONElement<?> updateElement,
			File fileToUpdate, String filePathToUpdate) {
		// pobieranie danych z pliku
		BSONDocument fileBsonDocument = FileOperations
				.readBytesFromFile(filePathToUpdate);
		Iterator<BSONElement<?>> iterator2 = fileBsonDocument.getElems()
				.iterator();
		// przeszukiwanie BSON-ow w pliku

		List<BSONElement<?>> newBsonElementList = new ArrayList<BSONElement<?>>();
		while (iterator2.hasNext()) {
			BSONElement<?> fileElement = iterator2.next();
			// porownanie nazwy z updateMessage i z pliku
			if (updateElement.getName().equals(fileElement.getName())) {
				System.out.println("znaleziono pole do aktualizacji"
						+ updateElement.getName());
				// aktualizacja elementu - "wiersza"
				newBsonElementList.add(updateElement);
			}else{
				newBsonElementList.add(fileElement);
			}
		}
		BSONDocument newBsonDocument = new BSONDocument();
		newBsonDocument.setElems(newBsonElementList);

		fileToUpdate.delete();
		FileOperations.createFile(newBsonDocument, filePathToUpdate);
	}
}
