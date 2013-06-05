package CRUD;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.ArrayList;

import messages.UpdateMessage;

import org.junit.Before;
import org.junit.Test;

import bson.BSON;
import bson.BSONDocument;
import bson.BSONElement;
import bson.BSONtype;

public class UpdateTest {
	UpdateMessage updateMessage = new UpdateMessage();
	String directory = "testoweBsony/";

	@Before
	public void setUp() {
		File[] files = FileOperations.openCollection(directory);
		BSONDocument doc = new BSONDocument();
		doc = FileOperations.readFromFile(files[0]);

		BSONDocument updateDataforBsonDocument = new BSONDocument();
		ArrayList<BSONElement<?>> elems = new ArrayList<BSONElement<?>>();
		elems.add(new BSONElement<String>("name", "kowalski", BSONtype.STRING));
		updateDataforBsonDocument.setElems(elems);

		// byte[] bsonBytes = BSON.getBSON(updateDataforBsonDocument);

		assertNotNull(updateMessage);

		updateMessage.fullCollectionName = "Collection1";
		updateMessage.selector = doc;
		updateMessage.update = updateDataforBsonDocument;
	}

	@Test
	public void test() {
		Update update = new Update();
		update.updateDocument(updateMessage);
	}

}
