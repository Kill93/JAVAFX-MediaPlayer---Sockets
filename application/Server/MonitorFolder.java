package application.Server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;

import application.ListObserver;

/*  Killian Nolan -  R00129172 - DWEB4	 */

public class MonitorFolder extends Observable implements MonitorInterface {

	private static volatile MonitorFolder monitorInstance= new MonitorFolder();

	//private constructor.
	private MonitorFolder(){}

	public static MonitorFolder getInstance() {
		return monitorInstance;
	}

	private Boolean endf = false;
	private DataInputStream in;

	@Override
	public Boolean checkBool() {
		return endf;
	}

	@Override
	public ArrayList<String> getNames () {

		File aDirectory = new File("/Users/killiannolan/Documents/FinalYear/DistributedSysProgramming/Lab1-Sys/Folder1");

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

	@Override
	public Boolean openFile1(String name) {
		try {
			in = new DataInputStream(new BufferedInputStream(
					new FileInputStream(name)));
		} catch (IOException e ) {e.printStackTrace();
		}
		return true;
	}

	@Override
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

	@Override
	public void copyFile(byte[] bytes, String songName) throws IOException {
		File someFile = new File("/Users/killiannolan/Documents/FinalYear/DistributedSysProgramming/Lab1-Sys/Folder1/" + songName);
		FileOutputStream fos = new FileOutputStream(someFile);
		fos.write(bytes);
		fos.flush();
		fos.close();    
	}

	@Override
	public Boolean closeFile() {
		try {
			in.close();
		}
		catch (IOException e) 
		{e.printStackTrace(); return false;}
		return true;
	}

	public Boolean checkForChange(ArrayList <String> items) {

		Boolean bool;

		File aDirectory2 = new File("/Users/killiannolan/Documents/FinalYear/DistributedSysProgramming/Lab1-Sys/Folder1");
		String[] filesInDir2 = aDirectory2.list();

		ArrayList<String> ls2 = new ArrayList<String>(Arrays.asList());

		for ( int i=0; i<filesInDir2.length; i++ )
		{
			if(filesInDir2[i].endsWith(".mp3")){
				ls2.add(filesInDir2[i]);
			}
		}
		if (items.size() != ls2.size()) {
			String serverList="Server";
			ListObserver ListObserver =new ListObserver(serverList);
			Server serverClass =new Server();
			ListObserver.register(serverClass);
			ListObserver.notifyObserver();
			bool = true;
		}
		else {
			bool = false;
		}
		return bool;
	}

}
