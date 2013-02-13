import java.util.PriorityQueue;
import java.util.Random;

/**
 * 
 * @author Stein
 *
 *Some expected results: about 15000 requests processed
 */

public class Simulation {

	private static final double TIME_MAX = 30000; //30000 ms in 30 seconds
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
			ClientRequest[] currentDsOccupants = new ClientRequest[2];
			int wsUsageTimer = 0; 
			double[] dsUsageTime = new double[2]; 
			Random r = new Random(186);
			double lesserTime = 0;
			double requestProb;
			double waitDifference;
			double iat = 0;
			double marginalIat = 0;
			//A request will show up about every two milliseconds
			while( iat < TIME_MAX ){
				do{
					requestProb = r.nextDouble();
				}while(requestProb == 0);
				marginalIat = -(1/(CLIENT_REQUEST_MEAN/SEC_TO_MS_CONVERSION))*Math.log(requestProb);
				iat += marginalIat;
				//WEB SERVER
				ClientRequest tmp = new ClientRequest(r.nextDouble());
				tmp.setCreateTime(marginalIat);
				wsClients.add(tmp);
				if(wsUsageTimer <= 0){
					currentWSOccupant = wsClients.poll();
					currentWSOccupant.setLocation(currentWSOccupant.STATES[1]);
					currentWSOccupant.bumpWebRequestsMet();
					do{
						requestProb = r.nextDouble();
					}while(requestProb == 0);
					wsUsageTimer = (int) Math.round(-1*WS_ST*Math.log(requestProb));
				}
				else{
					wsUsageTimer+=iat;
					for (ClientRequest c: wsClients){
						c.addWsWait(marginalIat);
					}
				}
				//EXIT
				//Check if WS occupant can leave system
				if(currentWSOccupant.canLeaveSystem()){
					//TODO KPI bumped here.
					currentWSOccupant = null;
				}
				else{
				//Else, the request, fresh from the web server, gets transferred to the DS
					dsClients.add(currentWSOccupant.newInstance(currentWSOccupant));
				}
				//DATA SERVER
				//If there is somebody in the data server queue
				if (!dsClients.isEmpty()){
					//Check if there is a vacancy in the data server
					 if(currentDsOccupants[0] == null){
						 //If so, pull from the cue and fill it
						 currentDsOccupants[0] = dsClients.poll();
						 //Calculate usage time
						 do{
							requestProb = r.nextDouble();
						 }while(requestProb == 0);
						 dsUsageTime[0] = -1*DS_ST*Math.log(requestProb);
					 }
					 //Symmetric case for second vacancy
					 else if (currentDsOccupants[1] == null){
						 currentDsOccupants[1] = dsClients.poll();
						 do{
							requestProb = r.nextDouble();
						 }while(requestProb == 0);
						 dsUsageTime[1] = -1*DS_ST*Math.log(requestProb);
					 }
					 //Here, we have an occupied queue and no processing space
					 else{
						waitDifference = Math.abs(dsUsageTime[1] - dsUsageTime[0]);
						lesserTime = (dsUsageTime[1] > dsUsageTime[0] ? dsUsageTime[0] : dsUsageTime[1] );
						for (ClientRequest c: wsClients){
							//At this point, we know the next server to be utilized will be the one with the lower wait
							c.addDsWait(Math.abs(waitDifference));
							c.addDsWait(lesserTime);
						}
						if (lesserTime == dsUsageTime[0]){
							dsUsageTime[1] -= dsUsageTime[0];
						}
						else{
							dsUsageTime[0] -= dsUsageTime[1];
						}
						iat += lesserTime;
					 }
					 if (dsUsageTime[0] <= 0){
						 wsClients.add(currentDsOccupants[0].newInstance(currentDsOccupants[0]));
						 currentDsOccupants[0] = null;
					 }
					 if(dsUsageTime[1] <= 0){
						 wsClients.add(currentDsOccupants[1].newInstance(currentDsOccupants[1]));
						 currentDsOccupants[1] = null;
					 }
				}	
				//Otherwise, if dsClients 
				else{
					System.out.println("This shouldn't have happened");
				}
		}
	}
}