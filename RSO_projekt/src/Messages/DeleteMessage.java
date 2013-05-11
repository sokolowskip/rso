package Messages;

import BSON.Document;

public class DeleteMessage {

	public MessageHeader header;
	public int flags;
	public String fullCollectionName;
	public Document selector;

}
