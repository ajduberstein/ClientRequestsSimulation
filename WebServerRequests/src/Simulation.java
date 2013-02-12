import java.util.PriorityQueue;
import java.util.Random;


public class Simulation {

	private static final int TIME_MAX = 30000; //30000 ms in 30 seconds
	private static final int CLIENT_REQUEST_MEAN = 500; //Mean number of client requests in a second is 500
	private static final int SEC_TO_MS_CONVERSION = 1000; //Conversion factor of 1 sec = 1000 ms
	private static final int WS_ST = 5; //Service time for web server in ms
	private static final int DS_ST = 15; //Service time for data server in ms
	/**
	 * @param args
	 * t is in ms
	 */
	public static void main(String[] args) {
			PriorityQueue<ClientRequest> wsClients = new PriorityQueue<ClientRequest>();
			PriorityQueue<ClientRequest> dsClients = new PriorityQueue<ClientRequest>();
			ClientRequest currentWSOccupant = null;
			int wsUsageTimer = 0; 
			int ds1UsageTimer, ds2UsageTimer = 0; 
			Random r = new Random(186);
			double requestProb;
			double iat;
			for (int t = 0; t < TIME_MAX; t++){
				do{
					requestProb = r.nextDouble();
				}while(requestProb == 0);
				iat = -(CLIENT_REQUEST_MEAN/SEC_TO_MS_CONVERSION)*Math.log(requestProb);
				//WEB SERVER
				if(.5 < iat){ //TODO is this right?
					wsClients.add(new ClientRequest(r.nextDouble()));
				}
				if(wsUsageTimer == 0){
					currentWSOccupant = wsClients.poll();
					currentWSOccupant.setLocation(currentWSOccupant.STATES[1]);
					currentWSOccupant.bumpWebRequestsMet();
					wsUsageTimer = WS_ST;
				}
				else{
					wsUsageTimer--;
				}
				//EXIT
				//Check if WS occupant can leave system
				if(currentWSOccupant.canLeaveSystem()){
					//TODO KPI bumped here.
					currentWSOccupant = null;
				}
				//DATA SERVER
				//1. Check if there exists a request leaving the web server
				if(currentWSOccupant != null){
					//2. If so, send it to an open data server
					
				}
			}
	}
	

}
