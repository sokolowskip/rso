package CRUD;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import bson.BSONDocument;

public class SelectorTest {
	String dbDirectory = "exampleDB/Collection1";
	BSONDocument doc = new BSONDocument();

	@Before
	public void setUp() {
		doc = FileOperations.readBytesFromFile(dbDirectory + "/"
				+ "1584363257887151367606178");
		assertNotNull(doc);
	}

	@Test
	public void test() {
		assertNotNull(doc);
		Selector.getFilesIDs(doc, "Collection1");
	}

}
