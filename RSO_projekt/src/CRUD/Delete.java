package CRUD;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import messages.DeleteMessage;
import bson.BSON;
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
		BSONDocument fileBsonDocument = readFromFile(file);
		Iterator<BSONElement<?>> iterator2 = fileBsonDocument.getElems()
				.iterator();
		// przeszukiwanie BSON-ow w pliku
		while (iterator2.hasNext()) {
			BSONElement<?> searchElement = iterator2.next();
			// porownanie nazwy z deleteMessage i z pliku
			if (deleteElement.getName().equals(searchElement.getName())) {
				System.out.println("znaleziono dokument z t¹ sam¹ nazw¹ "
						+ searchElement.getName() + " w pliku "
						+ deleteMessage.fullCollectionName);
				System.out.println("zawartoœæ: " + searchElement.getData());
			}
			
			// TODO:
			// dodac inne opcje (usuwanie nie tylko po nazwie)
		}
	}

	public static BSONDocument readFromFile(File file) {
		long longNumber = file.length();
		char[] array = new char[(int) longNumber];
		String[] tablica = new String[(int) longNumber];
		StringBuilder fileContent = new StringBuilder();

		BufferedReader br = null;

		try {

			br = new BufferedReader(new FileReader(file));
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				fileContent.append(currentLine);

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		String[] items = fileContent.toString().split(",");
		for (int i = 0; i < items.length; i++) {

			int decimal = Integer.parseInt(items[i].substring(3), 16);
			StringBuilder sb = new StringBuilder();
			// konwersja dziesietnej na znak
			sb.append((char) decimal);
			tablica[i] = sb.toString();
			array[i] = tablica[i].charAt(0);
		}

		BSONDocument bsonDocument = new BSONDocument();
		BSON.parseBSON(array, bsonDocument);
		return bsonDocument;
	}
}
