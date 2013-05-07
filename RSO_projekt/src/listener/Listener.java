package listener;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import messages.GenericMessage;

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
			byte[] messageBuffer = new byte[in.readUnsignedByte()];
			int length = messageBuffer.length;
			messageBuffer[0] = (byte) length;
			int i = 1;
			while (i != length)
			{
				try
				{
					messageBuffer[i] = (byte) in.readUnsignedByte();
					System.out.print(Integer.toHexString(messageBuffer[i] & 0xff) + " ");
//					System.out.print(Integer.toHexString((in.readUnsignedByte())) + " ");
					i++;
				}
				catch (EOFException e)
				{
					System.err.println("Incorrect document length. ");
					break;
				}
			}
			// TODO Tutaj trzeba pewnie bedzie stworzyc obiekt klasy GenericMessage od Mateusza. 
			@SuppressWarnings("unused")
			GenericMessage newMessage = new GenericMessage();
			// i co dalej ???
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}