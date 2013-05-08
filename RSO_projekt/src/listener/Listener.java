package listener;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import bson.BSON;


import messages.MessageHeader;

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
			for (int i1 = 0, i2 = 15; i2 < temp.length - 16; i1++, i2++) {
				c[i1] = (char) temp[i2];
			}
			//ok, wyglada niezle. nie ma wiecej kodu wiec nie wiem jak dalej sprawdzac
			bizon.parseBSON(c);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}