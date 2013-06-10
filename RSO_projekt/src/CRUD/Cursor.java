package CRUD;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import messages.MessageHeader;
import messages.ReplyMessage;

import bson.BSONDocument;

public class Cursor {
	private long id;
	private BSONDocument selector;
	private BSONDocument fieldsSelector;
	private File[] fileArray;

	private int fileIndex;
	private BSONDocument orphan;
	
	public Cursor(File[] fileArray, long id, BSONDocument selector, BSONDocument fieldsSelector)
	{
		this.id = id;
		this.selector = selector;
		this.fieldsSelector = fieldsSelector;
		this.fileArray = fileArray;
		fileIndex = 0;
		orphan = null;
	}
	
	public ReplyMessage getMore(int howMuch)
	{
		ReplyMessage reply = new ReplyMessage();
		
		reply.cursorID = 0;
		reply.startingFrom = fileIndex;
		
		List<BSONDocument> documentList = new LinkedList<BSONDocument>();
		if(orphan != null)
		{
			documentList.add(orphan);
			orphan = null;
			howMuch--;
		}
		
		while(howMuch > 0 && fileIndex < fileArray.length)
		{
			File file = fileArray[fileIndex];
			fileIndex++;
			BSONDocument document = FileOperations.readBytesFromFile(file);
			
			if(Selector.checkElement(selector, document))
			{
				documentList.add(document);
				howMuch--;
			}
		}
		
		while(fileIndex < fileArray.length)
			//jesli znajde chociaz jeden dokument to bede wiedzial ze sa jeszcze jakies nastepne
		{
			File file = fileArray[fileIndex];
			fileIndex++;
			BSONDocument document = FileOperations.readBytesFromFile(file);
			
			if(Selector.checkElement(selector, document))
			{
				orphan = document;
				reply.cursorID = id;
				
				break;
			}
		}
		
		reply.documents = (BSONDocument[]) Read.selectFields(fieldsSelector, documentList).toArray();
		
		reply.numberReturned = documentList.size();
		reply.responseFlags = 0;
		reply.header = new MessageHeader(0, 0, 0, 1);
		
		return reply;
	}
}
