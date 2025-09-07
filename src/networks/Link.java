package networks;

public class Link {

	private int[] nodes;

	//constructor
	public Link(int node1, int node2) {
		nodes = new int[2];
		nodes[0] = node1;
		nodes[1] = node2;
	}
	
	//check connection
	public boolean isConnected(int node1, int node2) {
		if(node1 == node2) return false;
		if(nodes[0] == node1)
			if(nodes[1] == node2) return true;
			else return false;
		else if(nodes[0] == node2)
			if(nodes[1] == node1) return true;
			else return false;
		else return false;
	}
	public boolean isConnected(int[] node) {
		return isConnected(node[0], node[1]);
    }
	
    //get nodes
	public int[] getNodes() {
		return nodes;
		
		
	}
}
