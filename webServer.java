import java.io.*;
import java.net.*;
import java.util.*;


public class webServer{
	
	public static void main(String[] args) throws IOException{

		int port=8888;
		//if port number is specified use that one otherwise go by default
		if(args.length==1){
			port=Integer.parseInt(args[0]);
		}
		
		ServerSocket webserver=new ServerSocket(port);
		System.out.println("server is ready for connection with server port - "+port);
		while(true){
			// while loop is used so that server is always ready for new connection.
			Socket webclient=webserver.accept();
			RequestHandler request=new RequestHandler(webclient);
			new Thread(request).start();
			
		}
	}
}