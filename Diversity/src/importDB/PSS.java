package importDB;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import backend.Settings;

//Post individual Object
public class PSS {
	private HashMap<String,Integer> tags = new HashMap<String,Integer>();

	public PSS() {
	}

	public boolean tagexists(String word){
		return tags.containsKey(word);
	}
	
	public int getTag(String word){
		return tags.get(word);
	}
	
	public void importPSS(){
		BufferedReader inputFile = null;
		Vector<String> names = new Vector<String>();
		try {
        	inputFile = new BufferedReader( new FileReader( Settings.DATA_FOLDER + "/" + AppConst.FILENAME_PRODUCTS ) );
            String line = "";
            do {
                line = this.inputFile.readLine();
                if (line != null) {
                	names.add( line );
                }
            } while( line != null );
        }
        catch(IOException ex){
            System.out.println("File IO Exception: " + ex.getMessage());
        }
		this.closeInputFile();
		
	}
}
