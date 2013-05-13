package messages;

import bson.Document;

public class InsertMessage {

    public MessageHeader header;             // standard message header
    public int     flags;              // bit vector - see below
    public String   fullCollectionName; // "dbname.collectionname"
    public Document[] documents;          // one or more documents to insert into the collection
}
