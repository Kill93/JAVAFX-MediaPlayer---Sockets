package application.Client;


/*  Killian Nolan -  R00129172 - DWEB4	 */

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import application.ListObserver;
import javafx.collections.ObservableList;

public class MonitorLocal {

	private static volatile MonitorLocal localInstance= new MonitorLocal();

	//private constructor.
	private MonitorLocal(){}

	public static MonitorLocal getInstance() {
		return localInstance;
	}

	private DataInputStream in;

	public ArrayList<String> getNames () {

		File aDirectory = new File("/Users/killiannolan/Documents/FinalYear/DistributedSysProgramming/Lab1-Sys/Folder2");

		String[] filesInDir = aDirectory.list();
		ArrayList<String> files = new ArrayList<String>();

		for ( int i=0; i<filesInDir.length; i++ )
		{
			if(filesInDir[i].endsWith(".mp3")){
				files.add(filesInDir[i]);
			}
		}
		return files;
	}

	public ArrayList<String> differenceItems(ArrayList<String> itemsS) {

		File aDirectory1 = new File("/Users/killiannolan/Documents/FinalYear/DistributedSysProgramming/Lab1-Sys/Folder2");
		String[] filesInDir1 = aDirectory1.list();

		ArrayList<String> ls1 = new ArrayList<String>(Arrays.asList());

		for ( int i=0; i<filesInDir1.length; i++ )
		{
			if(filesInDir1[i].endsWith(".mp3")){
				ls1.add(filesInDir1[i]);
			}
		}

		// Remove all elements in firstList from secondList
		itemsS.removeAll(ls1);

		return itemsS;

	}

	public void copyFile(byte[] bytes, String songName) throws IOException {
		File someFile = new File("/Users/killiannolan/Documents/FinalYear/DistributedSysProgramming/Lab1-Sys/Folder2/" + songName);
		FileOutputStream fos = new FileOutputStream(someFile);
		fos.write(bytes);
		fos.flush();
		fos.close();

	}

	public byte [] getB( File file) {		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];

		try {
			for (int readNum; (readNum = fis.read(buf)) != -1;) {
				bos.write(buf, 0, readNum); 
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		byte[] bytes = bos.toByteArray();

		return bytes;
	}

	public Boolean closeFile() {
		try {
			in.close();
		}
		catch (IOException e) 
		{e.printStackTrace(); return false;}
		return true;
	}

	public Boolean checkForChange(ObservableList <String> items) {

		Boolean bool;

		File aDirectory2 = new File("/Users/killiannolan/Documents/FinalYear/DistributedSysProgramming/Lab1-Sys/Folder2");
		String[] filesInDir2 = aDirectory2.list();

		ArrayList<String> ls2 = new ArrayList<String>(Arrays.asList());

		for ( int i=0; i<filesInDir2.length; i++ )
		{
			if(filesInDir2[i].endsWith(".mp3")){
				ls2.add(filesInDir2[i]);
			}
		}
		if (items.size() != ls2.size()) {
			String clientList = "Local";
			ListObserver ListObserver =new ListObserver(clientList);
			MyMediaPlayer media =new MyMediaPlayer();
			ListObserver.register(media);
			ListObserver.notifyObserver();
			bool = true;
		}
		else {
			bool = false;
		}
		return bool;
	}


}
