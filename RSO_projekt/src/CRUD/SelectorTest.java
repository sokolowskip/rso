package CRUD;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import bson.BSONDocument;

public class SelectorTest {
	String directory = "testoweBsony/";
	BSONDocument doc = new BSONDocument();

	@Before
	public void setUp() {
		File[] files = FileOperations.openCollection(directory);
		doc = FileOperations.readFromFile(files[0]);
		assertNotNull(doc);
	}

	@Test
	public void test() {
		assertNotNull(doc);
		Selector.getFilesIDs(doc, "Collection1");
	}

}
