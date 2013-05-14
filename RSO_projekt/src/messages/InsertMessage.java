package messages;

import bson.BSONDocument;

public class InsertMessage {

    public MessageHeader header;             // standard message header
    public int     flags;              // bit vector - see below
    public String   fullCollectionName; // "dbname.collectionname"
    public BSONDocument[] documents;          // one or more documents to insert into the collection
}
