package server;
import java.util.*;
import java.lang.*;
import java.io.*;

public class IPTable { // reading Ip table in
	final int maxSize = 100;
	private String[] names = new String[maxSize];
    private String[] hosts = new String[maxSize];
    private int[] ports = new int[maxSize];
    private int dirsize = 0;
    int NumofSeat = 0;
        
    int TableInit(){
    	try {
            String pathname = "../server/src/server/IP.txt";
            File filename = new File(pathname);   
            InputStreamReader reader = new InputStreamReader(  
            new FileInputStream(filename)); 
            BufferedReader br = new BufferedReader(reader);   
            String line = "";  
            line = br.readLine();
            NumofSeat =Integer.parseInt(line); // line 1 is the number of seats
            int count=1;
            String a ="";
            String b ="";
            int c =0;
            while (line != null) {
                line = br.readLine(); 
                if (count==1) {
                	a = line; count++; continue;
                }
                if (count==2) {
                	b = line; count++; continue;
                }
                if (count==3) {
                	c = Integer.parseInt(line); 
                	count=1;
                	this.insert(a,b,c); // every three combination of name , IP ,port
                	continue;
                }
            }
            br.close();
            
        } catch (Exception e) {  
            e.printStackTrace();  
    }
    	return dirsize;
    }// read txt and initial the ip table

    int search(String s) {
        for (int i = 0; i < dirsize; i++)
            if (names[i].equals(s)) return i;
        return -1;
    }//search certain IP
    
    int insert(String s, String hostName, int portNumber) {
        int oldIndex = search(s); // is it already there
        if (oldIndex == -1) {
            names[dirsize] = s;
            hosts[dirsize] = hostName;
            ports[dirsize] = portNumber;
            dirsize++;
            return 1;
        } else
            return 0;
    }//insert new ip
    
    int getPort(int index) {
        return ports[index];
    }// get port #
    
    String getHostName(int index) {
        return hosts[index];
    }// get Ip #
    
    int getRandomIndex() {
    	Random rand = new Random();
    	int  n = rand.nextInt(dirsize);
    	return n;
    }//get random index to connect
    
    int GetNum(){
    	return dirsize;
    }//get the number of servers
    
    int GetNumofSeat(){
    	return NumofSeat;
    }// get # seats
    
}

