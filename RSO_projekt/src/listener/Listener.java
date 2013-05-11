package listener;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import bson.BSON;


import messages.MessageHeader;
import messages.MessageParser;
import messages.ReplyMessage;

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
	private static int port;
	private static ServerSocket serverSocket;
	
	//Czasem w odpowiedzi trzeba wyslac
	//adres klienta z ktorego przyszlo zapytanie
	private static InetAddress remoteHostIP;
	private static int 		remoteHostPort;
	
	//tu bedzie umieszczana odebrana wiadomosc
	private static ByteBuffer messageBuffer;
	
	Listener(String name, String ip, int _port) 
	{
//		_name = name;
//		_ip = ip;
		port = _port;
	}

	public static void main(String[] args) throws IOException
	{
		if (setupSocket(port))
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
			   remoteHostIP = clientSocket.getInetAddress();
			   remoteHostPort = clientSocket.getPort();
			   accepted(clientSocket);
		   }
		   catch (IOException e)
		   {
				System.err.println("Accept failed.");
				System.exit(1);
		   }
	   }
	}
	

	/**
	 * Po kolei odczytuje bajty z otrzymanej wiadomosci
	 * Wrzuca je do messageBuffer
	 * Oddziela czesc naglowka i wrzuca ja do obiektu messages.MessageHeader
	 * Oddziela czesc dokumentu i wrzuca ja do obiektu bson.BSON
	 * Tworzy obiekt ReplyMessage i w razie potrzeby wysyla odpowiedz.
	 * 
	 * @param clientSocket
	 */
	static void accepted(Socket clientSocket) 
	{
		try 
		{
			DataInputStream in = new DataInputStream(clientSocket.getInputStream());
			byte[] message = new byte[in.readUnsignedByte()];
			int length = message.length;
			message[0] = (byte) length;
			int i = 1;
			//wczytujemy po kolei bajty z socketa 
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
					System.err.println("Incorrect message length. ");
					break;
				}
			}
			
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
			
			MessageParser.MessageType messageType = MessageParser.getType(messageBuffer.array());
			
			switch(messageType)
			{
				//OP_REPLY 	1 	Reply to a client request. responseTo is set
				case OP_REPLY:
					@SuppressWarnings("unused")
					ReplyMessage replyMessage = MessageParser.ParseReplyMessage(messageBuffer.array());
					//analogicznie poni�ej
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
					//b��d, trzeba wyrzuci� wyj�tek
					break;
			}
			
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
			
			//teraz trzeba cos dopowiedziec
			//konstruktor przyjmuje oryginalny naglowek i dokument
			//o to co trzeba odpowiedziec martwimy sie gdzie indziej
			Response response = new Response(header, bizon);
			if (response.createResponse())
				transmit(response.getBytes(), clientSocket.getOutputStream());
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Wysyla odpowiedz.
	 * 
	 * @param bytes
	 * @param outputStream
	 */
	private static void transmit(byte[] bytes, OutputStream outputStream) 
	{
		for (byte b : bytes) 
		{
			try {
				outputStream.write(b);
			} catch (IOException e) {
				System.err.println("Transmition failed.");
				e.printStackTrace();
			}
		}		
	}

	public InetAddress getRemoteHostIP() {
		return remoteHostIP;
	}

	public int getRemoteHostPort() {
		return remoteHostPort;
	}

}