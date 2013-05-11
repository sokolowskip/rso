package listener;

import java.nio.ByteBuffer;

import messages.MessageHeader;
import bson.BSON;

/**
 * Ta klasa bedzie laczyla dokument z naglowkiem.
 * Zmienna response przechowuje tablice bajtow gotowa do wyslania.
 * 
 * **Na razie beda tutaj przygotowywane fake'owe odpowiedzi
 *   do podlaczania shella mongo.
 * 
 * @author Piotr Cebulski
 *
 */
public class Response {

	private MessageHeader header;
	@SuppressWarnings("unused")
	private BSON bizon;
	
	private ByteBuffer response;
	
	public Response(MessageHeader _header, BSON _bizon) 
	{
		header = _header;
		bizon = _bizon;
	}

	public boolean createResponse() 
	{
		switch(header.getRequestID())
		{
		case 0: connectionInitiationResponse(0);
				return true;
		case 1: connectionInitiationResponse(1);
				return true;
		case 2: connectionInitiationResponse(2);
				return true;
		default:return false; 
		}
	}

	private void connectionInitiationResponse(int i) 
	{
	
		
	}

	public byte[] getBytes() 
	{
		return response.array();
	}

}
