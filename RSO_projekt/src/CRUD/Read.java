package CRUD;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import messages.QueryMessage;
import bson.BSON;
import bson.BSONDocument;

public class Read {
	public static List<BSONDocument> ReadFromQuery(QueryMessage queryMessage)
	{
		File[] fileArray = FileOperations.openCollection(queryMessage.fullCollectionName);
		List<BSONDocument> documentList = new LinkedList<BSONDocument>();
		
		for(int i = 0; i<fileArray.length; i++)
		{
			BSONDocument bsonDocument = ReadFromFile(queryMessage, fileArray[i]);
			if(bsonDocument.checkQuery(queryMessage.query))
			{
				documentList.add(bsonDocument);
			}
		}
		
		return documentList;
	}

	public static BSONDocument ReadFromFile(QueryMessage message, File file)
	{
		long longNumber = file.length();
		char[] array = new char[(int) longNumber];
		
		try 
		{
			FileReader fileReader = new FileReader(file);
			fileReader.read(array);
			fileReader.close();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] array2 = new byte[(int) longNumber];
		for (int i = 0; i < longNumber; i++)
			array2[i] = (byte)array[i];
		
		BSONDocument bsonDocument = new BSONDocument();
		BSON.parseBSON(array2, bsonDocument);
		return bsonDocument;
	}

}