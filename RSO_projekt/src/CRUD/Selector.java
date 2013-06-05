package CRUD;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bson.BSONDocument;
import bson.BSONElement;

public class Selector {
	String directory = "testoweBsony/";

	public List<File> getFiles(BSONDocument selector) {
		// pobieranie danych z deleteMessage
		BSONDocument searchBsonDocument = selector;
		List<File> finalFileList = new ArrayList<File>();
		Iterator<BSONElement<?>> iterator = searchBsonDocument.getElems()
				.iterator();

		// przeszukiwanie delete message
		while (iterator.hasNext()) {
			BSONElement<?> searchElement = iterator.next();
			if (searchElement.getName() != null) {
				File[] fileArray = FileOperations.openCollection(directory);
				for (int i = 0; i < fileArray.length; i++) {
					// przeszukiwanie wszystkich plikow
					finalFileList.add((File) compareWithFile(fileArray[i],
							searchElement, selector));
				}
			}
		}
		return finalFileList;
	}

	public File compareWithFile(File file, BSONElement<?> deleteElement,
			BSONDocument selector) {
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
						+ searchElement.getName() + " w pliku " + file);
				return file;
			}
		}
		return null;
	}
}
