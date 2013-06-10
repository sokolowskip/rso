package messages;

import bson.BSONDocument;


public class ReplyMessage {

	public MessageHeader header;
	public int responseFlags;
	public long cursorID;
	public int startingFrom;
	public int numberReturned;
	public BSONDocument[] documents;
}
