package server;

public class MutualEx {
	// mutual exclusion section
	int Max = 100;
	int sizeOfProcess=1;
	int localProcessId =0;
	int countOfAck = 0;
	private int[] requestQ; 
	private int[] ReadQ;
	Clock c;
	
	MutualEx(int n, int processId){
		localProcessId = processId;
		sizeOfProcess = n;
		c = new Clock(processId,n);

		requestQ = new int[n];
		for(int i=0;i<sizeOfProcess;i++){
			requestQ[i] = Integer.MAX_VALUE;
		}
		
		ReadQ = new int[n];
		for(int i=0;i<sizeOfProcess;i++){
			ReadQ[i] = Integer.MAX_VALUE;
		}
	}//initial lamport's algorithm

	void request(){
		requestQ[localProcessId] = c.tsVector[localProcessId];
		//local request
	}

	void requestRead(){
		ReadQ[localProcessId] = c.tsVector[localProcessId];
		//local request read
	}
	
	void release(){
		requestQ[localProcessId] = Integer.MAX_VALUE;
		//local release  
	}
	void releaseRead(){
		ReadQ[localProcessId] = Integer.MAX_VALUE;
		//local release read
	}
	
	void receive(int ts, int processId, String typeOfSend){
		if (typeOfSend == "request"){
			requestQ[processId] = ts;
			//when receive req
		}
		else if (typeOfSend == "requestRead"){
			ReadQ[processId] = ts;
			//when receive read req
		}
		
		else if (typeOfSend == "release"){
			requestQ[processId] = Integer.MAX_VALUE;
		}//when receive release
		
		else if (typeOfSend == "releaseRead"){
			ReadQ[processId] = Integer.MAX_VALUE;
		}//when receive read release
		
		else if (typeOfSend == "ack"){
			countOfAck++;
		}//when receive ack
	}
	
	Boolean EntryCS(int Alive){
		int min = requestQ[0];
		for (int i=1;i<sizeOfProcess;i++){
			if (requestQ[i]<min){
				min = requestQ[i];
			}
		}
		if(requestQ[localProcessId]==min & countOfAck>=Alive-1 ){
			return true;}
		return false;
		}// permission of entering CS
	
	Boolean ReadEntryCS(int Alive){
		int min = requestQ[0];
		for (int i=1;i<sizeOfProcess;i++){
			if (requestQ[i]<min){
				min = requestQ[i];
			}
		}
		
		if(ReadQ[localProcessId]<min & countOfAck>=Alive-1 ){
			return true;}
		return false;
		}//permission of reding entering CS
}
	
	
