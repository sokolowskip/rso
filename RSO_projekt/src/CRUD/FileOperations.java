package CRUD;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import bson.BSON;
import bson.BSONDocument;
import bson.ObjectID;

public class FileOperations {
	static String dbDirectory = "exampleDB/";

	// zwraca listê plików nale¿¹cych do danej kolekcji
	public static File[] openCollection(String collectionName) {
		File folder = new File(collectionName);
		File[] listOfFiles = folder.listFiles();

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
			array[i] = (byte) tablica[i].charAt(0);
		}

		BSONDocument bsonDocument = new BSONDocument();
		BSON.parseBSON(array, bsonDocument);
		return bsonDocument;
	}

	// wyszukiwanie elementu zawieraj¹cego pole "_id"
	public static String findIdElement(BSONDocument bsonDocument) {
		String fileName = null;
		for (int i = 0; i < bsonDocument.getElems().size(); i++) {
			if (bsonDocument.getElems().get(i).getName().equals("_id")) {
				ObjectID objectID = (ObjectID) bsonDocument.getElems().get(i)
						.getData();
				if (objectID != null) {
					fileName = Integer.toString(objectID.getCounter())
							+ Integer.toString(objectID.getMachine())
							+ Integer.toString(objectID.getProcID())
							+ Integer.toString(objectID.getTime());
					// System.out.println(fileName);
				}
				return fileName;
			}
		}
		return fileName;
	}

	public static void createFile(BSONDocument bsonDocument, String name) {
		File newRecord = new File(name);
		try {
			if (newRecord.createNewFile()) {
				FileWriter fw = new FileWriter(newRecord.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				// TODO: poprawic jak Marek doda parsowanie bsona do bajtów
				byte[] bsonBytes = BSON.getBSON(bsonDocument);
				bw.write(bsonBytes.toString());
				bw.close();

			} else {
				System.out.println("File already exists.");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// sprawdzenie czy nie istnieje folder
	public static boolean checkIfCollection(String collectionName) {
		File[] listOfFiles = openCollection(dbDirectory);
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isDirectory()) {
				if (listOfFiles[i].getName().equals(collectionName)) {
					return true;
				}
			}
		}
		return false;
	}
	
	//wyszukiwanie pliku
	public static File findFile(String fileName, String collectionName) {
		File[] listOfFiles = openCollection(dbDirectory + "/" + collectionName);
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				if (listOfFiles[i].getName().equals(fileName)) {
					return listOfFiles[i];
				}
			}
		}
		return null;
	}
}
