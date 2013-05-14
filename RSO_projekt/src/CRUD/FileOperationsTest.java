package CRUD;

import static org.junit.Assert.*;

import org.junit.Test;

public class FileOperationsTest {
	String directory = "C:/Users/Tomek/Desktop/test";

	@Test
	public void test() {
		FileOperations fo = new FileOperations();
		fo.findFiles(directory);
	}

}
