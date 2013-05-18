package CRUD;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.mongodb.DBObject;
import java.util.List;
import bson.BSONDocument;
import bson.BSON;
import messages.QueryMessage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.LinkedList;

public class Read {
	public static List<BSONDocument> ReadFromQuery(QueryMessage queryMessage)
	{
		File[] fileArray = FileOperations.openCollection(queryMessage.fullCollectionName);
		
		List<BSONDocument> documentList = new LinkedList<BSONDocument>();
		
		for(int i = 0; i<fileArray.length; i++)
		{
			documentList.add(ReadFromFile(queryMessage, fileArray[i]));
		}
		
		return documentList;
	}
	
	public static BSONDocument ReadFromFile(QueryMessage message, File file)
	{
		long longNumber = file.length();
		char[] array = new char[(int) longNumber];
		
		try {
			FileReader fileReader = new FileReader(file);
			fileReader.read(array);
			fileReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BSONDocument bsonDocument = new BSONDocument();
		BSON.parseBSON(array, bsonDocument);
		
		return bsonDocument;
	}

}