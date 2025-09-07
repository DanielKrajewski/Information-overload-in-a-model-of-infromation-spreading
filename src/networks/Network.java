package networks;

import java.util.ArrayList;
import java.util.Random;
import save.Save;

public class Network {
	//list of nodes
	protected ArrayList<Node> nodes;
	//list of global links
	protected ArrayList<Link> globalLinks;
	//number of nodes
	protected int N;
	//number of links
	protected int E;
	//average degree
	protected double k;
	// Just random number
	protected Random rnd;
		
	int[][] adjMatrix;
	String topology;
		
	public Network (int numberOfNodes) {
		N = numberOfNodes;
		E = 0;
		k = 0;
		nodes = new ArrayList<Node>(N);
		for (int i = 0; i < N; i++)
			nodes.add(new Node());
		globalLinks = new ArrayList<Link>();
		
		rnd = new Random();
		topology = "custom";

		}
		
	protected void addLink(int node1, int node2) {
		if (node1 < 0 || node1 >= N || node2 < 0 || node2 >= N) {
			System.out.println("Invalid node");
			return;
		}
		if (node1 == node2) {
			System.out.println("Cannot connect node to itself");
			return;
		}
		if (nodes.get(node1).checkLink(node1, node2) != -1) {
			System.out.println("Link already exists");
		    return;
		}
		Link link = new Link(node1, node2);
		globalLinks.add(link);
		nodes.get(node1).addLink(link);
		nodes.get(node2).addLink(link);
		E++;
			
	}
		//creates adj matrix
	public void createAdjMatrix() {
		this.adjMatrix = new int[N][N];
		//zeros the matrix
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				adjMatrix[i][j] = 0;
			}
		}
		//fills in the matrix
		for (int i = 0; i < E; i++) {
			int[] nodes = globalLinks.get(i).getNodes();
			adjMatrix[nodes[0]][nodes[1]] = 1;
			adjMatrix[nodes[1]][nodes[0]] = 1;
		}
	}
		
	//display the adj matrix
	public void displayAdjMatrix() {
		createAdjMatrix();
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				System.out.print(adjMatrix[i][j] + " ");
			}
			System.out.println();
		}
	}
		
	public void saveAdjMatrix(String string) {
		// TODO Auto-generated method stub
		//save the adj matrix to a csv file
		try {
			java.io.FileWriter writer = new java.io.FileWriter(string);
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					writer.append(adjMatrix[i][j] + ",");
				}
				writer.append("\n");
			}
			writer.close();
		} catch (java.io.IOException e) {
			System.out.println("Error writing to file");
		}
	}
		
	//save node and neighbour indexes to a txt file with header node index \t neighbour indexes
	public void saveNeighbourIndexes(Save s) {
		s.writeDatatb("Node Index");
		s.writeDataln("Neighbour Indexes");
		for (int i = 0; i < N; i++) {
			int[] neighbours = getNeighbourIndex(i);
			String line = i + "\t";
			for (int j = 0; j < neighbours.length; j++) {
				line += neighbours[j] + " ";
			}
			s.writeDataln(line);
		}
		s.closeWriter();
	}
	
	public void saveNeighbourIndexesandOpinion(Save s) {
		s.writeDatatb("Node Index");
		s.writeDatatb("Neighbour Indexes");
		s.writeDataln("Opinion");
		
		for (int i = 0; i < N; i++) {
			int[] neighbours = getNeighbourIndex(i);
			String line = i + "\t";
			for (int j = 0; j < neighbours.length; j++) {
				line += neighbours[j] + " ";
			}
			s.writeDatatb(line);
			//get first opinion of the node
			s.writeData(nodes.get(i).getNodeOpinion(0));
			s.writeDataln("");
		}
		s.closeWriter();
	}
	
	
	public int getNumberOfNodes() {return N;}
	public int getNumberOfLinks() {return E;}
	public double getAverageDegree() {return 2 * (double) E / N;}
	public int[][] getAdjacencyMatrix() {return adjMatrix;}
	public ArrayList<Node> getNodes() {return nodes;}
	public Node getNode(int i) {return nodes.get(i);}
	public ArrayList<Link> getLinks() {return globalLinks;}
	public Link getLink(int i) {return globalLinks.get(i);}
	public int getNodeDegree(int i) {return nodes.get(i).getNodeDegree();}
	public String getTopologyType() {return topology;}
		
	public int getNeighbourIndex(int nodeIndex, int connectionIndex) {
		if(getNode(nodeIndex).getConnection(connectionIndex)[0] != nodeIndex)
			return getNode(nodeIndex).getConnection(connectionIndex)[0];
		else
			return getNode(nodeIndex).getConnection(connectionIndex)[1];
	}
		
	public int[] getNeighbourIndex(int nodeIndex) {
		int[] neighbourIndexes = new int[getNode(nodeIndex).getNodeDegree()];
			
		for(int i=0; i<neighbourIndexes.length; i++)
			neighbourIndexes[i] = getNeighbourIndex(nodeIndex, i);
			
		return neighbourIndexes;
	}

}
