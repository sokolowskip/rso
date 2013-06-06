package CRUD;

import static org.junit.Assert.assertNotNull;
import messages.InsertMessage;

import org.junit.Test;

import bson.BSONDocument;

public class InsertTest {
	InsertMessage insertMessage = new InsertMessage();
	String directory = "testoweBsony/";

	@Test
	public void test() {
		// File[] files = FileOperations.openCollection(directory);
		BSONDocument doc = new BSONDocument();
		doc = FileOperations.readBytesFromFile(directory + "1");

		assertNotNull(insertMessage);

		insertMessage.fullCollectionName = "Collection1";
		BSONDocument[] bsonDocuments = new BSONDocument[1];
		bsonDocuments[0] = doc;
		insertMessage.documents = bsonDocuments;

		Insert insert = new Insert();
		insert.insertDocumentList(insertMessage);
	}

}
