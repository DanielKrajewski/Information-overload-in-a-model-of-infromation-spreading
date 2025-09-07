package simulation;

public class Message {
	// Content of the message and indexes
	public int[][] content;
		
	// Time of receiving the message
	public int time;
		
	// Id of this particular information
	public int id;
		
	public Message(int[][] content, int time, int id) {
		if(content.length != 2)
			throw new Error("Array with content contains more than 2 arrays,");
		if(content[0].length != content[1].length)
			throw new Error("Lenght of the message content does not match lengt of the index array.");
		for(int msg : content[0])
			if(Math.abs(msg) != 1 && msg !=0)
				throw new Error("Message must contain only -1, 0 or 1.");
				
		this.id = id;
		this.content = content.clone();
		this.time = time;
		}

	public void setId(int newId) {id = newId;}
	public int[] getMessageContent() {return content[0];}
	public void printMessageContent() {
		for (int i = 0; i < content[0].length; i++)
			if (content[0][i] < 0)
				System.out.print(content[0][i] + "  ");
			else
			System.out.print(" "+ content[0][i] + "  ");
		System.out.println();
	}
	public int[] getMessageIndexes() {return content[1];}

	public void printMessageIndexes() {
		for (int i = 0; i < content[1].length; i++)
			System.out.print(" " + content[1][i] + "  ");
		System.out.println();
	}
	public int[][] getMessageContentAndIndexes() {return content;}
	public int getTime() {return time;}
	public int getId() {return id;}

}
