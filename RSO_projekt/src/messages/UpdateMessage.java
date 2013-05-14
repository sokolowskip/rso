package messages;

import bson.BSONDocument;

public class UpdateMessage {
    public MessageHeader header;             // standard message header
    public int ZERO;               // 0 - reserved for future use
    public String fullCollectionName; // "dbname.collectionname"
    public int flags;              // bit vector. see below
    public BSONDocument selector;           // the query to select the document
    public BSONDocument update;             // specification of the update to perform
}
