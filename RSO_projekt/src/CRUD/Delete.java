package CRUD;

import java.io.File;
import java.util.List;

import messages.DeleteMessage;

public class Delete {
	String dbDirectory = "exampleDB/";

	void deleteDocument(DeleteMessage deleteMessage) {
		String collectionName = deleteMessage.fullCollectionName;
		// BSONDocument bsonDocument = deleteMessage.selector;

		// pobieranie nazwy pliku do usuniecia z deleteMessage
		// String fileNameToDelete = FileOperations.findIdElement(bsonDocument);
		List<String> fileNamesToDelete = Selector
				.getFilesIDs(deleteMessage.selector, collectionName);

		for (int i = 0; i < fileNamesToDelete.size(); i++) {
			// wyszukiwanie pliku na dysku
			File fileToDelete = FileOperations.findFile(fileNamesToDelete.get(i),
					collectionName);

			if (fileToDelete != null) {
				fileToDelete.delete();
			}
		}
	}
}
