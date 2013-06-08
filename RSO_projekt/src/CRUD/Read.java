package CRUD;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import messages.QueryMessage;
import bson.BSON;
import bson.BSONDocument;
import bson.BSONElement;

public class Read {
	public static List<BSONDocument> ReadFromQuery(QueryMessage queryMessage)
	{	
		List<BSONDocument> documentList = Selector.searchDocuments(queryMessage.query, queryMessage.fullCollectionName);
		List<BSONDocument> returnQuery = new Vector<BSONDocument>();
		
		BSONDocument fieldSelector = queryMessage.returnFieldSelector;
		Iterator<BSONDocument> iterator = documentList.iterator();
		while(iterator.hasNext())
		{
			returnQuery.add(selectFields(iterator.next(), fieldSelector));
		}
		
		return returnQuery;
	}

	private static BSONDocument selectFields(BSONDocument document, BSONDocument fieldSelector)
	{
		BSONDocument newDocument = new BSONDocument();
		
		List<BSONElement<?>> fields = fieldSelector.getElems();
		
		while(!fields.isEmpty())
		{
			Iterator<BSONElement<?>> documentIterator = document.getElems().iterator();
			while(documentIterator.hasNext())
			{
				BSONElement<?> documentField = documentIterator.next();
				
				Iterator<BSONElement<?>> fieldsIterator = document.getElems().iterator();
				while(fieldsIterator.hasNext())
				{
					BSONElement<?> field = fieldsIterator.next();
					
					if(field.getName().equals(documentField.getName()));
					{
						newDocument.addElem(documentField);
						fieldsIterator.remove();
						break;
					}
				}
				
			}
		}
		
		return newDocument;
	}
}