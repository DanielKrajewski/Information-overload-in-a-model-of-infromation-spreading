package save;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import simulation.Message;

import java.io.File;

public class Save {
	protected File aFile;
	protected FileWriter writer;
	protected String path;
	
	//constructor
	public Save(String path) {
		this.path = path;
		createFile(path);
	}
	public void createFile(String path)
	{
		createTree(path);
		aFile = new File(path); 
		try {writer = new FileWriter(aFile);} 
		catch (IOException e) {e.printStackTrace();}
	}
	public static void createTree(String path) {
		String dir = "";
		String[] dirs = path.split("/");
		
		for(int i=0; i<dirs.length-1; i++) {
			dir += dirs[i];
			createDirectory(dir);
			dir += "/";
		}
	}

	// Creates single directory
	private static void createDirectory(String dir) {
		File theDir = new File(dir);
		if(!theDir.exists()) theDir.mkdirs();
	}
	
	//save the adj matrix to a csv file
	public void saveAdjMatrix(int[][] adjMatrix) {
		try {
			writer = new FileWriter(path);
			for (int i = 0; i < adjMatrix.length; i++) {
				for (int j = 0; j < adjMatrix[0].length; j++) {
					writer.append(adjMatrix[i][j] + ",");
				}
				writer.append("\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//save the initial opinions of nodes to a csv file
	
	public void saveOpinions(int[][] opinions) {
        try {
            writer = new FileWriter(path);
            for (int i = 0; i < opinions.length; i++) {
                for (int j = 0; j < opinions[0].length; j++) {
                    writer.append(opinions[i][j] + ",");
                }
                writer.append("\n");
            }
            writer.close();
            } catch (IOException e) {
            	
            	e.printStackTrace();
            	
            }
	}
	
	//save the random message to a csv file
	public void saveRandomMessage(Message randomMessage) {
		try {
			writer = new FileWriter(path);
			int[] messageContent = randomMessage.getMessageContent();
	        int[] messageIndexes = randomMessage.getMessageIndexes();
	        
	        String contentString = Arrays.toString(messageContent).replaceAll("[\\[\\] ]", "");
	        String indexesString = Arrays.toString(messageIndexes).replaceAll("[\\[\\] ]", "");

	        // Zapis do pliku CSV (każdy wiersz to jedna wiadomość)
	        writer.append(contentString).append("\n");
	        writer.append(indexesString).append("\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeData(int data) {
		try {
			if (!aFile.exists()) aFile.createNewFile(); // checking if file exists
	        writer.write(data + "");
	        }
	    catch (IOException e) {e.printStackTrace();}
	}
	
	// Writes double to file
	public void writeData(double data) {
		try {
			if (!aFile.exists()) aFile.createNewFile(); // checking if file exists
		    writer.write(data + "");
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	// Writes string to file
	public void writeData(String data) {
		try {
			if (!aFile.exists()) aFile.createNewFile(); // checking if file exists
			writer.write(data); 
		} 
		catch (IOException e) {e.printStackTrace();}
	}
	
	
	// Writes data with \t on the end
	public void writeDatatb(double data) {writeData(data + "\t");}
	public void writeDatatb(int data) {writeData(data + "\t");}
	public void writeDatatb(String data) {writeData(data + "\t");}
	
	// Writes data and goes to the next line
	public void writeDataln(int data) {writeData(data + "\n");}
	public void writeDataln(double data) {writeData(data + "\n");}
	public void writeDataln(String data) {writeData(data + "\n");}
	
	public void writeDatacomma(int data) {writeData(data + ",");}
	public void writeDatacomma(double data) {writeData(data + ",");}
	public void writeDatacomma(String data) {writeData(data + ",");}
	
	public void closeWriter()
	{
		try {writer.close();} 
		catch (IOException e) {	e.printStackTrace(); }
	}
	
}
	
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
