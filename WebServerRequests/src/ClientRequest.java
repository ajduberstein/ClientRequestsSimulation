public class ClientRequest {
	public final String[] STATES = new String[]{"CREATED",
		"WS PROCESSING","DS LINE", "DS PROCESSING","EXIT"};
	int dsRequestsNeeded;
	int dsRequestsMet;
	int webRequestsMet;
	String location;
	
	public ClientRequest(double rand){
		this.dsRequestsNeeded = queriesNeeded(rand);
		this.dsRequestsMet = 0;
		this.webRequestsMet = 0;
		this.location = STATES[0];
	}
	
	public boolean canLeaveSystem(){
		return dsRequestsNeeded == dsRequestsMet;
	}
	
	public int getDsRequestsNeeded() {
		return dsRequestsNeeded;
	}

	public int getDsRequestsMet() {
		return dsRequestsMet;
	}

	public int getWebRequestsMet() {
		return webRequestsMet;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void bumpDsRequestMet(){
		dsRequestsMet++;
	}
	
	public void bumpWebRequestsMet(){
		webRequestsMet++;
	}
	
	public int queriesNeeded(double rand){
		if (0 <= rand && rand < .3){
			return 0;
		}
		else if (.3 <= rand && rand < .4){
			return 1;
		}
		else if (.4 <= rand && rand < .58){
			return 2;
		}
		else if (.58 <= rand && rand < .78){
			return 3;
		}
		else if (.78 <= rand && rand < .88){
			return 4;
		}
		else if (.88 <= rand && rand < .94){
			return 5;
		}
		else if (.94 <= rand && rand < .98){
			return 6;
		}
		else{
			return 7;
		}
	}
	
}
