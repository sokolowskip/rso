package listener;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

import messages.DeleteMessage;
import messages.InsertMessage;
import messages.MessageHeader;
import messages.MessageParser;
import messages.ReplyMessage;
import messages.UpdateMessage;
import CRUD.Delete;
import CRUD.FileOperations;
import CRUD.Insert;
import CRUD.Update;
import bson.BSON;
import bson.BSONDocument;

/**
 * Klasa przyjmujaca wiadomosci od pojedynczego klienta. Instancje tej klasy
 * umieszczane sa w nowych watkach.
 * 
 */
public class ClientHandler extends Thread {
	public Socket clientSocket;

	// tu bedzie umieszczana odebrana wiadomosc
	private static ByteBuffer messageBuffer;

	// Czasem w odpowiedzi trzeba wyslac
	// adres klienta z ktorego przyszlo zapytanie
	private static InetAddress remoteHostIP;
	private static int remoteHostPort;

	// konstruktor przyjmuje Listenera - moze pozniej sie to do czegos przyda
	public ClientHandler(Socket clientSocket, Listener listener) {
		this.clientSocket = clientSocket;
		remoteHostIP = clientSocket.getInetAddress();
		remoteHostPort = clientSocket.getPort();
		this.start();
	}

	// Odbiera wiadomosci
	public void run() {
		while (accepted(clientSocket))
			;
		try {
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Po kolei odczytuje bajty z otrzymanej wiadomosci Wrzuca je do
	 * messageBuffer Oddziela czesc naglowka i wrzuca ja do obiektu
	 * messages.MessageHeader Oddziela czesc dokumentu i wrzuca ja do obiektu
	 * bson.BSON Tworzy obiekt ReplyMessage i wysyla odpowiedz.
	 * 
	 * @param clientSocket
	 * @return True - gdy mamy cos w InputStream False - gdy nie udal sie odczyt
	 *         (np przerwane polaczenie)
	 */
	static boolean accepted(Socket clientSocket) {
		try {
			DataInputStream in = new DataInputStream(
					clientSocket.getInputStream());
			// dlugosc to int (dzieki Mateusz)
			int length = Integer.reverseBytes(in.readInt());
			byte[] message = new byte[length];
			message[0] = (byte) (length >>> 24);
			message[1] = (byte) (length >>> 16);
			message[2] = (byte) (length >>> 8);
			message[3] = (byte) (length);
			// czyli lyknelismy juz 4 bajty
			int i = 4;
			// wczytujemy po kolei bajty z socketa
			while (i != length) {
				try {
					message[i] = (byte) in.readUnsignedByte();
					i++;
				} catch (EOFException e) {
					System.err.println("Incorrect message length. ");
					return false;
				}
			}

			// zamieniamy na obiekt ByteBuffer - podobno jest sprytniejszy
			messageBuffer = ByteBuffer.wrap(message);
			// na razie sam naglowek
			MessageHeader header = new MessageHeader(
					Integer.reverseBytes(messageBuffer.getInt(0)),
					Integer.reverseBytes(messageBuffer.getInt(4)),
					Integer.reverseBytes(messageBuffer.getInt(8)),
					Integer.reverseBytes(messageBuffer.getInt(12)));
			// spoko, wszystko dziala

			MessageParser.MessageType messageType = MessageParser
					.getType(message);
			System.out.println("Received: " + messageType.name());

			switch (messageType) {
			// OP_REPLY 1 Reply to a client request. responseTo is set
			case OP_REPLY:
				@SuppressWarnings("unused")
				ReplyMessage replyMessage = MessageParser
						.ParseReplyMessage(message);
				// analogicznie ponizej
				break;
			// OP_MSG 1000 generic msg command followed by a string
			case OP_MSG:
				break;
			// OP_UPDATE 2001 update document
			case OP_UPDATE:
				UpdateMessage updateMessage = MessageParser.ParseUpdateMessage(message);
				Update update = new Update();
				update.updateDocument(updateMessage);
				break;
			// OP_INSERT 2002 insert new document
			case OP_INSERT:
				try {
					FileOutputStream fos = new FileOutputStream("testoweBsony/2");
					fos.write(message);
					fos.close();
				} catch (FileNotFoundException ex) {
					System.out.println("FileNotFoundException : " + ex);
				} catch (IOException ioe) {
					System.out.println("IOException : " + ioe);
				}
				InsertMessage insertMessage = MessageParser
						.ParseInsertMessage(message);
				Insert insert = new Insert();
				insert.insertDocumentList(insertMessage);
				break;
			// OP_QUERY 2004 query a collection
			case OP_QUERY:
				// teraz trzeba cos dopowiedziec
				Response response = new Response(header);
				if (response.createResponse())
					transmit(response.getBytes(), clientSocket.getOutputStream());
				break;
			// OP_GET_MORE 2005 Get more data from a query. See Cursors
			case OP_GET_MORE:
				break;
			// OP_DELETE 2006 Delete documents
			case OP_DELETE:
				DeleteMessage deleteMessage = MessageParser.ParseDeleteMessage(message);
				Delete delete = new Delete();
				delete.deleteDocument(deleteMessage);
				break;
			case OP_KILL_CURSORS:
				break;
			default:
				// b��d, trzeba wyrzuci� wyj�tek
				break;
			}

//			// reszta leci do obiektu bizona
//			BSONDocument bizon = new BSONDocument();
//
//			int strLen = 0;
//			for (int j = 20; j < message.length; j++)
//				if (message[j] == 0) {
//					strLen = j - 20;
//					break;
//				}
//
//			String collectionName = "";
//			try {
//				collectionName = new String(message, 20, strLen, "UTF-8");
//			} catch (UnsupportedEncodingException e) {
//			}
//			;
//
//			byte[] c = Arrays.copyOfRange(message, strLen + 21, message.length);
//
//			// TODO ok, wyglada niezle. nie ma wiecej kodu wiec nie wiem jak
//			// dalej sprawdzac
//			BSON.parseBSON(c, bizon);


		} catch (IOException e) {
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
	private static void transmit(byte[] bytes, OutputStream outputStream) {
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
