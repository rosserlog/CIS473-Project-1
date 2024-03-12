import java.io.*; 
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;
import java.nio.file.*;
class FTPClient { 

    public static void main(String argv[]) throws Exception 
    { 
        String sentence; 
        String modifiedSentence; 
        boolean isOpen = true;
        int number=1;
        boolean notEnd = true;
		int port1=1221;
		int port = 1200;
		String statusCode;
		boolean clientgo = true;
	    
	System.out.println("Welcome to the simple FTP App   \n     Commands  \nconnect servername port# connects to a specified server \nlist: lists files on server \nget: fileName.txt downloads that text file to your current directory \nstor: fileName.txt Stores the file on the server \nclose terminates the connection to the server");
	BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
    sentence = inFromUser.readLine();
    StringTokenizer tokens = new StringTokenizer(sentence);


	if(sentence.startsWith("connect")){
		String serverName = tokens.nextToken(); // pass the connect command
		serverName = tokens.nextToken();
		port1 = Integer.parseInt(tokens.nextToken());
		System.out.println("You are connected to " + serverName);
		Socket ControlSocket= new Socket(serverName, port1);
		while(isOpen && clientgo)
		{      

			DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream()); 
			DataInputStream inFromServer = new DataInputStream(new BufferedInputStream(ControlSocket.getInputStream()));
			sentence = inFromUser.readLine();
		
			
			if(sentence.equals("list:"))
			{
				
			port = port +2;
			System.out.println(port);
			ServerSocket welcomeData = new ServerSocket(port);


			System.out.println("\n \n \nThe files on this server are:");
			outToServer.writeBytes (port + " " + sentence + " " + '\n');

			Socket dataSocket =welcomeData.accept(); 
			DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
				while(notEnd) 
				{
					modifiedSentence = inData.readUTF();
					if(modifiedSentence.equals("eof"))
						break; 
				System.out.println("	" + modifiedSentence); 
				}

			welcomeData.close();
			dataSocket.close();
			System.out.println("\nWhat would you like to do next: \nget: file.txt ||  stor: file.txt  || close");

			}

			// else if(sentence.startsWith("get: "))
			// {
			// .........................................................
			// .........................................................
			// .........................................................
			// }
			
			else if(sentence.startsWith("stor: "))
			{
				//extract filename from command
				//String filename = tokens.nextToken();
				String filename = sentence.substring(6);
				System.out.println(filename);



				// //verify file
				Path filepath = Paths.get(filename);
				if(!Files.exists(filepath)){
				 	System.out.println("File not found");
				 	break;
				}
				else{
					System.out.println("THIS IS A FILE!");
				}

				// //open socket and outputstream
				// Socket dataSocket = new Socket(serverName, port1 + 1);
				// DataOutputStream outData = new DataOutputStream(dataSocket.getOutputStream());

				// //send server port, stor command, and filename
				// outToServer.writeBytes(port + " " + sentence + " " + '\n');

				// //Send file over line by line
				

			}

			else{
				if(sentence.equals("close"))
				{
					outToServer.writeBytes (port + " " + sentence + " " + '\n');
				    ControlSocket.close();
				}
				break;
			}	

		}			  
	}
  }
}