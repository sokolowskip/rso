import java.util.Arrays;

public static class MessageParser {

	Public enum MessageType
	{
		OP_REPLY 1, 
		OP_MSG 1000,
		OP_UPDATE 2001,
		OP_INSERT 2002,
		OP_QUERY 2004,
		OP_GET_MORE 2005,
		OP_DELETE 2006,
		OP_KILL_CURSORS 2007,
		OTHER
	}

	public static MessageType getType(byte[] msg)
	{
		int opCode = intToByteArray(byteSubarray(msg, 12, 4));
		
		switch(opCode)
		{
			//OP_REPLY 	1 	Reply to a client request. responseTo is set
			case 1:
				return OP_REPLY;
			//OP_MSG 	1000 	generic msg command followed by a string
			case 1000:
				return OP_MSG;
			//OP_UPDATE 	2001 	update document
			case 2001:
				return OP_UPDATE;
			//OP_INSERT 	2002 	insert new document
			case 2002:
				return OP_INSERT;
			//RESERVED 	2003 	formerly used for OP_GET_BY_OID
			case 2003:
				return OTHER;
			//OP_QUERY 	2004 	query a collection
			case 2004:
				return OP_QUERY;
			//OP_GET_MORE 	2005 	Get more data from a query. See Cursors
			case 2005:
				return OP_GET_MORE;
			//OP_DELETE 	2006 	Delete documents
			case 2006:
				return DELETE;
			//OP_KILL_CURSORS 	2007 	Tell database client is done with a cursor
			case 2007:
				return OP_KILL_CURSORS;
			default:
				return OTHER;
		}
	}
	
	public static ReplyMessage ParseReplyMessage(byte[] msg)
	{
		return ReplyMessage();
	}
	
	public static GenericMessage ParseGenericMessage(byte[] msg)
	{
		return GenericMessage();
	}
	
	public static UpdateMessage ParseUpdateMessage(byte[] msg)
	{
		return UpdateMessage();
	}
	
	public static InsertMessage ParseInsertMessage(byte[] msg)
	{
		return InsertMessage();
	}
	
	public static QueryMessage ParseQueryMessage(byte[] msg)
	{
		return QueryMessage();
	}
	
	public static GetMoreMessage ParseGetMoreMessage(byte[] msg)
	{
		return GetMoreMessage();
	}
	
	public static DeleteMessage ParseDeleteMessage(byte[] msg)
	{
		return DeleteMessage();
	}
	
	public static KillCursorsMessage ParseKillCursorsMessage(byte[] msg)
	{
		return KillCursorsMessage();
	}
	
	private static int byteSubarray(byte[] msg, int from, int count)
	{
		return Arrays.copyOfRange(Object[] src, int from, int from + count);
	}
	
	private static int intToByteArray(byte[] b)
	{
		return   b[0] & 0xFF |
				(b[1] & 0xFF) << 8 |
				(b[2] & 0xFF) << 16 |
				(b[3] & 0xFF) << 24;
	}
}
