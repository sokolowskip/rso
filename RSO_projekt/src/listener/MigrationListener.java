package listener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;


public class MigrationListener implements Runnable {

	private static int port;
	private static ServerSocket serverSocket;
	
	ArrayList<String> migrate;
	InetAddress to;
	
	private static InetAddress listenerIP;

	public MigrationListener(int _port)
	{
		port = _port;
		//trzeba ogarnac jakiego interfejsu uzyc
	    try {
	        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
	        while (interfaces.hasMoreElements()) {
	            NetworkInterface iface = interfaces.nextElement();
	            //sprawdzamy ktory NIC jest uruchomiony
	            if (iface.isLoopback() || !iface.isUp())
	                continue;
	            Enumeration<InetAddress> addresses = iface.getInetAddresses();
	            while(addresses.hasMoreElements()) {
	                InetAddress addr = addresses.nextElement();
	                //pomijamy adresy IPv6
	                if (addr instanceof Inet6Address) continue;
	                listenerIP = addr;
	                System.out.println("MigrationListener on: " + iface.getDisplayName() + " " + listenerIP + ":" + port);
	            }
	        }
	    } catch (SocketException e) {
	        throw new RuntimeException(e);
	    }
	    //odpalamy nasluchiwacz
		Thread t = new Thread(this);
		t.start();
	}
	
	public void run() 
	{
		//robimy server socketa
		try {
		    serverSocket = new ServerSocket(port, 0, listenerIP);
		} 
		catch (IOException e) {
		    System.out.println("Could not listen on port: " + port);
		}
		
		while (true)
		{
			Socket clientSocket = null;
			//czekamy az ktos sie podlaczy
			try {
			    clientSocket = serverSocket.accept();
			} 
			catch (IOException e) {
			    System.out.println("Accept failed");
			    System.exit(-1);
			}
			
			//Tutaj odbieramy albo wiadomosc od serwera konfiguracyjnego
			//albo plik od ktorego sharda
			BufferedReader in;
			try {
				InputStream is = clientSocket.getInputStream();
				in = new BufferedReader(new InputStreamReader(is));
				String inputLine;
				inputLine = in.readLine();
				//dostajemy nowy plik
				if (inputLine.equals("COFFEE"))
				{
					//nazwa pliku
					inputLine = in.readLine();
					//otwieramy nowy plik
					String collection[], document[];
					collection = inputLine.split("[^\\/]*$");
					document = inputLine.split("^.*\\/");
					//robimy folder z nazwa kolekcji na wypadek gdyby wczesniej go nie bylo
					File newDir = new File(System.getProperty("user.home") + "/exampleDB/" + collection[0]);
					newDir.mkdir();
					//robimy nowy dokument
					File newFile = new File(newDir, document[1]);
					newFile.createNewFile();
					//dlugosc pliku
					inputLine = in.readLine();
					int length = Integer.parseInt(inputLine);;
					//lykamy dokumenta
					byte []buffer = new byte[(int) length];
					is.read(buffer, 0, length);
					//i zapisujemy do pliku
					FileOutputStream fos = new FileOutputStream(newFile);
					fos.write(buffer);
					fos.close();
					
					in.close();
				}
				//dostajemy MigrationInfo od ConfigServera
				else if (inputLine.equals("CIGARETTES"))
				{
					//adres serwera do ktorego bedziemy wysylac
					inputLine = in.readLine();
					to = InetAddress.getByName(inputLine);
					//wczytujemy kolejne nazwy plikow
					migrate = new ArrayList<String>();
					while ((inputLine = in.readLine()) != null) {
						if (inputLine.equals("Bye."))
							break;
						migrate.add(inputLine);
					}
					//to musimy teraz wysalac to co nam kazano
					sendFiles();
				}
				is.close();
				clientSocket.close();
			} catch (IOException e) {
				System.err.println("IOException: " + e);
				e.printStackTrace();
			}
		}
	}

	private void sendFiles() throws IOException {
	    Socket sock = null;
	    
		int i = 0;
		while (i  < migrate.size())
		{
			OutputStream os = null;
			PrintWriter out = null;
			//otwieramy socketa
			try {
				sock = new Socket(to, 28017);
				os = sock.getOutputStream();
				out = new PrintWriter(os, true);
			} catch (UnknownHostException e) {
	            System.err.println("Cannot connect to shard: " + to);
	        } catch (IOException e) {
	            System.err.println("IOExeption: " + e);
	        }

			
			
			File file = new File(System.getProperty("user.home") + "/exampleDB/" + migrate.get(i));
			//wyciagamy plik do bufora
			byte []buffer = new byte[(int) file.length()];
			InputStream ios = null;
			try {
				ios = new FileInputStream(file);
				if ( ios.read(buffer) == -1 ) {
				throw new IOException("EOF reached while trying to read the whole file");
			}        
			} finally { 
			    try {
			         if ( ios != null ) 
			              ios.close();
			    } catch ( IOException e) {
			    }
			}
  			
			//wysylamy kawe
			out.println("COFFEE");
			//nazwe pliku
			out.println(migrate.get(i));
			//jego dlugosc
			out.println(buffer.length);
			//i jego zawartosc
			os.write(buffer);
			out.close();

			//usuwamy plik z kolejki plikow do wyslania
			migrate.remove(i);
			//a na koncu wywalamy przeslany plik
			file.delete();
		}
	}		
}
