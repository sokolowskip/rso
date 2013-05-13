package CRUD;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class DeleteTest {
	Collection<DBObject> collection = new ArrayList<DBObject>();
	Gson gson = new Gson();

	@Before
	public void setUp() {

		DataObject dataObject1 = new DataObject(1, "pierwszy", 10.89);
		String json1 = gson.toJson(dataObject1);

		DataObject dataObject2 = new DataObject(2, "drugi", 18.467);
		String json2 = gson.toJson(dataObject2);

		DBObject dbObject1 = (DBObject) JSON.parse(json1);
		DBObject dbObject2 = (DBObject) JSON.parse(json2);

		collection.add(dbObject1);
		collection.add(dbObject2);
		// System.out.println(collection);
	}

	@Test
	public void test() {
		Delete deleteObj = new Delete();

		String json = "{ \"id\" : 5 , \"name\" : \"pi¹ty\" , \"value\" : 8.78}";
		deleteObj.deleteObject(collection, 1);
		System.out.println(collection);
	}
}
