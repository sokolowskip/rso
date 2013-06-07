package CRUD;

import static org.junit.Assert.assertNotNull;
import messages.DeleteMessage;

import org.junit.Before;
import org.junit.Test;

import bson.BSONDocument;

public class DeleteTest {
	DeleteMessage deleteMessage = new DeleteMessage();
	// String directory = "testoweBsony/";
	String dbDirectory = "exampleDB/Collection1";

	@Before
	public void setUp() {
		BSONDocument doc = new BSONDocument();
		doc = FileOperations.readBytesFromFile(dbDirectory + "/"
				+ "1584363257887151367606178");
		assertNotNull(doc);
		deleteMessage.fullCollectionName = "Collection1";
		deleteMessage.selector = doc;
	}

	@Test
	public void test() {
		Delete delete = new Delete();
		delete.deleteDocument(deleteMessage);
	}
}
