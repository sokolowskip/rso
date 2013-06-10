package listener;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import messages.DeleteMessage;
import messages.GetMoreMessage;
import messages.InsertMessage;
import messages.KillCursorsMessage;
import messages.MessageHeader;
import messages.MessageParser;
import messages.QueryMessage;
import messages.ReplyMessage;
import messages.UpdateMessage;
import CRUD.Cursor;
import CRUD.CursorRegister;
import CRUD.Delete;
import CRUD.Insert;
import CRUD.Read;
import CRUD.Update;

/**
 * Klasa przyjmujaca wiadomosci od pojedynczego klienta. Instancje tej klasy
 * umieszczane sa w nowych watkach.
 * 
 */
public class ClientHandler implements Runnable {
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
		Thread handler = new Thread(this);
		handler.start();
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
			message[0] = (byte) (length);
			message[1] = (byte) (length >>> 8);
			message[2] = (byte) (length >>> 16);
			message[3] = (byte) (length >>> 24);
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
				//todo?
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
				InsertMessage insertMessage = MessageParser
				.ParseInsertMessage(message);
				Insert insert = new Insert();
				insert.insertDocumentList(insertMessage);				
				break;
			// OP_QUERY 2004 query a collection
			case OP_QUERY:
				// teraz trzeba cos dopowiedziec
				QueryMessage queryMessage = MessageParser
						.ParseQueryMessage(message);
				Long cursorId = CursorRegister.getNewCursorForQuery(queryMessage);
				Cursor queryCursor = CursorRegister.getCursor(cursorId);
				queryCursor.getMore(queryMessage.numberToSkip);
				ReplyMessage queryReply = queryCursor.getMore(queryMessage.numberToReturn);
				
				queryReply.header.responseTo = header.requestID;
				queryReply.header.requestID = generateRequestId();
				ByteBuffer queryBuffer = queryReply.getBuffer();
				transmit(queryBuffer.array(), clientSocket.getOutputStream());
				break;
				
			// OP_GET_MORE 2005 Get more data from a query. See Cursors
			case OP_GET_MORE:
				GetMoreMessage getMoreMessage= MessageParser
				.ParseGetMoreMessage(message);
				ReplyMessage getMoreReply;
				if(CursorRegister.checkIfCursorExists(getMoreMessage.cursorID))
				{
					Cursor getMoreCursor = CursorRegister.getCursor(getMoreMessage.cursorID);
					getMoreReply = getMoreCursor.getMore(getMoreMessage.numberToReturn);
					getMoreReply.header.responseTo = header.requestID;
					getMoreReply.header.requestID = generateRequestId();
					ByteBuffer getMoreBuffer = getMoreReply.getBuffer();
					transmit(getMoreBuffer.array(), clientSocket.getOutputStream());
				}
				else
				{
					getMoreReply = new ReplyMessage();
					//getMoreReply.responseFlags <- ustawic flage 1?
				}
				
			// OP_DELETE 2006 Delete documents
 			case OP_DELETE:
				DeleteMessage deleteMessage = MessageParser.ParseDeleteMessage(message);
				Delete delete = new Delete();
				delete.deleteDocument(deleteMessage);
				break;
			case OP_KILL_CURSORS:
				KillCursorsMessage killCursorsMessage = MessageParser.ParseKillCursorsMessage(message);
				long[] killCursorIds = killCursorsMessage.cursorIDs;
				for(int indexCursor = 0; indexCursor< killCursorIds.length; indexCursor++)
				{
					long killCursorId = killCursorIds[indexCursor];
					if(CursorRegister.checkIfCursorExists(killCursorId))
					{
						CursorRegister.deleteCursor(killCursorId);
					}
				}
				break;
			default:
				// b��d, trzeba wyrzuci� wyj�tek
				break;
			}


		} catch (IOException e) {
			return false;
		}
		return true;
	}

	private static int idNumber = 0;
	private static int generateRequestId() {
		// TODO Auto-generated method stub
		return idNumber++;
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
