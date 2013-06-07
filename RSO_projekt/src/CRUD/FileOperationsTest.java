package CRUD;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import bson.BSONDocument;

public class FileOperationsTest {
	String dbDirectory = "exampleDB/Collection1";

	@Test
	public void test() {
		BSONDocument doc = new BSONDocument();
		doc = FileOperations.readBytesFromFile(dbDirectory + "/"
				+ "1584363257887151367606178");
		assertNotNull(doc);

		// wyswietlanie zawartosci BSON-a
		for (int i = 0; i < doc.getElems().size(); i++) {
			System.out.println("Data: " + doc.getElems().get(i).getData());
			System.out.println("Name: " + doc.getElems().get(i).getName());
			System.out.println("Type: " + doc.getElems().get(i).getType());
		}

	}
}
