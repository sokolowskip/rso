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
	
	//@unused
	/*public static List<BSONDocument> ReadFromQuery(QueryMessage queryMessage)
	{	
		List<BSONDocument> documentList = Selector.searchDocuments(queryMessage.query, queryMessage.fullCollectionName);
		return selectFields(queryMessage.returnFieldSelector, documentList);
	}*/

	public static List<BSONDocument> selectFields(BSONDocument fieldSelector,
			List<BSONDocument> documentList) {
		List<BSONDocument> returnQuery = new LinkedList<BSONDocument>();
		
		Iterator<BSONDocument> iterator = documentList.iterator();
		while(iterator.hasNext())
		{
			returnQuery.add(selectFieldsHelper(iterator.next(), fieldSelector));
		}
		
		return returnQuery;
	}

	private static BSONDocument selectFieldsHelper(BSONDocument document, BSONDocument fieldSelector)
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