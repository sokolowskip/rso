package CRUD;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;


import messages.DeleteMessage;

import org.junit.Before;
import org.junit.Test;

import bson.BSON;
import bson.BSONDocument;

import com.google.gson.Gson;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class DeleteTest {
	Collection<DBObject> collection = new ArrayList<DBObject>();
	Gson gson = new Gson();
	DeleteMessage deleteMessage = new DeleteMessage();
	String directory = "C:/Users/Tomek/Desktop/test/";

	@Before
	public void setUp() {
		BSONDocument doc = new BSONDocument();
		// przyk³adowy BSON
		char[] testData = new char[] { 0x45, 0x00, 0x00, 0x00, 0x07, 0x5f,
				0x69, 0x64, 0x00, 0x51, 0x84, 0x03, 0xa2, 0x58, 0x54, 0x2b,
				0xcd, 0xa4, 0xf1, 0xc1, 0x30, 0x02, 0x6e, 0x61, 0x6d, 0x65,
				0x00, 0x07, 0x00, 0x00, 0x00, 0x6d, 0x6b, 0x79, 0x6f, 0x6e,
				0x67, 0x00, 0x10, 0x61, 0x67, 0x65, 0x00, 0x1e, 0x00, 0x00,
				0x00, 0x09, 0x63, 0x72, 0x65, 0x61, 0x74, 0x65, 0x64, 0x44,
				0x61, 0x74, 0x65, 0x00, 0x0e, 0x34, 0xae, 0x6b, 0x3e, 0x01,
				0x00, 0x00 };
		BSON.parseBSON(testData, doc);
		
		deleteMessage.fullCollectionName = directory;
		deleteMessage.selector = doc; 

		// DataObject dataObject1 = new DataObject(1, "pierwszy", 10.89);
		// String json1 = gson.toJson(dataObject1);
		//
		// DataObject dataObject2 = new DataObject(2, "drugi", 18.467);
		// String json2 = gson.toJson(dataObject2);
		//
		// DBObject dbObject1 = (DBObject) JSON.parse(json1);
		// DBObject dbObject2 = (DBObject) JSON.parse(json2);
		//
		// collection.add(dbObject1);
		// collection.add(dbObject2);
		// System.out.println(collection);
	}

	@Test
	public void test() {
		Delete deleteObj = new Delete();
		
		deleteObj.deteleEntry(deleteMessage);

		// String json =
		// "{ \"id\" : 5 , \"name\" : \"pi¹ty\" , \"value\" : 8.78}";
		// deleteObj.deleteObject(collection, 2);
		// System.out.println(collection);
	}
}
