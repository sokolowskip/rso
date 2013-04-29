
public class MessageHeader {
	private Int   messageLength; // total message size, including this
    private Int   requestID;     // identifier for this message
    private Int   responseTo;    // requestID from the original request
                           //   (used in reponses from db)
    private Int   opCode;        // request type - see table below 
	
	MessageHeader(Int length, Int id, Int to, Int op)
	{
		this.messageLength = length;
		this.requestID = id;
		this.responceTo = to;
		this.opCode = op;
	}
	
	public Int getMessageLength()
	{
		return messageLength;
	}
	
	public Int getRequestID()
	{
		return requestID;
	}
	
	public Int getResponseTo()
	{
		return responseTo;
	}
	
	public Int getOpCode()
	{
		return opCode;
	}
}
