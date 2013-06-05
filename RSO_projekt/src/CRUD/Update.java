package CRUD;

import java.io.File;
import java.util.Iterator;

import messages.UpdateMessage;
import bson.BSONDocument;
import bson.BSONElement;

public class Update {
	public void updateEntry(UpdateMessage updateMessage) {
		// pobieranie danych z updateMessage
		BSONDocument updateBsonDocument = updateMessage.selector;
		Iterator<BSONElement<?>> iterator = updateBsonDocument.getElems()
				.iterator();

		// przeszukiwanie update message
		while (iterator.hasNext()) {
			BSONElement<?> updateElement = iterator.next();
			if (updateElement.getName() != null) {
				File[] fileArray = FileOperations
						.openCollection(updateMessage.fullCollectionName);
				for (int i = 0; i < fileArray.length; i++) {
					// przeszukiwanie wszystkich plikow
					compareWithFile(fileArray[i], updateElement, updateMessage);
				}
			}
		}
	}

	public void compareWithFile(File file, BSONElement<?> updateElement,
			UpdateMessage updateMessage) {
		// pobieranie danych z pliku
		BSONDocument fileBsonDocument = FileOperations.readFromFile(file);
		Iterator<BSONElement<?>> iterator2 = fileBsonDocument.getElems()
				.iterator();
		// przeszukiwanie BSON-ow w pliku
		while (iterator2.hasNext()) {
			BSONElement<?> searchElement = iterator2.next();
			// porownanie nazwy z updateMessage i z pliku
			if (updateElement.getName().equals(searchElement.getName())) {
				System.out.println("znaleziono dokument z t¹ sam¹ nazw¹ "
						+ searchElement.getName() + " w pliku " + file);
				System.out.println("zawartoœæ: " + searchElement.getData());
				// update pliku
				int age = 31;
//				FileOperations.updateFile(file, "mkyong", age);
			}

			// TODO:
			// dodac inne opcje (aktualizacja nie tylko po nazwie)
			// case double
			// case String
			// case BSONDocument
			// case array
			// case IbjectId
			// case Boolean
			// case Long
			// case null
			// case Integer
		}
	}
}
