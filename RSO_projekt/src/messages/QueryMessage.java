package messages;

import bson.BSONElement;


public class QueryMessage {

	public MessageHeader header;
	public int flags;
	public String fullCollectionName;
	public int numberToReturn;
	public int numberToSkip;
	public BSONElement query;
	public BSONElement returnFieldSelector;

}
