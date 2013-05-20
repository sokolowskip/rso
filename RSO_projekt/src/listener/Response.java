package listener;

import java.nio.ByteBuffer;

import messages.MessageHeader;
import bson.BSONDocument;

/**
 * Ta klasa bedzie laczyla dokument z naglowkiem. Zmienna response przechowuje
 * tablice bajtow gotowa do wyslania.
 * 
 * **Na razie beda tutaj przygotowywane fake'owe odpowiedzi do podlaczania
 * shella mongo.
 * 
 * @author Piotr Cebulski
 * 
 */
public class Response {
	private MessageHeader header;
	@SuppressWarnings("unused")
	private BSONDocument bizon;

	private ByteBuffer response;

	public Response(MessageHeader _header, BSONDocument _bizon) {
		header = _header;
		bizon = _bizon;
	}

	public boolean createResponse() {
		switch (header.getRequestID()) {
		case 0:
			connectionInitiationResponse(0);
			return true;
		case 1:
			connectionInitiationResponse(1);
			return true;
		case 2:
			connectionInitiationResponse(2);
			return true;
		default: 
			response = ByteBuffer.wrap(fakeInsertResponse);
			response.putInt(4, Integer.reverseBytes(header.getRequestID()));
			response.putInt(8, Integer.reverseBytes(header.getRequestID() + 1));
			return true;
		}
	}

	private void connectionInitiationResponse(int i) {
		switch (i) {
		case 0: // Pierwsze zapytanie klienta to: whatsmyuri
				// a wiec trzeba w odpowienie miejsce wlozyc
				// adres i port remoteHosta.
			byte[] ip = ClientHandler.getRemoteHostIP().getHostAddress()
					.getBytes();
			byte[] port = Integer.toString(ClientHandler.getRemoteHostPort())
					.getBytes();
			response = ByteBuffer.allocate(fakeResponse0a.length + ip.length
					+ 1// +1 na srednik
					+ port.length + fakeResponse0b.length);
			response.put(fakeResponse0a);
			response.put(ip);
			response.put((byte) 0x3a);// srednik
			response.put(port);
			response.put(fakeResponse0b);
			// jeszcze trezba poprawic dlugsc
			response.putInt(0, Integer.reverseBytes(response.array().length));
			break;
		case 1:
			response = ByteBuffer.wrap(fakeResponse1);
			break;
		case 2:
			response = ByteBuffer.wrap(fakeResponse2);
			break;
		default:
			break;
		}
	}

	public byte[] getBytes() {
		return response.array();
	}

	final byte[] fakeResponse0a = new byte[] { 0x4e, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00,
			0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x2a, 0x00,
			0x00, 0x00, 0x02, 0x79, 0x6f, 0x75, 0x00, 0x10, 0x00, 0x00, 0x00 };
	// Tu laduje URI
	final byte[] fakeResponse0b = new byte[] { 0x00, 0x01, 0x6f, 0x6b, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xf0, 0x3f, 0x00 };

	final byte[] fakeResponse1 = new byte[] { 0x56, 0x00, 0x00, 0x00, 0x01,
			0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00,
			0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x32, 0x00,
			0x00, 0x00, 0x10, 0x74, 0x6f, 0x74, 0x61, 0x6c, 0x4c, 0x69, 0x6e,
			0x65, 0x73, 0x57, 0x72, 0x69, 0x74, 0x74, 0x65, 0x6e, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x04, 0x6c, 0x6f, 0x67, 0x00, 0x05, 0x00, 0x00,
			0x00, 0x00, 0x01, 0x6f, 0x6b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, (byte) 0xf0, 0x3f, 0x00 };

	final byte[] fakeResponse2 = new byte[] { 0x5c, 0x00, 0x00, 0x00, 0x02,
			0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00,
			0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x38, 0x00,
			0x00, 0x00, 0x01, 0x6f, 0x6b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x02, 0x65, 0x72, 0x72, 0x6d, 0x73, 0x67, 0x00,
			0x1b, 0x00, 0x00, 0x00, 0x6e, 0x6f, 0x74, 0x20, 0x72, 0x75, 0x6e,
			0x6e, 0x69, 0x6e, 0x67, 0x20, 0x77, 0x69, 0x74, 0x68, 0x20, 0x2d,
			0x2d, 0x72, 0x65, 0x70, 0x6c, 0x53, 0x65, 0x74, 0x00, 0x00 };

	final byte[] fakeInsertResponse = new byte[] { 0x53, 0x00, 0x00, 0x00,
			0x03, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00,
			0x00, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x2f,
			0x00, 0x00, 0x00, 0x10, 0x6e, 0x00, 0x00, 0x00, 0x00, 0x00, 0x10,
			0x63, 0x6f, 0x6e, 0x6e, 0x65, 0x63, 0x74, 0x69, 0x6f, 0x6e, 0x49,
			0x64, 0x00, 0x01, 0x00, 0x00, 0x00, 0x0a, 0x65, 0x72, 0x72, 0x00,
			0x01, 0x6f, 0x6b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			(byte) 0xf0, 0x3f, 0x00 };

}