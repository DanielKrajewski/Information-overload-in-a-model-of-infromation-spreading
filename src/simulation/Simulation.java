package simulation;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import networks.Network;
import networks.Node;
import save.Save;
import tools.Tools;
//import ProgramingTools.Time;
//import ProgramingTools.Tools;

public class Simulation {
	// ~ DATA FIELDS ~
	
	// The network
	private Network network;
	// Number of nodes in network
	private int N;
	// Length of opinion vectors
	private int D;
	// Just random number
	private Random rnd;
	// Number of different informations in the network, 
	// when agent sends new message this number is going up
	// It's just an id of a message
	private int nMsg;
	// Probability of sending new message by agent
	private double pNewMessage;
	// Cosine threshold
	private double cosineThreshold;
	// List of all messages
	private ArrayList<Message> messages;
	
	// Array to agregata data
	// [0] - id of message
	// [1] - length of message
	private int[][] datamessages;
	private int[][][] Messages;
	private int [][] nodeshared;
	private int [][] nodereceived;
	private int [][] nodeinterested;
	private double [][] nodesimilar;
	private int [] nodeinterestedamount;
	

	// ~ CONSTRUCTORS ~
	// Constructor #1
	public Simulation(Network network, int lenghtOfOpinionVector, double pNewMessage, double cosineThreshold) {
		if(lenghtOfOpinionVector <= 0)
			throw new Error("Length of opinion vector must be greater than zero.");
		if(pNewMessage < 0 || pNewMessage > 1)
			throw new Error("Probability of sending new message shoud be between 0 and 1.");
		
		rnd = new Random();
		
		this.network = network;
		N = network.getNumberOfNodes();
		this.pNewMessage = pNewMessage;;
		D = lenghtOfOpinionVector;
		
		nMsg = 0;
		messages = new ArrayList<Message>();
		this.cosineThreshold = cosineThreshold;
		setInitialOpinions(network);
		}
	
	// ~ METHODS ~
	// Set initial opinions
	public void setInitialOpinions(Network net) {
		for (Node node : network.getNodes()) {
			int[] opinion = new int[D];
			for (int i = 0; i < D; i++) {
				opinion[i] = rnd.nextInt(3) - 1;
			}
			node.setNodeOpinion(opinion);
		}
	}
	
	// Sets initial opinions based on Ising model
	public void setInitialOpinions(Network net, double sim) {
		setInitialOpinions(net);
		
		double beta = 2; // exponent
		int i = -1; // random node
		int changeIndex = -1; // random index of opinion vector
		int newOpinion = -2; // new opinion in given index
		int e = calculateWholeEnergy(net); // whole energy of the system
		int de = 0; // energy change
		double currentSimilarity = 0;
		int j = 0;
		
		while((currentSimilarity < sim - 0.005 || currentSimilarity > sim + 0.005) & currentSimilarity <= 0.8) {
			i = rnd.nextInt(N);
			changeIndex = rnd.nextInt(D);
			newOpinion = (net.getNode(i).getNodeOpinion()[changeIndex] + 1 + rnd.nextInt(2)+1) % 3 - 1;
			de = calculateEnergyChange(net, i, newOpinion, changeIndex);
			
			if(de < 0) {
				net.getNode(i).setOneNodeOpinion(changeIndex, newOpinion);
				e += de;
			}
			else if(Math.exp(- beta * de) > rnd.nextDouble()) {
				net.getNode(i).setOneNodeOpinion(changeIndex, newOpinion);
				e += de;
			}
			if((j+1) % 1000 == 0)
				currentSimilarity = getNeighoursAverageSimilarity(net);
			j++;
		}
	}	
	
	
	// Calculates whole energy of the system
	// it's sum of product of every element in the vector of every pair in the network
	public int calculateWholeEnergy(Network net) {
		int e = 0;
		int neighborIndex = -1;
			
		for(int i=0; i<N; i++)
			for(int j=0; j<net.getNodeDegree(i); j++) {
				neighborIndex = net.getNeighbourIndex(i, j);
				for(int k=0; k<D; k++)
					e -= net.getNode(i).getNodeOpinion()[k] * net.getNode(neighborIndex).getNodeOpinion()[k];
			}
			
		return e/2;
	}
	
	// Calculates energy change when one opinion was change
	public int calculateEnergyChange(Network net, int i, int newValue, int changeIndex) {
		if(changeIndex < 0 | changeIndex >= D)
			throw new Error("Change index out of bound");
			
		int de = 0;
		int neighborIndex = -1;
			
		for(int j=0; j<net.getNodeDegree(i); j++) {
			neighborIndex = net.getNeighbourIndex(i, j);
			de += net.getNode(i).getNodeOpinion()[changeIndex] * net.getNode(neighborIndex).getNodeOpinion()[changeIndex];
			de -= newValue * net.getNode(neighborIndex).getNodeOpinion()[changeIndex];
		}
			
		return de;
	}
	
	// Get average similarity between the closest neighbors in the network
	public double getNeighoursAverageSimilarity(Network net) {
		double sum = 0;
		double counts = 0;
		int neighborIndex = -1;
		
		for(int i=0; i<N; i++) {
			for(int j=0; j<net.getNodeDegree(i); j++) {
				neighborIndex = net.getNeighbourIndex(i, j);
				sum += agentsSimilarity(network.getNode(i).getNodeOpinion(), network.getNode(neighborIndex).getNodeOpinion());
				counts += 1;
				}
		}
		
		return(sum/counts);
	}	
	
	
	public double getGlobalSimilarity() {
		double sum = 0;
		double counts = 0;
		
		for (int i = 0; i < N; i++) {
			for (int j = i + 1; j < N; j++) {
				sum += agentsSimilarity(network.getNode(i).getNodeOpinion(), network.getNode(j).getNodeOpinion());
				counts += 1;
			}
		}
		return(sum/counts);
	}
	
	public double agentsSimilarity(int[] opinion1, int[] opinion2) {
		return Tools.cosineSimilarity(opinion1, opinion2);
	}
	
	// Gets last shared message
	private Message getLastMessage() {return messages.get(messages.size()-1);}
	//Send message into the network
	public void SendRandomMessage(int sourceNode, int time) {
		//do potem jaki max length liczyc jakis procent D czy co? narazie po prostu fixed
		//int maxlength = 20;
		int messagelength =  rnd.nextInt((int)Tools.convertToDouble(0.14*D, 0)) + 1;
		ArrayList<Integer> indexes = new ArrayList<Integer>(D);
		for(int i=0; i<D; i++)
		    indexes.add(i);
		int[] message = new int[messagelength];
		boolean zeroLength = true;
		while(zeroLength) {
			Collections.shuffle(indexes);
			for (int i = 0; i < messagelength; i++) {
				message[i] = getNodeOpinion(sourceNode)[indexes.get(i)];
			}
			if (Tools.length(message) != 0)
				zeroLength = false;
			
		}
		messages.add(new Message(new int[][] {message.clone(), Tools.ArrayToInts(indexes,messagelength)}, time, nMsg));
		
		sendMessage(sourceNode, getLastMessage());
		setNieghboursDashboard(sourceNode, getLastMessage());
		nMsg++;
	}
	
	//print Last message
	public void printLastMessage() {
		//print last message
		System.out.print("Last message:  ");
		getLastMessage().printMessageContent();
		//print indexes
		System.out.print("Indexes:       ");
		getLastMessage().printMessageIndexes();
		//length of message
		System.out.println("Length of message: " + getLastMessage().getMessageContent().length);
		
	}

	//print messages
	public void printMessages() {
		for (int i = 0; i < messages.size(); i++) {
			messages.get(i).printMessageContent();
			messages.get(i).printMessageIndexes();
		}
	}
	
	//print opinion
	public void printOpinion(int i) {
		System.out.print("Opinion of node " + i + ": ");
		int[] opinion = getNodeOpinion(i);
		for (int j = 0; j < opinion.length; j++) {
			System.out.print(opinion[j] + " ");
		}
		System.out.println();
	}
	//print queue
	public void printqueue(int i) {
		System.out.println("Queue of node " + i + ": ");
		ArrayList<Message> queue = getQueue(i);
		for (int j = 0; j < queue.size(); j++) {
			System.out.print("Message  " + j + ": ");
			queue.get(j).printMessageContent();
			System.out.print("Indexes: " + j + ": ");
			// print indexes
			queue.get(j).printMessageIndexes();
		}
	}
	
	//save data test
	public void saveData() {
		//save initial opinions of nodes to csv
		//get node opinions
		int[][] opinions = new int[N][D];
		for (int i = 0; i < N; i++) {
			opinions[i] = network.getNodes().get(i).getNodeOpinion();
		}
		//save to opinion.csv
		Save save = new Save("opinions.csv");
		save.saveOpinions(opinions);
		//save random message to csv
		Save saveRandom = new Save("randomMessage.csv");
		Message randomMessage = getLastMessage();
		saveRandom.saveRandomMessage(randomMessage);
		
		
	}
	
	
	public int[]getNodeOpinion(int i) {return network.getNode(i).getNodeOpinion();}
	public Node getNode(int i) {return network.getNode(i);}
	public int getNodeDegree(int i) {return network.getNodeDegree(i);}
	public ArrayList<Message> getQueue(int i) {return network.getNode(i).getNodeQueue();}
	public ArrayList<Integer> getNodeSharedIds(int i) {return network.getNode(i).getSharedIds();}
	public void deleteFromQueue(int node, int queuecounter) {network.getNode(node).deleteFromQueue(queuecounter);}
	public void clearQueue(int node) {network.getNode(node).clearQueue();}
	
	public void sendMessage(int i, Message msg) {
		if (i < 0 || i > N)
			throw new Error("Indexes out of range.");
		getNode(i).sendMessage(msg);
	}
	
	public void receiveMessage(int i, Message msg) {
		if (i < 0 || i > N)
			throw new Error("Indexes out of range.");
		getNode(i).receiveMessage(msg);
	}
	
	
	// Add message to the every neighbor dashboard
		public void setNieghboursDashboard(int i, Message msg) {
			if (i < 0 || i > N)
				throw new Error("Index out of range.");
			int connectionIndex = -1;
			
			for(int j=0; j<getNodeDegree(i); j++) {
				connectionIndex = getNode(i).getConnection(j)[0] != i ? 0 : 1;
				receiveMessage(getNode(i).getConnection(j)[connectionIndex], msg);
			}
		}
	
	
		private double cosineSimilarity(int[] opinion, int[][] message) {
			
			double lOpinion = 0;
			double lMessage = 0;
			double dotProduct = 0;
			
			for(int i=0; i<message[0].length; i++) {
				lOpinion += opinion[message[1][i]] * opinion[message[1][i]];
				lMessage += message[0][i] * message[0][i];
				dotProduct += opinion[message[1][i]] * message[0][i];
			}
			
			// when agent has neutral opinion on something he's randomly post it
			if(lOpinion == 0 || lMessage == 0) return rnd.nextDouble();
			else return dotProduct/Math.sqrt(lOpinion * lMessage);
			
			//return dotProduct/Math.sqrt(lOpinion * lMessage);
		}
		
		
		public boolean isCosineSimilar(int[] opinion, int[][] message) {
			return cosineSimilarity(opinion, message) >= cosineThreshold;
		}
	
		//check how many messages are similar to the opinion in the queue
		public int checkQueue(int i) {
			int counter = 0;
			ArrayList<Message> queue = getQueue(i);
			int[] opinion = getNodeOpinion(i);
			for (int j = 0; j < queue.size(); j++) {
				if (isCosineSimilar(opinion, queue.get(j).getMessageContentAndIndexes()))
					counter++;
			}
			return counter;
		}
		
		private boolean alreadyShared(ArrayList<Integer> sendByNode, Message neighborMessage) {		
			for(int i=sendByNode.size()-1; i>=0; i--)
				if(sendByNode.get(i).equals(neighborMessage.getId()))
					return true;
			return false;
		}
		public int getQueueSize(int i) {return network.getNode(i).getNodeQueue().size();}
		//test
		public void runwithprint() {
			//send random message
			System.out.println("node 0 sends messages :");
			SendRandomMessage(0, 0);
			
			printLastMessage();
			SendRandomMessage(0, 0);
			printLastMessage();
			System.out.println(" ");
			printOpinion(1);
			
			printqueue(1);
			System.out.println("Check queue: " + checkQueue(1));
			deleteFromQueue(1, 0);
			//getNode(1).deleteFromQueue(0);
			printqueue(1);
			System.out.println("Check queue: " + checkQueue(1));
		}
		
		public void run() {
			//send random message
			SendRandomMessage(0, 0);
			SendRandomMessage(0, 0);
			checkQueue(1);
			getNode(1).deleteFromQueue(0);
			checkQueue(1);
		}
	
		//main
		/*public static void main(String[] args) {
			// create a random network
			RandomNetwork net = new RandomNetwork(600, 1, 1);
			net.createAdjMatrix();
			net.saveAdjMatrix("adjMatrix.csv");
			// create a simulation
			Simulation sim = new Simulation(net, 300, 0.1, 1);
			// run the simulation
			//sim.run(1000);
			
			//send random message
			for (int i = 0; i < 1; i++) {
				sim.runwithprint();
			}
			sim.printOpinion(1);
			sim.printOpinion(0);

		}*/
		
		
		private void oneStep(int time) {
			int node = rnd.nextInt(N); // pick random node from the network
			nodeshared[time][0]=node;
			boolean alreadyShared; // true if message with this ID was shared by agent
			boolean alreadyinQueue; // true if message is already in the queue
			double cosineSimilarity; // cosine similarity between message and node opinion
			int[][] message; // message content
		
			//write into nodereceived[time] ids of received messages
			nodereceived[time] = new int[getQueueSize(node)];
			nodeinterested[time] = new int[getQueueSize(node)];
			nodesimilar[time] = new double[getQueueSize(node)];
			
			for (int i=getQueueSize(node) - 1; i>=0; i--) {
				cosineSimilarity = cosineSimilarity(getNodeOpinion(node), getQueue(node).get(i).getMessageContentAndIndexes());
				alreadyShared = alreadyShared(getNodeSharedIds(node), getQueue(node).get(i));
				nodereceived[time][i] = getQueue(node).get(i).getId()+1;
				//check if it was already in the queue for bigger i 
				alreadyinQueue = false;
				for (int j = i + 1; j < getQueueSize(node); j++) {
					if (getQueue(node).get(i).getId() == getQueue(node).get(j).getId()) {
						alreadyinQueue = true;
						break;
					}
				}
				
				if (!alreadyinQueue) {
					nodesimilar[time][i] = cosineSimilarity;
				}
				else nodesimilar[time][i] = 2;
				
				
				if (cosineSimilarity >= cosineThreshold && !alreadyShared && !alreadyinQueue) {
					//get i for the first that gets here
					// if cosine similarity is greater than threshold and message was not already shared
					// and it is not already in the queue
					nodeinterested[time][i] = getQueue(node).get(i).getId()+1;
					nodeinterestedamount[time]++;
					
					
				} else
					nodeinterested[time][i] = 0;
			}
			
			
			
			// Sends new message to the network
			if(rnd.nextDouble() < pNewMessage) {
				SendRandomMessage(node, time);
				
				saveMessageToArray();
				nodeshared[time][1]=getLastMessage().getId()+1; 
			} 
			else { 
				// Share message
				// Checks if the last neighbor message is similar to the node's opinion vector
				// this loop is for all messages shared by node's neighbors
				for(int i=getQueueSize(node) - 1; i>=0; i--) {
					cosineSimilarity = cosineSimilarity(getNodeOpinion(node), getQueue(node).get(i).getMessageContentAndIndexes());
					// If the agent like it the message goes to next condition
					if(cosineSimilarity >= cosineThreshold) {
						alreadyShared = alreadyShared(getNodeSharedIds(node), getQueue(node).get(i));
						// Checks if this message was already shared (by ID)
						if(!alreadyShared) {
							message = getQueue(node).get(i).getMessageContentAndIndexes().clone();
							messages.add(new Message(message.clone(), time, getQueue(node).get(i).getId()));

							sendMessage(node, getLastMessage());
							setNieghboursDashboard(node, getLastMessage());
							
							saveMessageToArray();
							nodeshared[time][1]=getLastMessage().getId()+1; 
							
							break;
						}
						else deleteFromQueue(node,i);
					}
					else deleteFromQueue(node,i);
				}
				 clearQueue(node); 
			}
		}
		
		public void run(int time_steps) {
			datamessages = new int[time_steps][2];
			Messages = new int[time_steps][2][];
			nodeshared = new int[time_steps][2];
			nodeinterested = new int[time_steps][];
			nodereceived = new int[time_steps][];
			nodeinterestedamount = new int[time_steps];
			nodesimilar = new double[time_steps][];
			for (int i = 0; i < time_steps; i++) {
				oneStep(i);
			}
		}
				
		public void saveMessageToArray() {
			int msgID = getLastMessage().getId();
			datamessages[msgID][0]++; //repetitions
			datamessages[msgID][1] = getLastMessage().getMessageContent().length; //length
			Messages[msgID][0] = getLastMessage().getMessageContent(); //content
			Messages[msgID][1] = getLastMessage().getMessageIndexes(); //indexes
		}

		public void saveMessageTable(Save s) {
			s.writeDatatb("id"); //i+1
			s.writeDatatb("content"); //Messages[i][0]
			s.writeDatatb("indexes"); //Messages[i][1]
			s.writeDatatb("length"); //datamessages[i][1]
			s.writeDataln("repetitions"); //datamessages[i][0]
			for (int i = 0; i <= getLastMessage().getId(); i++) {
				s.writeDatatb(i+1);
				
				for (int j = 0; j < Messages[i][0].length-1; j++) {
					s.writeDatacomma(Messages[i][0][j]);
				}
				s.writeDatatb(Messages[i][0][Messages[i][0].length-1]);
				for (int j = 0; j < Messages[i][1].length-1; j++) {
					s.writeDatacomma(Messages[i][1][j]);
				}
				s.writeDatatb(Messages[i][1][Messages[i][1].length-1]);
				
				
				s.writeDatatb(datamessages[i][1]);
				s.writeDataln(datamessages[i][0]);
			}
			s.closeWriter();
		}
		
		
		public void saveStepTable(int time_steps, Save s) {
			s.writeDatatb("step");
			s.writeDatatb("node"); //nodeshared[i][0]
			s.writeDatatb("node_degree");
			s.writeDatatb("message_shared_id"); //nodeshared[i][1]
			//s.writeDatatb("length"); //datamessages[nodeshared[i][1]-1][1]
			//s.writeDatatb("repetitions"); //datamessages[nodeshared[i][1]-1][0]
			s.writeDatatb("messages_received_ids");
			s.writeDatatb("messages_interested_ids");
			s.writeDatatb("messages_interested_amount");
			s.writeDataln("similarity");
			for (int i = 0; i < time_steps; i++) {
				s.writeDatatb(i+1);
				s.writeDatatb(nodeshared[i][0]);
				s.writeDatatb(getNodeDegree(nodeshared[i][0]));
				s.writeDatatb(nodeshared[i][1]);
				
				/*length of message and repetitions already in massage table so just to check
				if (nodeshared[i][1] != 0) {
                    s.writeDatatb(datamessages[nodeshared[i][1]-1][1]); //=1
                    s.writeDatatb(datamessages[nodeshared[i][1]-1][0]); //-1
                }
                else {
                    s.writeDatatb(0);
                    s.writeDatatb(0);
                }*/
				
				if (nodereceived[i].length != 0) {
					for (int j = 0; j < nodereceived[i].length - 1; j++) {
						s.writeDatacomma(nodereceived[i][j]);
					}
					s.writeDatatb(nodereceived[i][nodereceived[i].length - 1]);
				} else
					s.writeDatatb(0);
				
				
				if (nodeinterested[i].length != 0) {
					for (int j = 0; j < nodeinterested[i].length - 1; j++) {
						s.writeDatacomma(nodeinterested[i][j]);
						
					}
					s.writeDatatb(nodeinterested[i][nodeinterested[i].length - 1]);
					
				} else
					s.writeDatatb(0);
				s.writeDatatb(nodeinterestedamount[i]);
							
				
				if (nodesimilar[i].length != 0) {
					for (int j = 0; j < nodesimilar[i].length - 1; j++) {
						if (nodesimilar[i][j] != 2) // if it is not already in queue
							s.writeDatacomma(nodesimilar[i][j]);
					}
					if (nodesimilar[i][nodesimilar[i].length - 1] != 2) // if it is not already in queue
						s.writeDataln(nodesimilar[i][nodesimilar[i].length - 1]);
					else
						s.writeDataln("");
				} 
				else
				    s.writeDataln("");
				
				
				
			}
			s.closeWriter();
		
		}
		public void saveParameters(Save s, int realisations, int maxTime) {
			s.writeDatatb("N");
			s.writeDatatb(N);
			s.writeDataln("Number of nodes in the newtork");
			
			s.writeDatatb("<k>");
			s.writeDatatb(network.getAverageDegree());
			s.writeDataln("Average degree");
			
			s.writeDatatb("network type");
			s.writeDatatb(network.getTopologyType());
			s.writeDataln("Topology type of the network");
			
			s.writeDatatb("D");
			s.writeDatatb(D);
			s.writeDataln("Length of the opinion vector");
			
			
			s.writeDatatb("tau");
			s.writeDatatb(Tools.convertToDouble(cosineThreshold));
			s.writeDataln("Threshold cosines");
			
			s.writeDatatb("alpha");
			s.writeDatatb(pNewMessage);
			s.writeDataln("Probability of sending new message");
			
			
			s.writeDatatb("realizations");
			s.writeDatatb(realisations);
			s.writeDataln("Number of independet realisations");
			
			s.writeDatatb("time steps");
			s.writeDatatb(maxTime);
			s.writeDataln("Number of time steps");
			
			s.writeDataln("");
		}
		
		public void saveOpinions(Save s) {
			s.writeDatatb("node");
			s.writeDatatb("opinion");
			s.writeDataln("time");

			for (int i = 0; i < N; i++) {
				s.writeDatatb(i);
				int[] opinion = getNode(i).getNodeOpinion();
				for (int j = 0; j < opinion.length - 1; j++) {
					s.writeDatacomma(opinion[j]);
				}
				s.writeDatatb(opinion[opinion.length - 1]);
				s.writeDataln("");
			}
			s.closeWriter();
		}
		
		/*//main
				public static void main(String[] args) {
					// create a random network
					BA net = new BA(600, 3, 3);
					
					net.createAdjMatrix();
					net.saveAdjMatrix("adjMatrix.csv");
					net.saveNeighbourIndexes("neighbours.txt");
					// create a simulation
					Simulation sim = new Simulation(net, 100, 0.1, 0.2);
                    // run the simulation
					int time_steps = 500000;
					sim.run(time_steps);
					sim.saveMessageTable();
				//	sim.saveStepTable(time_steps);
					
					
				}
		*/
}
