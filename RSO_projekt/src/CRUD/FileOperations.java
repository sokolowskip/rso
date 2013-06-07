package CRUD;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import bson.BSON;
import bson.BSONDocument;
import bson.ObjectID;

public class FileOperations {
	static String dbDirectory = "exampleDB/";

	// zwraca liste plikow nalezacych do danej kolekcji
	public static File[] openCollection(String collectionName) {
		File folder = new File(collectionName);
		File[] listOfFiles = folder.listFiles();

		return listOfFiles;
	}

	public static BSONDocument readBytesFromFile(String filePath) {
		BSONDocument bsonDocument = new BSONDocument();
		Path path = Paths.get(filePath);
		byte[] data;
		try {
			data = Files.readAllBytes(path);
			BSON.parseBSON(data, bsonDocument);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bsonDocument;
	}

	public static BSONDocument readFromFile2(File file) {
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

	// wyszukiwanie elementu zawierajacego pole "_id"
	public static String findIdElement(BSONDocument bsonDocument) {
		String fileName = null;
		for (int i = 0; i < bsonDocument.getElems().size(); i++) {
			if (bsonDocument.getElems().get(i).getName().equals("_id")) {
				ObjectID objectID = (ObjectID) bsonDocument.getElems().get(i)
						.getData();
				if (objectID != null) {
					fileName = Integer.toString(objectID.getCounter())
							+ Integer.toString(objectID.getMachine())
							+ Integer.toString(objectID.getTime());
				}
				return fileName;
			}
		}
		return fileName;
	}

	public static void createFile(BSONDocument bsonDocument, String name) {
		byte[] bsonBytes = BSON.getBSON(bsonDocument);
		try {
			FileOutputStream fos = new FileOutputStream(name);
			fos.write(bsonBytes);
			fos.close();
		} catch (FileNotFoundException ex) {
			System.out.println("FileNotFoundException : " + ex);
		} catch (IOException ioe) {
			System.out.println("IOException : " + ioe);
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

	// wyszukiwanie pliku
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
