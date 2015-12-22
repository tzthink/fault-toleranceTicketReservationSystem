package server;
import java.io.IOException;
import java.net.*;

class ServerThread implements Runnable{// multi thread for handling section
	Server ns;
	ServerSocket socket;
	ServerThread(ServerSocket socket, Server ns){
		this.socket = socket;
		this.ns = ns;
	}
	public void run(){
        try {
            Socket aClient = socket.accept();
		    Thread t1 = new Thread(this);
			t1.start();
            ns.handleclient(aClient);
            aClient.close();		
                return;
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Server aborted:" + e);
        }
	
	}
}
