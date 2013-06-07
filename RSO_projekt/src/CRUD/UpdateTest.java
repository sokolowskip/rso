package CRUD;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import messages.UpdateMessage;

import org.junit.Before;
import org.junit.Test;

import bson.BSONDocument;
import bson.BSONElement;
import bson.BSONtype;

public class UpdateTest {
	UpdateMessage updateMessage = new UpdateMessage();
	String dbDirectory = "exampleDB/Collection1";

	@Before
	public void setUp() {
		BSONDocument updateSelectorDoc = new BSONDocument();
		updateSelectorDoc = FileOperations.readBytesFromFile(dbDirectory + "/"
				+ "1584363257887151367606178");
		BSONDocument updateDataDoc = new BSONDocument();
		ArrayList<BSONElement<?>> elems = new ArrayList<BSONElement<?>>();
		elems.add(new BSONElement<String>("name", "kowalski", BSONtype.STRING));
		updateDataDoc.setElems(elems);

		assertNotNull(updateMessage);

		updateMessage.fullCollectionName = "Collection1";
		updateMessage.selector = updateSelectorDoc;
		updateMessage.update = updateDataDoc;
	}

	@Test
	public void test() {
		Update update = new Update();
		update.updateDocument(updateMessage);
	}

}
