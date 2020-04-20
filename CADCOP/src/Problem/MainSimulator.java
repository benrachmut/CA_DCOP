package Problem;
import java.util.List;

public class MainSimulator {

	// ------------------------------**Implementation**
	public static boolean runThreads;
	public static double mailerMessagesGaps = 1;
	
	
	// ------------------------------**any time**
	public static boolean anyTime = false;


	// ------------------------------**Experiment Repetitions**
	public static int start = 0;
	public static int end = 100;
	public static int termination = 1000;

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
	public static double dcopUniformP1 = 0.2;// Probability for agents to have constraints
	public static double dcopUniformP2 = 1;// Probability for two values in domain between neighbors to have constraints
	// 2 = Graph Coloring
	public static double dcopGraphColoringP1 = 0.05;// Probability for agents to have constraints
	// 3 = Graph Coloring
	public static int dcopScaleHubs = 10; // number of agents with central weight
	public static int dcopScaleNeighbors = 3; // number of neighbors (not including policy of hubs
	public static double dcopScaleP2 =  1 ;// Probability for two values in domain between neighbors to have constraints
	
	// ------------------------------**Algorithm Selection**
	/*
	 * 1 = DSA-ASY; 2 = DSA-SY; 3 = MGM-ASY ; 4 = MGM-SY ; 5 = AMDLS ; 6 = DSA_SDP ;
	 */
	public static int agentType = 1; // 1= DSA ASY,1= DSA SY
	// 1,2-DSA
	public static double dsaP;
	// 6-DSA_SDP
	public static double dsaSdpPA, dsaSdpPB, dsaSdpPC, dsaSdpPD;
	public static int dsaSdpK;

	public static void main(String[] args) {
		Dcop[] dcops = generateDcops();
		//List<Mailer>mailers = createMailers();
		//dcopsMeetMailers(dcops,mailers);
	}

	/*
	 * 
	 */
	private static Dcop[] generateDcops() {
		Dcop[] ans = new Dcop[end - start];
		for (int dcopId = start; dcopId < end; dcopId++) {
			ans[dcopId] = createDcop(dcopId).initiate();
		}
		return ans;
	}

	private static Dcop createDcop(int dcopId) {
		Dcop ans = null;
		// use default Domain contractors
		if (D <= 0 || costParameter <= 0) {
			if (dcopBenchMark == 1) {
				ans = new DcopUniform(A, dcopUniformP1, dcopUniformP2);
			}

			if (dcopBenchMark == 2) {
				ans = new DcopGraphColoring(A,  dcopGraphColoringP1);
			}

			if (dcopBenchMark == 3) {
				ans = new DcopScaleFreeNetwork(A,  dcopScaleHubs, dcopScaleNeighbors, dcopScaleP2);
			}
		}

		else {
			if (dcopBenchMark == 1) {
				ans = new DcopUniform(A, D, costParameter, dcopUniformP1, dcopUniformP2);
			}

			if (dcopBenchMark == 2) {
				ans = new DcopGraphColoring(A,  D, costParameter, dcopGraphColoringP1);
			}

			if (dcopBenchMark == 3) {
				ans = new DcopScaleFreeNetwork(A,  D, costParameter, dcopScaleHubs, dcopScaleNeighbors,
						dcopScaleP2);
			}
		}

		return ans;
	}
}
