package listener;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

import messages.MessageHeader;
import messages.MessageParser;
import messages.ReplyMessage;
import bson.BSON;

/**
 * Klasa przyjmujaca wiadomosci od pojedynczego klienta.
 * Instancje tej klasy umieszczane sa w nowych watkach.
 * 
 * @author Piotr Cebulski
 *
 */
public class ClientHandler extends Thread
{
	public Socket clientSocket;
	
	//tu bedzie umieszczana odebrana wiadomosc
	private static ByteBuffer messageBuffer;
	
	//Czasem w odpowiedzi trzeba wyslac
	//adres klienta z ktorego przyszlo zapytanie
	private static InetAddress remoteHostIP;
	private static int 		remoteHostPort;
	
	//konstruktor przyjmuje Listenera - moze pozniej sie to do czegos przyda
	public ClientHandler(Socket clientSocket, Listener listener) 
	{
		this.clientSocket = clientSocket;
		remoteHostIP = clientSocket.getInetAddress();
		remoteHostPort = clientSocket.getPort();
		this.start();
	}

	// Odbiera wiadomosci
    public void run() 
    {
    	while (accepted(clientSocket));
    	try 
    	{
			clientSocket.close();
		} 
    	catch (IOException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	
	/**
	 * Po kolei odczytuje bajty z otrzymanej wiadomosci
	 * Wrzuca je do messageBuffer
	 * Oddziela czesc naglowka i wrzuca ja do obiektu messages.MessageHeader
	 * Oddziela czesc dokumentu i wrzuca ja do obiektu bson.BSON
	 * Tworzy obiekt ReplyMessage i wysyla odpowiedz.
	 * 
	 * @param clientSocket
	 * @return True - gdy mamy cos w InputStream
	 * 		  False - gdy nie udal sie odczyt (np przerwane polaczenie)
	 */
	static boolean accepted(Socket clientSocket) 
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
					i++;
				}
				catch (EOFException e)
				{
					System.err.println("Incorrect message length. ");
					return false;
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
			//spoko, wszystko dziala
			
			MessageParser.MessageType messageType = MessageParser.getType(messageBuffer.array());
			System.out.println("Received: " + messageType.name());
			
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
			// TODO ok, wyglada niezle. nie ma wiecej kodu wiec nie wiem jak dalej sprawdzac
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
			return false;
		}
	return true;
	}

	/**
	 * Wysyla odpowiedz.
	 * 
	 * @param bytes
	 * @param outputStream
	 */
	private static void transmit(byte[] bytes, OutputStream outputStream) 
 {
		try {
			outputStream.write(bytes);
		} catch (IOException e) {
			System.err.println("Transmition failed.");
			e.printStackTrace();
		}
	}

	public static InetAddress getRemoteHostIP() {
		return remoteHostIP;
	}

	public static int getRemoteHostPort() {
		return remoteHostPort;
	}

	
	
}
