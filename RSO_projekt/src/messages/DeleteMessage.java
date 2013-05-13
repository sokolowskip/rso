package messages;

import bson.BSONElement;

public class DeleteMessage {

	public MessageHeader header;
	public int flags;
	public String fullCollectionName;
	public BSONElement selector;

}
