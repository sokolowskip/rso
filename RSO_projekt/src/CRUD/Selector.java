package CRUD;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import bson.BSONDocument;
import bson.BSONElement;

public class Selector {
	private static String dbDirectory = "exampleDB/";

	// private static String collectionName;

	public static List<String> getFilesIDs(BSONDocument selector,
			String collectionName) {
		// pobieranie danych z message
		List<String> tempFileNameList = new ArrayList<String>();
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
						String fileName = compareWithFile(selectorElement,
								dbDirectory + collectionName + "/"
										+ fileArray[i].getName());
						if (fileName != null) {
							tempFileNameList.add(fileName);
							System.out.println("dodano plik o nazwie: " + fileName + " do listy selectora");
						}
					}
				}
			}
		}
		// przypisanie rekordom tabeli informacji o ilosci ich powtorzen
		List<String> stringList = new ArrayList<String>();
		List<Integer> intList = new ArrayList<Integer>();
		for (int i = 0; i < tempFileNameList.size(); i++) {
			// sprawdzenie czy element ju¿ nie dodany
			if (stringList.size() == 0) {
				int occurrences = Collections.frequency(tempFileNameList,
						tempFileNameList.get(i));
				stringList.add(tempFileNameList.get(i));
				intList.add(occurrences);
			}
			for (int j = 0; j < stringList.size(); j++) {
				if (!tempFileNameList.get(i).equals(stringList.get(j))) {
					int occurrences = Collections.frequency(tempFileNameList,
							tempFileNameList.get(i));
					// dodanie elementow do list
					stringList.add(tempFileNameList.get(i));
					intList.add(occurrences);
				}
			}
		}
		// wybranie elementów wystepujacych w kazdym polu => ilosc wystapien =
		// selector.getElems().size()-1; (minus ObejctID)
		for (int i = 0; i < intList.size(); i++) {
			if (intList.get(i) < selector.getElems().size()-1) {
				intList.remove(i);
				stringList.remove(i);
			}
		}

		return stringList;
	}

	public static String compareWithFile(BSONElement<?> selectorElement,
			String filePathToSelect) {
		// pobieranie danych z pliku
		BSONDocument fileBsonDocument = FileOperations
				.readBytesFromFile(filePathToSelect);
		Iterator<BSONElement<?>> iterator2 = fileBsonDocument.getElems()
				.iterator();
		// przeszukiwanie BSON-ow w pliku
		while (iterator2.hasNext()) {
			BSONElement<?> searchElement = iterator2.next();
			// porownanie nazwy z message i z pliku
			if (selectorElement.getName().equals(searchElement.getName())
					&& selectorElement.getData()
							.equals(searchElement.getData())) {
				System.out.println("znaleziono dokument z tym samym elementem: "
						+ searchElement.getName() + " w " + filePathToSelect);
				// zwracanie "_id" pliku
				return FileOperations.findIdElement(fileBsonDocument);
			}
		}
		return null;
	}
}
