package server;

import java.util.*;
import java.lang.*;
import java.io.*;
import java.net.*;

public class Lamport {// use for connecting other servers for lamport's algorithm
	Socket[] socs = new Socket[100];
	BufferedReader[] dins= new BufferedReader[100];
	PrintStream[] pouts = new PrintStream[100];
	int id = -1;
	int NumOfServer =1 ;
	private IPTable table = new IPTable();
	int[] Connect = new int[100];
	
	Lamport(int n,int NumOfPro) throws UnknownHostException, IOException{
		id = n;
    	int count = NumOfPro;
    	table.TableInit();
    	
    	for (int i=0;i<count;i++){
    		try{
    		if(i!=n){
    			Socket k = new Socket(table.getHostName(i), table.getPort(i));
//    			System.out.println(i+" ready");
    			BufferedReader din = new BufferedReader(new InputStreamReader(k.getInputStream()));
    			PrintStream pout = new PrintStream(k.getOutputStream());
    			socs[i]=k;
    			dins[i]=din;
    			pouts[i] = pout;
        		NumOfServer++;
        		Connect[i]=1;
    		}
    		}catch(Exception e){
//    			System.out.println(i+"dead");
    			Connect[i]=0;
    			continue;
    		}
        }//find other server and connect
	}
	
	int GetAlive(){
		return NumOfServer;
	}// how many servers are running
	
	void UpdateAlive(int i){
		Connect[i]=1; 
	}//got someone's message and denote it
}
