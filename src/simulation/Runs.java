package simulation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import networks.Network;
import networks.RandomNetwork;
import save.Save;
//import Networks.RealNetwork;
import networks.BA;
import tools.Tools;

public class Runs implements Runnable{
	private int id;
	private int N;
	private int k;
	private int timeSteps;
	private int dimOpinion;
	private double pNewMessage;
	private int realisations;
	private double simthreshold;
	private double similarity;
	private double globalsimilarity;
	
	private String topologyType;
	private Network net;
	
	Runs(int id, double threshold, String topologyType, int realisations) {
		this.id = id;
		
		N = 600;
		//k = 6;
		k=6;
		dimOpinion = 100;
		pNewMessage = 0.1;
		
		
		timeSteps = 500000;

		similarity = 0; // similarity between opinions of neighbours
		globalsimilarity = 0;
		
		this.realisations = realisations;
		this.simthreshold = threshold;
		this.topologyType = topologyType;
		net = new Network(0);
	}
	
	public void run() {
		if(topologyType.equals("randomNetwork"))
			net = new RandomNetwork(N,k/2,k/2);
		else if(topologyType.equals("BA"))
			net = new BA(N, k/2,k/2);
		
		Simulation sim = new Simulation(net, dimOpinion, pNewMessage, simthreshold);
		String folder = "sim/";
		//String folder = "RESULTS"+Tools.convertToString(similarity)+"/"+timeSteps+"/"+net.getTopologyType()+"/"+Tools.convertToString(simthreshold)+"/";
		//to slabe String folder = "RESULTS0,8/"+timeSteps+"/"+net.getTopologyType()+"/"+Tools.convertToString(simthreshold)+"/";
		Save masseges = new Save(folder + id + "_messages.txt");
		Save Steps = new Save(folder +  id  + "_steps.txt");
		Save neighbours = new Save(folder + id + "_neighbours.txt");
		sim.setInitialOpinions(net,similarity);
		similarity=sim.getNeighoursAverageSimilarity(net);
		globalsimilarity = sim.getGlobalSimilarity();
		
		
		
		System.out.println("Similarity: " + similarity);
		System.out.println("Global similarity: " + globalsimilarity);
		//print similarity and global similarity to file
		FileWriter fw;
		try {
			fw = new FileWriter(folder + "/sim/similarity.txt", true);
			fw.write(id + "\t");
			fw.write(similarity + "\t");
			fw.write(globalsimilarity + "\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // true = appen
		
		
		
		if (id == 1 )
		{
			Save params = new Save(folder + "params.txt");
		    sim.saveParameters(params, realisations, timeSteps);
		    params.closeWriter();
		}
		net.createAdjMatrix();
		net.saveAdjMatrix("adjMatrix.csv");
		net.saveNeighbourIndexesandOpinion(neighbours);
		
		Save opinion = new Save(folder + id + "_opinion.txt");
		sim.saveOpinions(opinion);
		
		sim.run(timeSteps);
		sim.saveMessageTable(masseges);
		sim.saveStepTable(timeSteps, Steps);
		
		masseges.closeWriter();
		Steps.closeWriter();
		opinion.closeWriter();
		neighbours.closeWriter();
		System.out.println("Run finished: " + id + " " + topologyType + " " + simthreshold);
		
		}
	
	public static void runExperiment() throws InterruptedException {
		
		//double[] tau = new double[] {-1,-0.9,-0.8,-0.7,-0.6,-0.5,-0.3,-0.2,-0.1,0,0.1,0.3,0.4,0.5,0.6,0.7,0.9,1};
		//int N = 2;
		//int n =18;
		
		
		//int n=3;
		//int N=10;
		//double[] tau = new double[] {-0.4,0.2,0.8};
		
		int n=1;
		int N=10;
		double[] tau = new double[] {0.8};
		
	//	ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()/2);
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			for(int i=0; i<n; i++) 
				for(int j=0 ; j<N; j++)
					executor.execute(new Runs(j+1, tau[i], "BA", N));
		
		for(int i=0; i<n; i++) 
				for(int j=0 ; j<N; j++)
				executor.execute(new Runs(j+1, tau[i], "randomNetwork", N));
		
		executor.shutdown();
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		

	}
	
	
}
