package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class Server{
	BufferedReader din;
    PrintStream pout;
	private Ticket Tic;
    private IPTable table;
    Lamport Lam;
    int NumofSeat=0;
    int id =-1;
    int NumOfServer=0; 
    int NumOfPro = 0;
    MutualEx mutualEx;
    
	Server() {
        table =new IPTable();
        table.TableInit();
        NumOfPro = table.GetNum();
        NumofSeat = table.GetNumofSeat();
        
	}//Server initialize
	
	public ServerSocket ServerInit() throws IOException{	
    	while(true){
    		try{
		int count=NumOfPro;
    	int a = 0;
    	ServerSocket listener = new ServerSocket();
        while(true){
        	try{
            	listener = new ServerSocket(table.getPort(a));
            	break;
        	}catch(Exception e) {
                System.out.println("Fail to Build Server...Trying Again...");
                if(a<count)a++;
                else a=0;
        	}
        }
        this.id=a;
        mutualEx = new MutualEx(NumOfPro,id);
        Tic = new Ticket(table.NumofSeat);
        System.out.println("#"+id+" Server started:");
        return listener;
        }
    		catch(Exception e){
    			System.out.println("System error..Trying again");
    		}
    		}
        
    }//initial server socket to listening
	
	
	 void handleclient(Socket theClient) throws ClassNotFoundException {
		 try {//receiving any kinds of socket and message. Have corresponding reaction as follows 
	            BufferedReader din = new BufferedReader
	            (new InputStreamReader(theClient.getInputStream()));
	            PrintWriter pout = new PrintWriter(theClient.getOutputStream());
	            String getline = din.readLine();
	            StringTokenizer st = new StringTokenizer(getline);
	            String tag = st.nextToken();
            	
	            if (tag.equals("reserve")) { // client willing to reserve
	            	String name = st.nextToken();
	            	int num = Integer.parseInt(st.nextToken());

	            	
	            	Lamport LamReqReser = new Lamport(this.id, this.NumOfPro);
	            	mutualEx.request();
	            	mutualEx.c.send();
	            	System.out.println("Reserving...");
	            	try{	
	            	for(int i = 0; i<this.NumOfPro; i++){
	            		if(LamReqReser.Connect[i]==1 & i!=LamReqReser.id){
	            			LamReqReser.pouts[i].println("req "+id+" "+mutualEx.c.tsVector[id]);
	    	            	String get = LamReqReser.dins[i].readLine();
	    		            StringTokenizer stonize = new StringTokenizer(get);
	    		            String tags = stonize.nextToken();
	    	                if (tags.equals("Ack")){
	    	                	int s1 = Integer.parseInt(stonize.nextToken());
	    	                	this.mutualEx.receive(0,s1,"ack");
	            		}//send req to all running server and waiting for ack
	            	}
	            		}
	            	}
	            	catch(Exception e){
	            		System.out.println("System error..Trying again");
	            	}
	            	
	            	 while (true) {
	           
		    	        	if (mutualEx.EntryCS(LamReqReser.NumOfServer) == true){
		    	        		break;}
		    	        }//waiting...until get permit to CS
		            	
	            	int k = Tic.reserve(name, num);
	            	//doing something in CS
	            	
	            	Lamport LamTick = new Lamport(this.id, this.NumOfPro);
	            	for(int j = 0; j<this.NumOfPro; j++){
	            		if(LamTick.Connect[j]==1 & j!=LamTick.id){
	            			LamTick.pouts[j].println("Update "+Tic.Decode());
	    	            	String get = LamTick.dins[j].readLine();
	    		            StringTokenizer stonizer = new StringTokenizer(get);
	    		            String tags = stonizer.nextToken();
	    	                if (tags.equals("Already")){
	    	                }// after reserve, tell other server to update their ticket table and get confirmation
	            		}
	            		}	            		
	            
	            	if (k==-1){
	            		int left = Tic.GetSeatleft();
	            		pout.println("Failed: only "+left+" seats left but "+num+" seats are request"); 
	            	}
	            	else if(k==-2) {
	            		String seatnum = Tic.GetSeatnum(name);
	            		pout.println("Failed:"+ name +" has booked the following seats:"+seatnum);
	            	}
	            	else if(k==1) {
	            		String seatnum = Tic.GetSeatnum(name);
	            		pout.println("The seats have been reserved for "+name+":"+seatnum);
	            	}
	            	//different reply to client
	    	        
	            	Lamport LamRelReser = new Lamport(this.id, this.NumOfPro);
	            	mutualEx.release();
	            	mutualEx.c.send();
	            	
	            	for(int m = 0; m<this.NumOfPro; m++){
	            		if(LamRelReser.Connect[m]==1 & m!=LamRelReser.id){
	            			LamRelReser.pouts[m].println("rel "+id);
	            		}
	            	}
	            	//release CS
	            	mutualEx.countOfAck = 0;
	            	}
	            
	            
	            else if (tag.equals("search")) {
	            	String name = st.nextToken();
	            
	            	Lamport LamReqReser = new Lamport(this.id, this.NumOfPro);
	            	mutualEx.requestRead();
	            	mutualEx.c.send();
	            	System.out.println("Searching...");
	            	for(int i = 0; i<this.NumOfPro; i++){
	            		if(LamReqReser.Connect[i]==1 & i!=LamReqReser.id){
	            			LamReqReser.pouts[i].println("reqRead "+id+" "+mutualEx.c.tsVector[id]);
	    	            	String get = LamReqReser.dins[i].readLine();
	    		            StringTokenizer stonize = new StringTokenizer(get);
	    		            String tags = stonize.nextToken();
	    	                if (tags.equals("Ack")){
	    	                	int s1 = Integer.parseInt(stonize.nextToken());
	    	                	this.mutualEx.receive(0,s1,"ack");
	            		}//sending read req to all other running servers
	            		}
	            	}
	            	 while (true) {
	      	           
		    	        	if (mutualEx.ReadEntryCS(LamReqReser.NumOfServer) == true){
		    	        		break;}
		    	        }// waiting for entering CS
		            	
	            	int g = Tic.search(name);
	            	
	            	if(g==0) pout.println("Failed: No reservation is made by "+name);
	            	else {
	            		String seatnum = Tic.GetSeatnum(name);
	            		pout.println(seatnum);
	            	}//reply to client
	            
	            	Lamport LamRelReser = new Lamport(this.id, this.NumOfPro);
	            	mutualEx.releaseRead();
	            	mutualEx.c.send();//release
	            	
	            	for(int m = 0; m<this.NumOfPro; m++){
	            		if(LamRelReser.Connect[m]==1 & m!=LamRelReser.id){
	            			LamRelReser.pouts[m].println("relRead "+id);
	            		}
	            	}
	            	
	            	mutualEx.countOfAck = 0;
	            }
	           
	            else if (tag.equals("delete")) {
	            	String name = st.nextToken();
		            	Lamport LamReqReser = new Lamport(this.id, this.NumOfPro);
		            	mutualEx.request();
		            	mutualEx.c.send();
		            	System.out.println("Deleting...");
		            	for(int i = 0; i<this.NumOfPro; i++){
		            		if(LamReqReser.Connect[i]==1 & i!=LamReqReser.id){
		            			LamReqReser.pouts[i].println("req "+id+" "+mutualEx.c.tsVector[id]);
		    	            	String get = LamReqReser.dins[i].readLine();
		    		            StringTokenizer stonize = new StringTokenizer(get);
		    		            String tags = stonize.nextToken();
		    	                if (tags.equals("Ack")){
		    	                	int s1 = Integer.parseInt(stonize.nextToken());
		    	                	this.mutualEx.receive(0,s1,"ack");
		            		}//send request of delete
		            		}
		            	}
		            	 while (true) {
			    	        	if (mutualEx.EntryCS(LamReqReser.NumOfServer) == true){
			    	        		break;}
			    	        }//waiting for entering CS

	            	int k = Tic.delete(name);
	            	
	            	Lamport LamTick = new Lamport(this.id, this.NumOfPro);	
	            	for(int j = 0; j<this.NumOfPro; j++){
	            		if(LamTick.Connect[j]==1 & j!=LamTick.id){
	            			LamTick.pouts[j].println("Update "+Tic.Decode());
	    	            	String get = LamTick.dins[j].readLine();
	    		            StringTokenizer stonizer = new StringTokenizer(get);
	    		            String tags = stonizer.nextToken();
	    	                if (tags.equals("Already")){
	    	                }//send updated ticket table to every running server
	            		}
	            	}
	            
	            	if (k==0) pout.println("Failed: No reservation is made by "+ name); 
	            	else {
	            		int left = Tic.GetSeatleft();
	            		pout.println( k +" seats have been released."+left+" seats are now available.");
	            	}//reply to client

	            	Lamport LamRelReser = new Lamport(this.id, this.NumOfPro);
	            	mutualEx.release();
	            	mutualEx.c.send();
	            	for(int m = 0; m<this.NumOfPro; m++){
	            		if(LamRelReser.Connect[m]==1 & m!=LamRelReser.id){
	            			LamRelReser.pouts[m].println("rel "+id);
	            		}//release
	            	}
	            	mutualEx.countOfAck = 0;
	            }
	            
	            else if (tag.equals("SynchronizeTable")) {
	                ObjectOutputStream os = new
	            	ObjectOutputStream(theClient.getOutputStream());
	                ObjectInputStream is = new
	                  ObjectInputStream(theClient.getInputStream());
	                os.writeObject(this.Tic);
	            }// reply to new coming server's request of synchronizing ticket table
	            
	            else if (tag.equals("SynchronizeClock")) {
	                ObjectOutputStream os = new
	            	ObjectOutputStream(theClient.getOutputStream());
	                ObjectInputStream is = new
	                  ObjectInputStream(theClient.getInputStream());
	                os.writeObject(this.mutualEx.c);
	            }// reply to new coming server's request of synchronizing ticket table
	            
	            else if (tag.equals("req")){
            	int s1 = Integer.parseInt(st.nextToken());
            	int s2 = Integer.parseInt(st.nextToken());

            	this.mutualEx.c.receive(s2, s1);
            	this.mutualEx.receive(s2, s1, "request");
            	pout.println("Ack "+this.id);
            }// got req, send back ack
	            
	            else if (tag.equals("reqRead")){
            	int s1 = Integer.parseInt(st.nextToken());
            	int s2 = Integer.parseInt(st.nextToken());

            	this.mutualEx.c.receive(s2, s1);
            	this.mutualEx.receive(s2, s1, "requestRead");
            	pout.println("Ack "+this.id);  	
            }// got read req,send back ack
	            
	            else if (tag.equals("Update")){
	            	String kakak = st.nextToken();
	            	Tic.Code(kakak);
	            	pout.println("Already");
            }// got new ticket table and update your own
	            
            else if (tag.equals("rel")){
            	int s1 = Integer.parseInt(st.nextToken());
            	this.mutualEx.receive(0, s1, "release");
            }//got release message
	            
            else if (tag.equals("relRead")){
            	int s1 = Integer.parseInt(st.nextToken());
            	this.mutualEx.receive(0, s1, "releaseRead");
            }//got read section release message
	            
            else if (tag.equals("Ack")){
            	int s1 = Integer.parseInt(st.nextToken());
            	this.mutualEx.receive(0,s1,"ack");
            }//got ack,counting them.
            
	            pout.flush();
	        }	       
		 catch (IOException e) {
			 System.out.println("System error..Trying again");
	        } 			
	    }//handle client
	 
	 void synchronize() throws IOException{
		 //synchronize your information when server are start
		 Socket flag = getsynSocket();
     	 System.out.println("Synchronizing...");
		 if (flag == null){
			 //only your self
			 this.NumOfServer=1;
			 return;
		 }
		 else{
			 while(true){
				 try{
			 din = new BufferedReader(new InputStreamReader(flag.getInputStream()));
         pout = new PrintStream(flag.getOutputStream());
         pout.println("SynchronizeTable");
			
			 ObjectInputStream is = new
                 	ObjectInputStream(flag.getInputStream());
         	 ObjectOutputStream os = new
     	        	ObjectOutputStream(flag.getOutputStream());
	        server.Ticket joe=(server.Ticket)is.readObject();
     		this.Tic.UpdateTicket(joe);
     		break;
     		}
   		 catch(Exception e2){
			 System.out.println("System error..Trying again");
		 }//synchronize ticket table
				 
			while(true){
				try{
     		Socket galf = getsynSocket();
     		din = new BufferedReader(new InputStreamReader(galf.getInputStream()));
            pout = new PrintStream(galf.getOutputStream());
     	pout.println("SynchronizeClock");		
		 ObjectInputStream iss = new
              	ObjectInputStream(galf.getInputStream());
      	 ObjectOutputStream oss = new
  	        	ObjectOutputStream(galf.getOutputStream());
     		server.Clock tom = (server.Clock)iss.readObject();
 		this.mutualEx.c =tom;
 		break;
 		}
		 catch(Exception e2){
		 System.out.println("System error..Trying again");
	 }//synchronize clock
            return;}
		 }
		}
	 }
	 
	  public Socket getsynSocket() throws IOException {
		  //randomly get someone to synchronize when you start up 
		  int a=this.id+1;
	    	int count = NumOfPro;
		  while(true){
	        	try{
	        	Socket syn= new Socket(table.getHostName(a), table.getPort(a));
	  		  return syn;
	        }catch (Exception e) {
	            System.out.println("Fail to find #"+a+" Server...Trying Again...");
	            if(a<count-1){a++;}
	            else {a=0;}
	            if(a==this.id){return null;}
	            }
	        } 
	        }

	 public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
	        Server ns = new Server();
        	ServerSocket listener = ns.ServerInit(); // initial server
        	ns.synchronize();               //synchronize with others
        	System.out.println("Synchronization Success!");

	        while (true) {
	        	try{// begin handling client and other servers with multiple threads
			    ServerThread t = new ServerThread(listener,ns);
                Socket aClient = listener.accept();
			    Thread t1 = new Thread(t);
				t1.start();
                ns.handleclient(aClient);
                aClient.close();
	        	}
	        	catch(Exception e){
	            	System.out.println("Server Failed...Trying again");
	        	}
			}
	    }
}