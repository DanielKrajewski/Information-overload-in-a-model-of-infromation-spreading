package networks;

import java.util.ArrayList;
import java.util.Random;

public class BA extends Network{
	
	
	//initial number of nodes m0 and connections to add m
	private int m0;
	private int m;
	
	
	//constructor
	public BA(int N, int m0, int m) {
		super(N);
		if (m0 < 1) 
			throw new RuntimeException("Invalid initial number of nodes, it must at least one");
		if (m > m0) 
			throw new RuntimeException("Invalid number of adding connectons, it must be lesser or equal to inital nodes");
		this.N = N;
		this.m0 = m0;
		this.m = m;
		k=0;
		rnd = new Random();
		nodes = new ArrayList<Node>(N);
		globalLinks = new ArrayList<Link>();
		assignLinks();
		topology = "BA";
		
	}
	
	
	private void assignLinks() {
		//connect all initial links to each other
		for (int i = 0; i < m0; i++) {
			nodes.add(new Node());
		}
		
		if(m0>1) {
			for (int i = 0; i < m0 - 1; i++) {
				for (int j = i + 1; j < m0; j++) {
					addLink(i, j);
				}
			}
		}
		//which nodes to connect the new node
		int[] connectNodes = new int[m];
		//add the rest of the nodes
		for (int i = m0; i < N; i++) {
			nodes.add(new Node());
			// if globablLinks is empty, add to first node
			if (globalLinks.size() == 0) {
				connectNodes[0] = 0;
			}
			else {
				for (int j = 0; j < m; j++) {
					// choose a node to connect to
					connectNodes[j] = globalLinks.get(rnd.nextInt(globalLinks.size())).getNodes()[rnd.nextInt(2)];
					for (int k = 0; k < j; k++) {
						if (connectNodes[j] == connectNodes[k]) {
							j--;
							break;
						}
					}
				}
			}
			
			for (int j = 0; j < m; j++) {
				addLink(i, connectNodes[j]);
			}
			k=(2*E/N);
			
		}
		
	}
	
	
	//main
	public static void main(String[] args) {
		BA ba = new BA(1000,1,1);
		//save the adj matrix to a csv file
		ba.createAdjMatrix();
		ba.saveAdjMatrix("adjMatrix.csv");
		
	}

	public int getInitialNodes() {return m0;}
	public int getAddingConnections() {return m;}
}
