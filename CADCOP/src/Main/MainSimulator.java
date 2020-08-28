package Main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import AgentsAbstract.Agent;
import AgentsAbstract.AgentFunction;
import AgentsAbstract.AgentVariable;
import AgentsAbstract.AgentVariableInference;
import Data.Data;
import Delays.CreatorDelays;
import Delays.CreatorDelaysNone;
import Delays.CreatorDelaysNormal;
import Delays.CreatorDelaysUniform;
import Delays.ProtocolDelay;
import Down.CreatorDown;
import Down.CreatorDownConstant;
import Down.CreatorDownNone;
import Down.ProtocolDown;
import Problem.Dcop;
import Problem.DcopGraphColoring;
import Problem.DcopScaleFreeNetwork;
import Problem.DcopUniform;

public class MainSimulator {

	// ------------------------------**For Data
	public static List<Mailer> mailerAll = new ArrayList<Mailer>();
	public static Map<Protocol, List<Mailer>> mailersByProtocol = new HashMap<Protocol, List<Mailer>>();

	// ------------------------------**Algorithmic relevance under imperfect
	// communication**
	// true = send only if change, false = send regardless if change took place
	public static boolean sendOnlyIfChange = false;

	// ------------------------------**Implementation**
	public static boolean isThreadMailer = false; // determines the mailers type
	public static boolean isThreadDebug = false;
	public static boolean isWhatAgentDebug = false;
	// ------------------------------**any time**
	public static boolean anyTime = false;

	// ------------------------------**Experiment Repetitions**
	public static int start = 0;
	public static int end = 100;
	public static int termination =  5000;

	// ------------------------------**PROBLEM MANGNITUDE**
	public static int A = 50; // amount of agents
	public static int D = -1; // if D or costParameter < 0 use default
	public static int costParameter = -1; // if D or costParameter < 0 use default

	// ------------------------------ **DCOP GENERATOR**
	/*
	 * 1 = Random uniform; 2 = Graph Coloring; 3 = Scale Free Network
	 */
	public static int dcopBenchMark = 1;
	// 1 = Random uniform
	public static double dcopUniformP1 = 0.1;// 0.1,0.6
	public static double dcopUniformP2 = 1;// Probability for two values in domain between neighbors to have constraints
	// 2 = Graph Coloring
	public static double dcopGraphColoringP1 = 0.05;// Probability for agents to have constraints
	public static int costLb = 10;
	public static int costUb = 20;
	// 3 = Graph Coloring
	public static int dcopScaleHubs = 10; // number of agents with central weight
	public static int dcopScaleNeighbors = 3; // number of neighbors (not including policy of hubs
	public static double dcopScaleP2 = 1;// Probability for two values in domain between neighbors to have constraints

	// ------------------------------**Algorithm Selection**
	/*
	 * 1 = DSA-ASY; 2 = DSA-SY; 3 = MGM-ASY ; 4 = MGM-SY ; 5 = AMDLS ;
	 * 7 = maxsum asynch; 8 = maxsum synch; 9 = split constraint factor;
	 * 10 = DSA_SDP-ASY; 11 = DSA_SDP-SY
	 */
	public static int agentType = 11;

	public static boolean isAMDLSdebug = false;
	/*
	 * delayTypes: 0 = non, 1 = normal, 2 = uniform
	 */
	public static int delayType = 2;
	public static CreatorDelays creatorDelay;

	/*
	 * delayTypes: 0 = non
	 */
	public static int downType = 1;

//	public static CreatorDelays creatorDelay;
//	public static CreatorDowns creatorDown;

	public static String protocolDelayHeader = "";
	public static String protocolDownHeader = "";
	public static String mailerHeader = "";

	public static String header = "";
	public static Collection<String> lineInExcel = new ArrayList<String>();
	public static String fileName = "";

	public static void main(String[] args) {

		Dcop[] dcops = generateDcops();
		// printProblemCreationDebug(dcops);
		List<Protocol> protocols = createProtocols();
		runDcops(dcops, protocols);
		createData();
		createExcel();

		// createStatistics();
	}

	private static void createExcel() {
		BufferedWriter out = null;
		try {
			FileWriter s = new FileWriter(fileName + ".csv");
			out = new BufferedWriter(s);
			out.write(header);
			out.newLine();

			for (String o : lineInExcel) {
				out.write(o);
				out.newLine();
			}

			out.close();
		} catch (Exception e) {
			System.err.println("Couldn't open the file");
		}

	}

	private static void createData() {
		createFileName();
		createHeader();
		createMeansByProtocol();

	}

	private static void createFileName() {
		String ans = "Algorithm_" + AgentVariable.AlgorithmName ;
		if (!AgentVariable.algorithmData.equals("")) {
			ans = ans+"("+AgentVariable.algorithmData+")";
		}
		ans = ans+",";
		ans = ans+","+ "DCOP_" + Dcop.dcopName + ",";
		ans = ans + "Mailer_" + Mailer.mailerName + ",";
		ans = ans + "A_" + A + ",";
		ans = ans + "Reps_" + (end - start)+",";
		ans = ans + "Time_" + (termination);

		fileName = ans;
	}

	private static void createMeansByProtocol() {
		String dcopString = Dcop.dcopName;
		// String dcopString = "";

		String algoString = AgentVariable.AlgorithmName + "," + AgentVariable.algorithmData;
		for (Entry<Protocol, List<Mailer>> e : mailersByProtocol.entrySet()) {
			String protocolString = e.getKey().getDelay().toString();
			SortedMap<Integer, List<Data>> mapBeforeCalcMean = getMeanMapBeforeAvg(e.getValue());
			SortedMap<Integer, Data> meanMap = createMeanMap(mapBeforeCalcMean);

			for (Entry<Integer, Data> e1 : meanMap.entrySet()) {
				String tempAns = dcopString + "," + protocolString + "," + algoString + "," + e1.getValue();
				lineInExcel.add(tempAns);
			}
		}

	}

	private static SortedMap<Integer, Data> createMeanMap(SortedMap<Integer, List<Data>> input) {
		SortedMap<Integer, Data> ans = new TreeMap<Integer, Data>();
		for (Entry<Integer, List<Data>> e : input.entrySet()) {
			ans.put(e.getKey(), new Data(e));
		}
		return ans;
	}

	private static SortedMap<Integer, List<Data>> getMeanMapBeforeAvg(List<Mailer> mailers) {
		SortedMap<Integer, List<Data>> ans = new TreeMap<Integer, List<Data>>();
	
		int firstMax = getFirstMax(mailers);	
		for ( int i = firstMax ; i < termination; i++) {
			List<Data> listPerIteration = new ArrayList<Data>();
			for (Mailer mailer : mailers) {
				listPerIteration.add(mailer.getDataPerIteration(i));
			}
			ans.put(i, listPerIteration);
		}
		return ans;
	}

	private static int getFirstMax(List<Mailer> mailers) {
		List<Integer>firsts = new ArrayList<Integer>();
		for (Mailer m : mailers) {
			firsts.add(m.getFirstKeyInData());
		}
		return Collections.min(firsts);
	}

	// ------------ 1. DCOP CREATION------------
	private static Dcop[] generateDcops() {
		Dcop[] ans = new Dcop[end - start];
		for (int i = 0; i < end - start; i++) {
			int dcopId = i + start;
			ans[i] = createDcop(dcopId).initiate();
		}
		return ans;
	}

	private static Dcop createDcop(int dcopId) {
		Dcop ans = null;
		// use default Domain contractors
		if (D <= 0 || costParameter <= 0) {
			if (dcopBenchMark == 1) {
				ans = new DcopUniform(dcopId, A, dcopUniformP1, dcopUniformP2);
			}

			if (dcopBenchMark == 2) {
				ans = new DcopGraphColoring(dcopId, A, dcopGraphColoringP1);
				// int dcopId, int A, int D, int costLb, int costUb, double p1
			}

			if (dcopBenchMark == 3) {
				ans = new DcopScaleFreeNetwork(dcopId, A, dcopScaleHubs, dcopScaleNeighbors, dcopScaleP2);
			}
		}

		else {
			if (dcopBenchMark == 1) {
				ans = new DcopUniform(dcopId, A, D, costParameter, dcopUniformP1, dcopUniformP2);
			}

			if (dcopBenchMark == 2) {
				ans = new DcopGraphColoring(dcopId, A, D, costLb, costUb, dcopGraphColoringP1);
			}

			if (dcopBenchMark == 3) {
				ans = new DcopScaleFreeNetwork(dcopId, A, D, costParameter, dcopScaleHubs, dcopScaleNeighbors,
						dcopScaleP2);
			}
		}

		return ans;
	}

	// ------------ 2. PROTOCOL CREATION------------
	private static List<Protocol> createProtocols() {
		List<ProtocolDelay> delays = getCreatorDelays().createProtocolDelays();
		List<ProtocolDown> downs = getCreatorDowns().createProtocolDowns();
		List<Protocol> ans = new ArrayList<Protocol>();
		for (ProtocolDelay delay : delays) {
			for (ProtocolDown down : downs) {
				Protocol p = new Protocol(delay, down);
				ans.add(p);
			}
		}
		for (Protocol protocol : ans) {
			mailersByProtocol.put(protocol, new ArrayList<Mailer>());
		}
		return ans;
	}

	private static CreatorDown getCreatorDowns() {
		CreatorDown ans = null;

		if (downType == 0) {
			ans = new CreatorDownNone();
		}

		if (downType == 1) {
			ans = new CreatorDownConstant();
		}
		protocolDownHeader = ans.getHeader();

		return ans;
	}

	private static CreatorDelays getCreatorDelays() {
		CreatorDelays ans = null;
		if (delayType == 0) {
			ans = new CreatorDelaysNone();
		}

		if (delayType == 1) {
			ans = new CreatorDelaysNormal();
		}

		if (delayType == 2) {
			ans = new CreatorDelaysUniform();
		}

		protocolDelayHeader = ans.getHeader();

		return ans;
	}

	// ------------ 3. Run the DCOPs------------

	private static void runDcops(Dcop[] dcops, List<Protocol> protocols) {
		for (Dcop dcop : dcops) {
			int protocolCounter = -1;
			for (Protocol protocol : protocols) {
				protocolCounter += 1;
				Mailer mailer = getMailer(protocol, dcop);
				dcop.dcopMeetsMailer(mailer);
				mailer.mailerMeetsDcop(dcop);
				if (isThreadMailer) {
					executeThreadMailer(mailer);
				} else {
					mailer.execute();
				}
				addMailerToDataFrames(protocol, mailer);
				System.out.println("Algo: " + AgentVariable.AlgorithmName + "; Finish DCOP: " + dcop.getId()
						+ " ; SCORE: " + mailer.getDataPerIteration(termination - 1).getGlobalCost() + "; protocol "
						+ protocol.getDelay());
			}
			System.out.println("----------------------------");
		}

	}

	private static void executeThreadMailer(Mailer mailer) {
		Thread t = new Thread((MailerThread) mailer);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	private static void addMailerToDataFrames(Protocol protocol, Mailer mailer) {
		mailerAll.add(mailer);
		mailersByProtocol.get(protocol).add(mailer);

	}

	private static Mailer getMailer(Protocol protocol, Dcop dcop) {
		Mailer ans;
		if (isThreadMailer) {
			ans = new MailerThread(protocol, termination, dcop);
		} else {
			ans = new MailerIterations(protocol, termination, dcop);
		}

		return ans;
	}

	// ------------ 4. DATA------------

	private static void createHeader() {

		header = "DCOP";

		if (delayType != 0) {
			header = header + "," + protocolDelayHeader + ",";
		}
		/*
		 * if (downType != 0) { header = header + "," + protocolDownHeader; }
		 */
		header = header + "Algorithm" + "," + AgentVariable.algorithmHeader + ",";
		header = header + Data.header();

	}

	// ------------ 5. Debug------------

	private static void printProblemCreationDebug(Dcop[] dcops) {
		printAmountOfNeighbors(dcops);
		printConstraintMatrixs(dcops);

	}

	private static void printConstraintMatrixs(Dcop[] dcops) {
		for (Dcop dcop : dcops) {
			System.out.println("-----Dcop number : " + dcop.getId() + " constraint matrix with agent zero ----");
			for (AgentVariable a : dcop.getVariableAgents()) {
				if (a.getId() != 0) {
					Integer[][] matrix = a.getMatrixWithAgent(0);
					if (matrix != null) {
						System.out.println("\t" + "agent " + a.getId());
						print2DArray(matrix);
					}
				}
			}
		}
	}

	private static void print2DArray(Integer[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				System.out.print("[" + matrix[i][j] + "]");
			}
			System.out.println();
		}
	}

	private static void printAmountOfNeighbors(Dcop[] dcops) {
		for (Dcop dcop : dcops) {
			System.out.println("-----Dcop number : " + dcop.getId() + " neighbor count-----");
			double sumN = 0;
			for (AgentVariable a : dcop.getVariableAgents()) {
				int aN = a.neighborSize();
				System.out.println("\t" + "Agent " + a.getId() + ": " + aN);
				sumN += aN;
			}
			System.out.println();
			System.out.println("The average amount of neighbors per agent is: " + (sumN / A));
			System.out.println();
		}

	}

}
