package Messages;

import java.util.Arrays;
import java.lang.String;
public class MessageParser {

	public enum MessageType
	{
		OP_REPLY, 
		OP_MSG,
		OP_UPDATE,
		OP_INSERT,
		OP_QUERY,
		OP_GET_MORE,
		OP_DELETE,
		OP_KILL_CURSORS,
		OTHER
	}

	public static MessageType getType(byte[] msg)
	{
		int opCode = byteArrayToInt(msg, 12);
		
		switch(opCode)
		{
			//OP_REPLY 	1 	Reply to a client request. responseTo is set
			case 1:
				return MessageType.OP_REPLY;
			//OP_MSG 	1000 	generic msg command followed by a string
			case 1000:
				return MessageType.OP_MSG;
			//OP_UPDATE 	2001 	update document
			case 2001:
				return MessageType.OP_UPDATE;
			//OP_INSERT 	2002 	insert new document
			case 2002:
				return MessageType.OP_INSERT;
			//RESERVED 	2003 	formerly used for OP_GET_BY_OID
			case 2003:
				return MessageType.OTHER;
			//OP_QUERY 	2004 	query a collection
			case 2004:
				return MessageType.OP_QUERY;
			//OP_GET_MORE 	2005 	Get more data from a query. See Cursors
			case 2005:
				return MessageType.OP_GET_MORE;
			//OP_DELETE 	2006 	Delete documents
			case 2006:
				return MessageType.OP_DELETE;
			//OP_KILL_CURSORS 	2007 	Tell database client is done with a cursor
			case 2007:
				return MessageType.OP_KILL_CURSORS;
			default:
				return MessageType.OTHER;
		}
	}
	
	public static ReplyMessage ParseReplyMessage(byte[] msg)
	{
		MessageHeader messageHeader = CreateHeader(msg);
		
		return new ReplyMessage();
	}
	
	public static GenericMessage ParseGenericMessage(byte[] msg)
	{
		MessageHeader messageHeader = CreateHeader(msg);
		
		return new GenericMessage();
	}
	
	public static UpdateMessage ParseUpdateMessage(byte[] msg)
	{
		UpdateMessage updateMessage = new UpdateMessage();
		
		updateMessage.header = CreateHeader(msg);

		
		String fullCollectionName = byteArrayToString(msg, 20);
		int stringLength = fullCollectionName.length();
		
		updateMessage.fullCollectionName = fullCollectionName;
		
		int i = stringLength + 20 + 1;
		
		updateMessage.flags = (byteArrayToInt(msg, i));
		
		i += 4;

		int selectorLength = (byteArrayToInt(msg, i));
		

		byte[] selector = byteSubarray(msg, i, selectorLength);
		
		i += selectorLength;
		

		int UpdateLength = (byteArrayToInt(msg, i));
		
		byte[] update = byteSubarray(msg, i, UpdateLength);
		
		return updateMessage;
	}
	
	public static InsertMessage ParseInsertMessage(byte[] msg)
	{
		
		InsertMessage insertMessage = new InsertMessage();
		
		insertMessage.header = CreateHeader(msg);
		
		insertMessage.flags = (byteArrayToInt(msg, 16));
		
		String fullCollectionName = byteArrayToString(msg, 20);
		
		int i = 20 + fullCollectionName.length() + 1;
		
		insertMessage.fullCollectionName = fullCollectionName;
		
		//tutaj powinienem obrobic jeszcze adres do dokumentu / dokumentow ale nie wiem jak to zrobic
		
		return insertMessage; 
	}
	
	public static QueryMessage ParseQueryMessage(byte[] msg)
	{
		MessageHeader messageHeader = CreateHeader(msg);
		return new QueryMessage();
	}
	
	public static GetMoreMessage ParseGetMoreMessage(byte[] msg)
	{
		MessageHeader messageHeader = CreateHeader(msg);
		return new GetMoreMessage();
	}
	
	public static DeleteMessage ParseDeleteMessage(byte[] msg)
	{
		MessageHeader messageHeader = CreateHeader(msg);
		return new DeleteMessage();
	}
	
	public static KillCursorsMessage ParseKillCursorsMessage(byte[] msg)
	{
		MessageHeader messageHeader = CreateHeader(msg);
		return new KillCursorsMessage();
	}
	
	private static byte[] byteSubarray(byte[] msg, int from, int count)
	{
		return Arrays.copyOfRange(msg, from, from + count - 1);
	}
	
	private static MessageHeader CreateHeader(byte[] msg)
	{
		 int messageLength = (byteArrayToInt(msg, 0));
		 int requestID = (byteArrayToInt(msg, 4));
		 int responceTo = (byteArrayToInt(msg, 8));
		 int opCode = (byteArrayToInt(msg, 12));	
		
		return new MessageHeader(messageLength, requestID, responceTo, opCode);
	}
	
	private static String byteArrayToString(byte[] b, int from)
	{
		int to = from;
		while(b[to] != 0)
		{
			to = to + 1;
			
			if(b.length <= to)
			{
				break;
				//b³ont, dodaæ wyj¹tek
			}
		}
		
		to = to - 1;
		
		String string = null;
		try
		{
			string = new String(Arrays.copyOfRange(b, from, to), "ANSI");
		}
		catch(Exception e)
		{
		//nic tu nie ma 
		}
		
		from = to + 2;
		
		return string;
	}
	
	private static int byteArrayToInt(byte[] b, int from)
	{
		return   b[from + 0] & 0xFF |
				(b[from + 1] & 0xFF) << 8 |
				(b[from + 2] & 0xFF) << 16 |
				(b[from + 3] & 0xFF) << 24;
	}

	private static int Byte(byte[] b)
	{
		return   b[0] & 0xFF |
				(b[1] & 0xFF) << 8 |
				(b[2] & 0xFF) << 16 |
				(b[3] & 0xFF) << 24;
	}
}
