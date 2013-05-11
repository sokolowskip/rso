package Messages;

import BSON.Document;


public class QueryMessage {

	public MessageHeader header;
	public int flags;
	public String fullCollectionName;
	public int numberToReturn;
	public int numberToSkip;
	public Document query;
	public Document returnFieldSelector;

}
