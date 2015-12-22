package server;
import java.io.Serializable;

public class Clock implements Serializable {// clock
	int localId = 0;
	int processNum = 100;
	int[] tsVector  = new int[processNum];
																						
	Clock(){
		for(int i=0;i<100;i++){
			tsVector[i]=0;
		}
	}// initialize clock
	
	Clock(int processId, int NumofPro){
		localId = processId;
		processNum = NumofPro;
		int length = processNum;
		for(int i=0;i<length;i++){
			tsVector[i]=0;
		}
		tsVector[localId] = 1;
	}//initialize clock with argument
	
	void send(){
		tsVector[localId]++;
	}// when send ,clock++
		
	void receive(int receiveTs, int receiveId){
		tsVector[localId] = Math.max(tsVector[localId], receiveTs) + 1;
		tsVector[receiveId] = Math.max(receiveTs, tsVector[receiveId]);
	}//when receive, compare
		
	int[] read(){
		return tsVector;	
	}//read timestamp	
	
	int readLocalTs(){
		return tsVector[localId];
	}// read local timestamp
}



