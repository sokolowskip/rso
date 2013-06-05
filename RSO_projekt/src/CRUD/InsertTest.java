package CRUD;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import bson.BSONDocument;

public class InsertTest {
	String directory = "testoweBsony/";

	@Test
	public void test() {
		File[] files = FileOperations.openCollection(directory);
		BSONDocument doc = new BSONDocument();
		doc = FileOperations.readFromFile(files[0]);

		Insert insert = new Insert();
		//TODO: przetestowaæ dla insert Message
		// insert.insertDocumentList(insertMessage);
	}

}
