package Messages;

public class UpdateMessage {
    public MessageHeader header;             // standard message header
    public int ZERO;               // 0 - reserved for future use
    public String fullCollectionName; // "dbname.collectionname"
    public int flags;              // bit vector. see below
    public String selector;           // the query to select the document
    public String update;             // specification of the update to perform
}
