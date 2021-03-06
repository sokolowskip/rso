package CRUD;

import java.io.File;
import java.util.List;

import messages.DeleteMessage;

public class Delete {
	static String dbDirectory = FileOperations.dbDirectory;

	public void deleteDocument(DeleteMessage deleteMessage) {
		String collectionName = deleteMessage.fullCollectionName;

		// pobieranie nazwy pliku do usuniecia z deleteMessage
		List<String> fileNamesToDelete = Selector
				.searchFileNames(deleteMessage.selector, collectionName);

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
