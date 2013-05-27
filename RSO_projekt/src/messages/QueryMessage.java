package messages;

import bson.BSONDocument;


public class QueryMessage {

	public MessageHeader header;
	public int flags;
	public String fullCollectionName;
	public int numberToReturn;
	public int numberToSkip;
	public BSONDocument query;
	public BSONDocument returnFieldSelector;

}
