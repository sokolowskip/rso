package listener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
		Thread t = new Thread(this);
		t.start();
	}
	
	public void run() 
	{
		try {
		    serverSocket = new ServerSocket(port, 0, listenerIP);
		} 
		catch (IOException e) {
		    System.out.println("Could not listen on port: " + port);
		}
		
		while (true)
		{
			Socket clientSocket = null;
			try {
			    clientSocket = serverSocket.accept();
			} 
			catch (IOException e) {
			    System.out.println("Accept failed");
			    System.exit(-1);
			}
			
			BufferedReader in;
			try {
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String inputLine;
				inputLine = in.readLine();
				//dostajemy nowy plik
				if (inputLine.equals("COFFEE"))
				{
					//nazwa pliku
					inputLine = in.readLine();
					//otwieramy nowy plik
					File newFile = new File(System.getProperty("user.home") + "/exampleDB/" + inputLine);
					newFile.mkdirs();
				    FileOutputStream fos = new FileOutputStream(newFile);
				    //dlugosc pliku
					inputLine = in.readLine();
					int length = Integer.parseInt(inputLine);
					int bytesRead;
					int current = 0;
					byte [] mybytearray  = new byte[length];
				    InputStream is = clientSocket.getInputStream();
				    BufferedOutputStream bos = new BufferedOutputStream(fos);
				    bytesRead = is.read(mybytearray, 0, length);
				    current = bytesRead;
				    //czytamy bajty
				    do {
				       bytesRead =
				          is.read(mybytearray, current, (mybytearray.length-current));
				       if(bytesRead >= 0) current += bytesRead;
				    } while(bytesRead > -1);
				    bos.write(mybytearray, 0 , current);
				    //zrzucamy do pliku i go zamykamy
				    bos.flush();
				    bos.close();
				}
				//dostajemy MigrationInfo
				else
				{
					to = InetAddress.getByName(inputLine);
					migrate = new ArrayList<String>();
					while ((inputLine = in.readLine()) != null) {
						if (inputLine.equals("Bye."))
							break;
						migrate.add(inputLine);
					}
					//to musimy teraz wysalac to co nam kazano
					sendFiles();
				}
				in.close();
				clientSocket.close();
			} catch (IOException e) {
				System.err.println("IOException: " + e);
				e.printStackTrace();
			}
		}
	}

	private void sendFiles() {
		Socket  sock = null;
		PrintWriter out = null;
		//otwieramy socketa
		try {
			int i = 0;
			while (i  < migrate.size())
			{
				sock = new Socket(to, 28017);
				out = new PrintWriter(sock.getOutputStream(), true);
				out.println("COFFEE");
				out.println(migrate.get(i));
				System.out.println(migrate.get(i));
				File myFile = new File(System.getProperty("user.home") + "/exampleDB/" + migrate.get(i));
				//usuwamy z kolejki plikow do wyslania
				migrate.remove(i);
				byte [] mybytearray  = new byte [(int)myFile.length()];
				FileInputStream fis = new FileInputStream(myFile);
				BufferedInputStream bis = new BufferedInputStream(fis);
				bis.read(mybytearray,0,mybytearray.length);
				OutputStream os = sock.getOutputStream();
				os.write(mybytearray,0,mybytearray.length);
				os.flush();
				bis.close();
				sock.close();
				i++;
			}
		} catch (UnknownHostException e) {
            System.err.println("Cannot connect to shard: " + to.getHostAddress());
        } catch (IOException e) {
            System.err.println("IOExeption: " + e);
        }


	}
}
