package listener;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import bson.BSON;

<<<<<<< HEAD
import Messages.MessageParser;
import Messages.*;
=======

import messages.MessageHeader;
>>>>>>> 8c0b8eaeb84b84a5867658c23f563a0b49dfcd04

/**
 * Klasa nasluchiwacza. 
 * Tworzy socket nasluchujacy na porcie 27017.
 * Przyjmuje wiadomosci.
 * Odczytuje jej dlugosc i wczytuje odpowiednia ilosc bajtow.
 * @author Piotr Cebulski
 *
 */
public class Listener {

//	private String _name;
//	private String _ip;
	static int _port;
	private static ServerSocket serverSocket;
	
	//tu bedzie umieszczana odebrana wiadomosc
	private static ByteBuffer messageBuffer;
	
	Listener(String name, String ip, int port) 
	{
//		_name = name;
//		_ip = ip;
		_port = port;
	}

	public static void main(String[] args) throws IOException
	{
		if (setupSocket(_port))
		{
			initAndListen();
		}
	}
	
	
	/**
	 * Tworzy gniazdo.
	 * @param addr
	 * @param port
	 * @return
	 * @throws IOException 
	 */
	static boolean setupSocket(int port)
	{
		 serverSocket = null;
		   try {
			serverSocket = new ServerSocket(27017);
		   } catch (IOException e) {
		       System.err.println("Could not listen on port: " + 27017);
		       System.exit(1);
		   }
		   return true;
	}

	/**
	 * Startuje nowy watek z nasluchiwaniem.
	 */
	static void initAndListen()
	{
	   while (true)
	   {	   
		   Socket clientSocket = null;
		   try
		   {
			   clientSocket = serverSocket.accept();
			   accepted(clientSocket);
		   }
		   catch (IOException e)
		   {
				System.err.println("Accept failed.");
				System.exit(1);
		   }
	   }
	}
	

	
	static void accepted(Socket clientSocket) 
	{
		try 
		{
			DataInputStream in = new DataInputStream(clientSocket.getInputStream());
			byte[] message = new byte[in.readUnsignedByte()];
			int length = message.length;
			message[0] = (byte) length;
			int i = 1;
			while (i != length)
			{
				try
				{
					message[i] = (byte) in.readUnsignedByte();
//					System.out.print(Integer.toHexString(message[i] & 0xff) + " ");
//					System.out.print(Integer.toHexString((in.readUnsignedByte())) + " ");
					i++;
				}
				catch (EOFException e)
				{
					System.err.println("Incorrect document length. ");
					break;
				}
			}
<<<<<<< HEAD
			// TODO Tutaj trzeba pewnie bedzie stworzyc obiekt klasy GenericMessage od Mateusza. 
			@SuppressWarnings("unused")

			MessageParser.MessageType messageType = MessageParser.getType(messageBuffer);
			
			switch(messageType)
			{
				//OP_REPLY 	1 	Reply to a client request. responseTo is set
				case OP_REPLY:
					ReplyMessage replyMessage = MessageParser.ParseReplyMessage(messageBuffer);
					//analogicznie poni¿ej
					break;
				//OP_MSG 	1000 	generic msg command followed by a string
				case OP_MSG:
					break;
				//OP_UPDATE 	2001 	update document
				case OP_UPDATE:
					break;
				//OP_INSERT 	2002 	insert new document
				case OP_INSERT:
					break;
				//OP_QUERY 	2004 	query a collection
				case OP_QUERY:
					break;
				//OP_GET_MORE 	2005 	Get more data from a query. See Cursors
				case OP_GET_MORE:
					break;
				//OP_DELETE 	2006 	Delete documents
				case OP_DELETE:
					break;
				//OP_KILL_CURSORS 	2007 	Tell database client is done with a cursor
				case OP_KILL_CURSORS:
					break;
				default:
					//b³¹d, trzeba wyrzuciæ wyj¹tek
					break;
			}
=======
			//zamieniamy na obiekt ByteBuffer - podobno jest sprytniejszy
			messageBuffer = ByteBuffer.wrap(message);
			//na razie sam naglowek
			MessageHeader header = new MessageHeader(
					Integer.reverseBytes(messageBuffer.getInt(0)),
					Integer.reverseBytes(messageBuffer.getInt(4)),
					Integer.reverseBytes(messageBuffer.getInt(8)),
					Integer.reverseBytes(messageBuffer.getInt(12)));
			System.out.println("length: " + header.getMessageLength());
			System.out.println("rqst: " + header.getRequestID());
			System.out.println("resp: " + header.getResponseTo());
			System.out.println("opcode: " + header.getOpCode());
			//spoko, wszystko dziala
			
			//reszta leci do obiektu bizona
			BSON bizon = new BSON();
			//ale parseBSON przyjmuje tablice char wiec konwertujemy
			byte[] temp = messageBuffer.array();
			//pomijamy jeszcze naglowek
			char[] c = new char[temp.length - 16];
			for (int i1 = 0, i2 = 16; i1 < temp.length - 16; i1++, i2++) {
				c[i1] = (char) temp[i2];
			}
			//ok, wyglada niezle. nie ma wiecej kodu wiec nie wiem jak dalej sprawdzac
			//System.out.println(c.length);
			bizon.parseBSON(c);
>>>>>>> 8c0b8eaeb84b84a5867658c23f563a0b49dfcd04
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}