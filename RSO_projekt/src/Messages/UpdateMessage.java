package Messages;

import BSON.Document;

public class UpdateMessage {
    public MessageHeader header;             // standard message header
    public int ZERO;               // 0 - reserved for future use
    public String fullCollectionName; // "dbname.collectionname"
    public int flags;              // bit vector. see below
    public Document selector;           // the query to select the document
    public Document update;             // specification of the update to perform
}
