package Client;

import java.lang.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class Client { // client
	BufferedReader din;
    PrintStream pout;
    IPTable table;
    
    Client(){
        table = new IPTable();
        table.TableInit();
    }//initialize client

    public Socket getSocket() throws IOException {
    	int count=table.GetNum();
    	int a = table.getRandomIndex();
    	while(true){
        	try{
        	Socket server = new Socket(table.getHostName(a), table.getPort(a));
            din = new BufferedReader(new InputStreamReader(server.getInputStream()));
            pout = new PrintStream(server.getOutputStream());
        	return server;
        }catch (SocketException e) {
            if(a<count-1)a++;
            else a=0;
            }
        } 
        }//randomly get server to connect
    

    public String reserve(String name, int num){
    	while(true){
        	try{
    	Socket k = getSocket();
		System.out.println("Reserving...");
        pout.println("reserve " + name + " " + num);
        pout.flush();
        String re = din.readLine();
        k.close();
        return re;  //do something
            } catch (Exception e) {
            	System.out.println("Server Crash...Trying again...");
            	}
            }
    }//reserve method

    public String search(String name){
    	while(true){
        	try{
    	Socket k = getSocket();
		System.out.println("Searching...");
        pout.println("search " + name);
        pout.flush();
        String re = din.readLine();
        k.close();
        return re;
        	} catch (Exception e) {
            	System.out.println("Server Crash...Trying again...");
        	}
    }//search method
    }

    public String delete(String name){
    	while(true){
        	try{
    	Socket k = getSocket();
		System.out.println("Deleting...");
        pout.println("delete " + name);
        pout.flush();
        String re = din.readLine();
        k.close();
        return re;} catch (Exception e) {
        	System.out.println("Server Crash...Trying again...");
    	}}
    }//delete method
    
    public static void main(String[] args) {
        Client myClient = new Client();
        int count = 1;
        int maxTries = 9999;
        while(true) {
            try {
            	String g = myClient.reserve("Tom",10);
                System.out.println(g);            
                String k = myClient.search("Tom");
                System.out.println(k);
                String l = myClient.delete("Tom");
                System.out.println(l);
                break; //do something
            } catch (Exception e) {
            	System.out.println("Server Crash...Try again #"+count);
                if (++count == maxTries){
					try {
						throw e;
					} catch (Exception e1) {
						e1.printStackTrace();
					}
            }
          }
        }
    }
}