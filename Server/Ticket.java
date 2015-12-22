package server;
import java.util.*;
import java.io.*;

public class Ticket implements Serializable{// ticket tale
	int dirsize;
    String[] seatname;
    int seatleft;
    
    Ticket(int n){
    	seatname = new String[n];
    	for(int i=0;i<n;i=i+1){
    		seatname[i]="empty";
    	}
    	dirsize = n;
    	seatleft = dirsize;
    }// initialize ticket table
    
    int search(String name) {
    	int count=0;
        for (int i = 0; i < dirsize; i=i+1){
            if (seatname[i].equals(name)) count++;
        }
        return count;
    }//return the number of find, if not found,return 0
    
    int reserve(String name, int number) {
        if(seatleft < number) {return -1;}
        
        boolean b = Arrays.asList(seatname).contains(name);
        if (b) return -2;
        
        for(int j=0;j<dirsize;j=j+1){
        	if(seatname[j].equals("empty")){
        		seatname[j]=name;
        		number--;
        		seatleft--;
        	}
        	if(number==0) {break;}
        }
        return 1;
    }
    //not seat return -1
    //already reserve return -2
    //reserve successful return 1
   
    int delete(String name) {
    	boolean b = Arrays.asList(seatname).contains(name);
        if (!b) return 0;
    
    	int ori = seatleft;
    	for(int i=0;i<dirsize;i++){
        	if(seatname[i].equals(name)){
        	seatname[i]="empty";
        	seatleft++;
        }
    	}
        int release = seatleft-ori;
        return release;      		    
    }
    
    int GetSeatleft() {
        return seatleft;      		   
    }// how many seats left
    
    String GetSeatnum(String name) {
    	String s="";
    	for(int i=0;i<dirsize;i++){
        	if(seatname[i].equals(name)){
        	String snum = String.valueOf(i);
    		s=s+snum+",";
        }	
    	}
    	return s;
    }//get seat #
    
    String[] GetSeatName(){
    	return seatname;
    }// get seatname
    
    String Decode() {
    	String s="";
    	for(int i=0;i<dirsize-1;i++){ 	
    		s=s+seatname[i]+"-";
    	}
    	s=s+seatname[dirsize-1];
    	return s;
    }// decode table for transmission
 	
    void Code(String s){
    	seatname = s.split("-");
    	int count=0;
    	for(int j=0;j<dirsize;j=j+1){
        	if(seatname[j].equals("empty")){
        		count++;
        	}
    }
     seatleft = count;
    }// code the receive table back
    
    void UpdateTicket(Ticket Tic){
    	seatname = Tic.seatname;
    	int count=0;
    	for(int j=0;j<dirsize;j=j+1){
        	if(seatname[j].equals("empty")){
        		count++;
        	}
    }
     seatleft = count;
    }
}// update your table

