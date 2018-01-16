package application.Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/*  Killian Nolan -  R00129172 - DWEB4	 */

public interface MonitorInterface {
	
	public Boolean checkBool(); // returns a bool
	public ArrayList<String> getNames(); // returns name of all files in folder 1
	public Boolean openFile1( String name); //opens a file called name
	public byte [] getB(File fileName); //gets a byte from currently opened file
	public Boolean closeFile(); // closes a file
	public void copyFile (byte[] bytes, String songName) throws FileNotFoundException, IOException;
}
