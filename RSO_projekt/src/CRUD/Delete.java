package CRUD;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import messages.DeleteMessage;
import messages.QueryMessage;

import bson.BSON;
import bson.BSONDocument;
import bson.BSONElement;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.mongodb.DBObject;

public class Delete {
	public void deleteObject(final Collection<DBObject> collection, final int id) {

		final Gson gson = new Gson();
		DBObject dbo = Iterables.find(collection, new Predicate<DBObject>() {
			public boolean apply(DBObject dbo) {
				// System.out.println(dbo);
				DataObject dataObject = gson.fromJson(dbo.toString(),
						DataObject.class);
				if (dataObject.getId() == id) {
					// System.out.println(id);
					// usun element
					collection.remove(dbo);
				}
				return dataObject.getId() == id;
			}
		});
	}

	public void deleteObject(final Collection<DBObject> collection,
			final String name) {

		final Gson gson = new Gson();
		DBObject dbo = Iterables.find(collection, new Predicate<DBObject>() {
			public boolean apply(DBObject dbo) {
				// System.out.println(dbo);
				DataObject dataObject = gson.fromJson(dbo.toString(),
						DataObject.class);
				if (dataObject.getName() == name) {
					// System.out.println(id);
					// usun element
					collection.remove(dbo);
				}
				return dataObject.getName() == name;
			}
		});
	}

	public void deleteObject(final Collection<DBObject> collection,
			final double value) {

		final Gson gson = new Gson();
		DBObject dbo = Iterables.find(collection, new Predicate<DBObject>() {
			public boolean apply(DBObject dbo) {
				// System.out.println(dbo);
				DataObject dataObject = gson.fromJson(dbo.toString(),
						DataObject.class);
				if (dataObject.getValue() == value) {
					// System.out.println(id);
					// usun element
					collection.remove(dbo);
				}
				return dataObject.getValue() == value;
			}
		});
	}

	public void deteleEntry(DeleteMessage deleteMessage) {
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
					BSONDocument fileBsonDocument = readFromFile(fileArray[i]);
					Iterator<BSONElement<?>> iterator2 = fileBsonDocument
							.getElems().iterator();
					// przeszukiwanie BSON-ow w plikach
					while (iterator.hasNext()) {
						BSONElement<?> searchElement = iterator2.next();
						if (deleteElement.getName() == searchElement.getName()) {
							System.out
									.println("znaleziono dokument z t¹ sam¹ nazw¹ w pliku"
											+ deleteMessage.fullCollectionName);
							// sprwadzenie inncyh wariantow
						}
					}
				}
			}

		}
	}

	public static BSONDocument readFromFile(File file) {
		long longNumber = file.length();
		char[] array = new char[(int) longNumber];
		String[] tablica = new String[(int) longNumber];
		StringBuilder fileContent = new StringBuilder();

		// try {
		// FileReader fileReader = new FileReader(file);
		// fileReader.read(array);
		// fileReader.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

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
//			 convert the decimal to character
			StringBuilder sb = new StringBuilder();
			sb.append((char) decimal);
			tablica[i] = sb.toString();
		}

		BSONDocument bsonDocument = new BSONDocument();
		BSON.parseBSON(array, bsonDocument);
		return bsonDocument;
	}
}
