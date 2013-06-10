package messages;

import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import bson.BSON;
import bson.BSONDocument;


public class ReplyMessage {

	public MessageHeader header;
	public int responseFlags;
	public long cursorID;
	public int startingFrom;
	public int numberReturned;
	public BSONDocument[] documents;
	
	public ByteBuffer getBuffer()
	{
		List<byte[]> documentsBytesList = new LinkedList<byte[]>();
		int documentsTotalLength = 0;
		
		for(int i=0; i<documents.length; i++)
		{
			byte[] bsonBytes = BSON.getBSON(documents[i]);
			documentsBytesList.add(bsonBytes);
			documentsTotalLength += bsonBytes.length;
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(36 + documentsTotalLength);
		
		buffer.order(ByteOrder.LITTLE_ENDIAN).putInt(documentsTotalLength);
		buffer.order(ByteOrder.LITTLE_ENDIAN).putInt(header.getRequestID());
		buffer.order(ByteOrder.LITTLE_ENDIAN).putInt(header.getResponseTo());
		buffer.order(ByteOrder.LITTLE_ENDIAN).putInt(header.getOpCode());

		buffer.order(ByteOrder.LITTLE_ENDIAN).putInt(responseFlags);
		buffer.order(ByteOrder.LITTLE_ENDIAN).putLong(cursorID);
		buffer.order(ByteOrder.LITTLE_ENDIAN).putInt(startingFrom);
		buffer.order(ByteOrder.LITTLE_ENDIAN).putInt(numberReturned);
		
		Iterator<byte[]> iterator = documentsBytesList.iterator();
		while(iterator.hasNext())
		{
			byte[] bsonBytes = iterator.next();
			buffer.put(bsonBytes);
		}
		
		return buffer;
		
	}
}
