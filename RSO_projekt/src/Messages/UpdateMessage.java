
public class UpdateMessage {
    private MsgHeader header;             // standard message header
    private int ZERO;               // 0 - reserved for future use
    private string fullCollectionName; // "dbname.collectionname"
    private int flags;              // bit vector. see below
    private document selector;           // the query to select the document
    private document update;             // specification of the update to perform
}
