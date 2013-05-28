package CRUD;

import java.io.File;
import java.util.Iterator;

import messages.DeleteMessage;
import bson.BSONDocument;
import bson.BSONElement;

public class Delete {

	public void deteleEntry(DeleteMessage deleteMessage) {
		// pobieranie danych z deleteMessage
		BSONDocument deleteBsonDocument = deleteMessage.selector;
		Iterator<BSONElement<?>> iterator = deleteBsonDocument.getElems()
				.iterator();

		// przeszukiwanie delete message
		while (iterator.hasNext()) {
			BSONElement<?> deleteElement = iterator.next();
			if (deleteElement.getName() != null) {
				File[] fileArray = FileOperations
						.openCollection(deleteMessage.fullCollectionName);
				for (int i = 0; i < fileArray.length; i++) {
					// przeszukiwanie wszystkich plikow
					compareWithFile( fileArray[i],  deleteElement,  deleteMessage);
				}
			}
		}
	}

	public void compareWithFile(File file, BSONElement<?> deleteElement,
			DeleteMessage deleteMessage) {
		// pobieranie danych z pliku
		BSONDocument fileBsonDocument = FileOperations.readFromFile(file);
		Iterator<BSONElement<?>> iterator2 = fileBsonDocument.getElems()
				.iterator();
		// przeszukiwanie BSON-ow w pliku
		while (iterator2.hasNext()) {
			BSONElement<?> searchElement = iterator2.next();
			// porownanie nazwy z deleteMessage i z pliku
			if (deleteElement.getName().equals(searchElement.getName())) {
				System.out.println("znaleziono dokument z t¹ sam¹ nazw¹ "
						+ searchElement.getName() + " w pliku "
						+ file);
				System.out.println("zawartoœæ: " + searchElement.getData());
				// usuwanie pliku
				FileOperations.deleteFile(file);
			}
			
			// TODO:
			// dodac inne opcje (usuwanie nie tylko po nazwie)
		}
	}
}
