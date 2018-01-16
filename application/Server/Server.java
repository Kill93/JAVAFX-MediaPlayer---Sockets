package application.Server;

/*  Killian Nolan -  R00129172 - DWEB4	 */

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.net.ServerSocket;

public class Server implements Observer{

	public static void main(String args[]) throws ClassNotFoundException {

		ServerSocket echoServer = null;
		String line;
		ObjectOutputStream oos;
		ObjectInputStream ois;
		Socket clientSocket = null;
		MonitorFolder monitorInstance = MonitorFolder.getInstance();
		ArrayList<String> list = monitorInstance.getNames();


		try {
			echoServer = new ServerSocket(3333);
		} catch (IOException e) {
			System.out.println(e);
		}

		System.out.println("The server started.");

		try {
			clientSocket = echoServer.accept();
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			ois = new ObjectInputStream(clientSocket.getInputStream()); 

			while (true) {	
				line = (String) ois.readObject();
				String first = line.substring(0, 1);
				Boolean serverChange = false;

				switch(first) {
				case "1" : 
					oos.writeObject(list);  
					oos.flush();
					break; 
				case "2" :
					String songName = line.substring(1);
					File someFile = new File("/Users/killiannolan/Documents/FinalYear/DistributedSysProgramming/Lab1-Sys/Folder1/" + songName);
					byte [] bytesOut = monitorInstance.getB(someFile);
					oos.writeObject(bytesOut);  
					oos.flush();
					break; 
				case "3" :
					String songNameIn = line.substring(1);
					byte [] bytesIn = (byte []) ois.readObject();
					monitorInstance.copyFile(bytesIn, songNameIn);
					break; 
				case "4" :
					Boolean bool = monitorInstance.checkForChange(list);
					list = monitorInstance.getNames();
					if (bool == true) {
						serverChange = true;
						oos.writeObject(serverChange);
						oos.writeObject(list);
						oos.flush();
					}
					else {
						serverChange = false;
						oos.writeObject(serverChange);
						oos.flush();
					}
					break;
				default:
					System.out.println("No Option selected");
					break;
				}
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		System.out.println("There's been a change detected on the " + arg + " songs list!");	
	}
}

