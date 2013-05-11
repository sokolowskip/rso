package messages;

public class MessageHeader {
	private int   messageLength; // total message size, including this
    private int   requestID;     // identifier for this message
    private int   responseTo;    // requestID from the original request
                           //   (used in reponses from db)
    private int   opCode;        // request type - see table below 
	
	public MessageHeader(int length, int id, int to, int op)
	{
		this.messageLength = length;
		this.requestID = id;
		this.responseTo = to;
		this.opCode = op;
	}
	
	public int getMessageLength()
	{
		return messageLength;
	}
	
	public int getRequestID()
	{
		return requestID;
	}
	
	public int getResponseTo()
	{
		return responseTo;
	}
	
	public int getOpCode()
	{
		return opCode;
	}
}
