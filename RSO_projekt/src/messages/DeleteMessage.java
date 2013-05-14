package messages;

import bson.BSONDocument;

public class DeleteMessage {

	public MessageHeader header;
	public int flags;
	public String fullCollectionName;
	public BSONDocument selector;

}
