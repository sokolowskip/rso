package messages;

import bson.Document;

public class DeleteMessage {

	public MessageHeader header;
	public int flags;
	public String fullCollectionName;
	public Document selector;

}
