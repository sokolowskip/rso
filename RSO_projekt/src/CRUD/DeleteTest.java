package CRUD;

import java.io.File;

import messages.DeleteMessage;

import org.junit.Before;
import org.junit.Test;

import bson.BSONDocument;

public class DeleteTest {
	DeleteMessage deleteMessage = new DeleteMessage();
	String directory = "testoweBsony/";

	@Before
	public void setUp() {
		File[] files = FileOperations.openCollection(directory);
		BSONDocument doc = new BSONDocument();
		doc = FileOperations.readFromFile(files[0]);
		deleteMessage.fullCollectionName = "Collection1";
		deleteMessage.selector = doc; 
	}

	@Test
	public void test() {
		Delete delete = new Delete();
		delete.deleteDocument(deleteMessage);
	}
}
