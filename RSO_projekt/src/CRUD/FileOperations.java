package CRUD;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import bson.BSON;
import bson.BSONDocument;
import bson.BSONElement;

public class FileOperations {
	String directory = "C:/Users/Tomek/Desktop/test/";

	// zwraca listê plików nale¿¹cych do danej kolekcji
	public static File[] openCollection(String collectionName) {
		// zak³adam ¿e pliki le¿¹ p³asko w kolekcji, je¿eli oka¿e siê to ma³o
		// wydajne trzeba bêdzie to skomplikowaæ
		File folder = new File(collectionName);
		File[] listOfFiles = folder.listFiles();

		// for (int i = 0; i < listOfFiles.length; i++) {
		// if (listOfFiles[i].isFile()) {
		// System.out.println("File " + listOfFiles[i].getName());
		// } else if (listOfFiles[i].isDirectory()) {
		// System.out.println("Directory " + listOfFiles[i].getName());
		// }
		// }

		return listOfFiles;
	}

	public static BSONDocument readFromFile(File file) {
		long longNumber = file.length();
		byte[] array = new byte[(int) longNumber];
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
			array[i] = (byte)tablica[i].charAt(0);
		}

		BSONDocument bsonDocument = new BSONDocument();
		BSON.parseBSON(array, bsonDocument);
		return bsonDocument;
	}

	public static void insertFile(File file) {

	}

	public static void deleteFile(File file) {
		file.delete();
	}

	public static <T> void updateFile(File file, String updateName, T updateData) {
		BSONDocument bsonDocument = new BSONDocument();
		bsonDocument = readFromFile(file);

		Iterator<BSONElement<?>> iterator = bsonDocument.getElems().iterator();
		while (iterator.hasNext()) {
			BSONElement<?> documentElement = iterator.next();
			if (documentElement.getName().equals(updateName))
				;
			{
				// documentElement.setData(updateData);
				// delete file
				// insert file
			}
		}
	}
}
