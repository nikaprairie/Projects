package comp303.assignment6.robot;

//Sets Log Entry based on action and charge
class ActionEntry implements LogEntry {
	String        aActionName;
	int           aBatteryLevel;

	ActionEntry(String pName, int pBatteryLevel) { 
		this.aActionName = pName;
		this.aBatteryLevel = pBatteryLevel;
	}
	
	//Appropriate formatting for Log Entry of Actions
	public String getLogString() {
		return this.aActionName + " action performed, battery level is " + this.aBatteryLevel;
	}

}
