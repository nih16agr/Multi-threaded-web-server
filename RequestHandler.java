import java.io.*;
import java.net.*;
import java.util.*;

public class RequestHandler implements Runnable{
	String crlf="\r\n";
	Socket clientSocket;

	public RequestHandler(Socket s){
		this.clientSocket=s;
	}

	public void run(){
		try {
                	process();
        	} catch (Exception e) {
                	System.out.println(e);
        	}
	}


	private void process() throws Exception{
		OutputStream os = clientSocket.getOutputStream();
		InputStream is=clientSocket.getInputStream();
		BufferedReader inStream = new BufferedReader(new InputStreamReader(is)); //read data over socket input stream
		DataOutputStream opStream= new DataOutputStream(os);//write data over socket outputstream

		String requestLine=inStream.readLine();// store request messages in packet line by line
		System.out.println();
		System.out.println(requestLine);// print initial request line

		String headerLine = null;
		while ((headerLine = inStream.readLine()).length() != 0) {
        		System.out.println(headerLine);     //print header line of request
		}

		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken();  // skip over the method name, which should be "GET"
		String fileName = tokens.nextToken();

		fileName = "." + fileName;//make the filePath relative to the current location
		FileInputStream fis = null;//reads file from the local file system
		boolean fileExists = true;
		try {
        		fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
        		fileExists = false;
		}
		
		// Construct the response message.
		String statusLine = null;
		String contentTypeLine = null;
		String messageBody = null;
		if (fileExists) {
        		statusLine = "HTTP/1.0 200 OK" + crlf;
        		contentTypeLine = "Content-type: " + typeOfContent( fileName ) + crlf;
		} else {
        		statusLine ="HTTP/1.0 404 Not Found" + crlf;
        		contentTypeLine = "Content-type: text/html" + crlf;
        		messageBody = "<HTML>" +
				 "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
				"<BODY>Not Found</BODY></HTML>";
		}

		opStream.writeBytes(statusLine);
		opStream.writeBytes(contentTypeLine);
		opStream.writeBytes(crlf);
		
		// Send the message body.
		if (fileExists) {
        		sendBytes(fis, opStream);
        		fis.close();
		} else {
        		opStream.writeBytes(messageBody);
		}

		opStream.close();
		inStream.close();
		clientSocket.close();
        }
	
	private static void sendBytes(FileInputStream fis, OutputStream opStream) throws Exception
	{
        	// Construct a 1K buffer to hold bytes on their way to the socket.
        	byte[] buffer = new byte[1024];
        	int bytes = 0;

        	// Copy requested file into the socket's output stream.
        	while((bytes = fis.read(buffer)) != -1 ) {
                	opStream.write(buffer, 0, bytes);
        	}
	}
	private static String typeOfContent(String fileName)
	{
        	if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
                	return "text/html";
        	}
        	else if(fileName.toLowerCase().endsWith(".gif")){
                	return "gif";
        	}
        	else if(fileName.toLowerCase().endsWith(".jpg")||fileName.toLowerCase().endsWith(".jpeg")||fileName.toLowerCase().endsWith(".jpg")){
                	return "image/jpeg";
        	}
        	return "application/octet-stream";
	} 
}