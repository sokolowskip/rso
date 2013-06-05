package CRUD;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bson.BSONDocument;
import bson.BSONElement;

public class Selector {
	private static String dbDirectory = "exampleDB/";
//	private static String collectionName;

	public static List<String> getFilesIDs(BSONDocument selector, String collectionName) {
		// pobieranie danych z message
		List<String> finalFileList = new ArrayList<String>();
		Iterator<BSONElement<?>> iterator = selector.getElems().iterator();

		// przeszukiwanie message
		while (iterator.hasNext()) {
			BSONElement<?> selectorElement = iterator.next();
			if (selectorElement.getName() != null) {
				File[] fileArray = FileOperations.openCollection(dbDirectory
						+ collectionName);
				if (fileArray != null) {
					for (int i = 0; i < fileArray.length; i++) {
						// przeszukiwanie wszystkich plikow
						finalFileList.add(compareWithFile(selectorElement,
								fileArray[i]));
					}
				}
			}
		}
//		java.util.Set uniqueFinalFileList = new java.util.HashSet(
//				java.util.Arrays.asList(finalFileList));
//		return (List<String>) uniqueFinalFileList;
		return finalFileList;
	}

	public static String compareWithFile(BSONElement<?> selectorElement,
			File file) {
		// pobieranie danych z pliku
		BSONDocument fileBsonDocument = FileOperations.readFromFile(file);
		Iterator<BSONElement<?>> iterator2 = fileBsonDocument.getElems()
				.iterator();
		// przeszukiwanie BSON-ow w pliku
		while (iterator2.hasNext()) {
			BSONElement<?> searchElement = iterator2.next();
			// porownanie nazwy z message i z pliku
			if (selectorElement.getName().equals(searchElement.getName())
					&& selectorElement.getData()
							.equals(searchElement.getData())) {
				System.out.println("znaleziono dokument z t¹ sam¹ nazw¹ "
						+ searchElement.getName() + " w pliku " + file);
				// zwracanie "_id" pliku
				return FileOperations.findIdElement(fileBsonDocument);
			}
		}
		return null;
	}
}
