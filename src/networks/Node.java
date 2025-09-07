package networks;

import java.util.ArrayList;

import simulation.Message;

public class Node {
	
	//list of links that are connected to this node
	private ArrayList<Link> links;
	
	private int[] Opinion;
	private double cosineThreshold;
	
	// Messages that this node shared
	private ArrayList<Message> messages_shared;
		
	// Messages that will appear on this node's queue
	private ArrayList<Message> queue;
	
	// ID's of shared messages
	private ArrayList<Integer> ids;
		
	//constructor
	public Node(int[] Opinion) {
		// Errors and exceptions
		for(double opinion : Opinion) {
			if(Math.abs(opinion) != 1 && opinion != 0) {
				throw new Error("Node opinion must be equal -1, 0 or 1.");
			}
		}
		this.Opinion = Opinion;
		links = new ArrayList<Link>();
		messages_shared = new ArrayList<Message>();
		queue = new ArrayList<Message>();
		cosineThreshold = -1;
		ids = new ArrayList<Integer>();
		
	}
	//constructor default
	public Node() {this(new int[] {0});}
		
	
	//add link
	public void addLink(Link link) {
		if (checkLink(link) == -1) {
			links.add(link);
		} else {
			System.out.println("Link already exists");
		}
	}

	void addLink(int node1, int node2) {
		if (checkLink(node1, node2) == -1) {
			links.add(new Link(node1, node2));
		}
		else {
			System.out.println("Link already exists");
		}
	}
	
	//check Link
	int checkLink(int node1, int node2) {
		for (int i = 0; i < links.size(); i++) {
			if (links.get(i).isConnected(node1, node2)) {
				return i;
			}
		}
	    return -1;
	}

	int checkLink(Link link) {
		return checkLink(link.getNodes()[0], link.getNodes()[1]);
	}
	int getNodeDegree() {
		return links.size();
	}

	Link getLink(int i) {
		return links.get(i);
	}

	ArrayList<Link> getLinks() {
		return links;
	}

	public int[] getConnection(int i) {
		return links.get(i).getNodes();
	}
	public void clearQueue() {queue.clear();}
	public void deleteFromQueue(int index) {queue.remove(index);}
	
	public void setNodeOpinion(int[] nodeOpinion) {this.Opinion = nodeOpinion.clone();}
	public void setOneNodeOpinion(int index, int value) {
		if(value != -1 & value != 0 & value != 1)
			throw new Error("Opinion element must be -1, 0 or 1.");
		Opinion[index] = value;
	}
	public void sendMessage(Message msg) {messages_shared.add(msg); ids.add(msg.getId());}
	public void receiveMessage (Message msg) {queue.add(msg);} //setsharedmessage
	public void setThreshold(double cosineThreshold) {this.cosineThreshold = cosineThreshold;}
	
	public int[] getNodeOpinion() {return Opinion;}
	public int getNodeOpinion(int id) {return Opinion[id];}
	public ArrayList<Message> getAllNodeMessages() {return messages_shared;}
	public ArrayList<Message> getNodeQueue() {return queue;}
	public ArrayList<Integer> getSharedIds() {return ids;}
	public double getCosineThreshold() {return cosineThreshold;}
}
