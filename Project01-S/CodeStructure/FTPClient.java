import java.io.*; 
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;
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
	    
	System.out.println("\nWelcome to the simple FTP App   \n\n*****Commands*****  \nconnect servername port# connects to a specified server \nlist: lists files on server \nget: fileName.txt downloads that text file to your current directory \nstor: fileName.txt Stores the file on the server \nclose terminates the connection to the server\n");
	BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
    sentence = inFromUser.readLine();
    StringTokenizer tokens = new StringTokenizer(sentence);


	if(sentence.startsWith("connect")){
		String serverName = tokens.nextToken(); // pass the connect command
		serverName = tokens.nextToken();
		port1 = Integer.parseInt(tokens.nextToken());
		System.out.println("\n***** You are connected to " + serverName + " *****\n");
		Socket ControlSocket= new Socket(serverName, port1);
		while(isOpen && clientgo)
		{      

			DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream()); 
			DataInputStream inFromServer = new DataInputStream(new BufferedInputStream(ControlSocket.getInputStream()));
			sentence = inFromUser.readLine();
		
			
			if(sentence.equals("list:"))
			{
				
			port = port +2;
			ServerSocket welcomeData = new ServerSocket(port);

			System.out.println("\n \nThe files on this server are: " );

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
			System.out.println("\nWhat would you like to do next: \nget: file.txt ||  stor: file.txt  || close\n");

			}

			else if(sentence.startsWith("get: "))
			{
				//supposed to get file name
				String filename = sentence.substring(4).trim();

				//Sets up data connection since get uses it
				System.out.println("\n \n \nThe data connection for get has been created:");
			   
				//send the request over the control connection outToServer/inToServer = control connection
				System.out.println("\n \n \nRequested File is:");
				System.out.println("Hello");
				port = port +2;
				ServerSocket welcomeData = new ServerSocket(port);
				outToServer.writeBytes (port + " " + sentence + " " + '\n');

				
				String error = inFromServer.readUTF();
				if(error.equals("F")){
					System.out.println("File not existing");
					System.out.println("\nWhat would you like to do next: \nget: file.txt ||  stor: file.txt  || close");
					welcomeData.close();
				}
				else{
					Socket dataSocket =welcomeData.accept();
					DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
				
					BufferedReader dataReader = new BufferedReader(new InputStreamReader(inData));
					BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filename));
					String line;
		
					while(notEnd)
					{
						line = inData.readUTF();
						if(line.equals("eof"))
							break;
					//    System.out.println("  " + modifiedSentence);
				   
						fileWriter.write(line);
						//fileWriter.newLine();
					}
					System.out.println(" " + filename);
		
		
					welcomeData.close();
					dataSocket.close();
					dataReader.close();
					fileWriter.close();
					
					System.out.println("\nWhat would you like to do next: \nget: file.txt ||  stor: file.txt  || close");
				}
		
			}
			
			else if(sentence.startsWith("stor: "))
			{
				String filename = sentence.substring(6);
				File file = new File(filename);

				// //verify file
				if(!file.exists()){
				 	System.out.println("***** File " + filename + " not found *****");
					System.out.println("\nWhat would you like to do next: \nget: file.txt ||  stor: file.txt  || close\n");
				}				
				else{
					port = port +2;
					ServerSocket welcomeData = new ServerSocket(port);
	
					System.out.println("\nStoring file: " + filename);
	
					outToServer.writeBytes (port + " " + sentence + " " + '\n');
					Socket dataSocket =welcomeData.accept();
	
					DataOutputStream  dataOutToClient =
					new DataOutputStream(dataSocket.getOutputStream());
					BufferedWriter dataWriter = new BufferedWriter(new OutputStreamWriter(dataOutToClient));
					BufferedReader fileReader = new BufferedReader(new FileReader(filename));
					String line;
	
					while ((line = fileReader.readLine()) != null) {
						dataOutToClient.writeUTF(line + "\r\n");
	
					}
					dataOutToClient.writeUTF("eof");
					dataWriter.newLine();
	
					System.out.println("\n*****Process Complete*****");
	
					welcomeData.close();
					dataWriter.close();
					fileReader.close();
					dataSocket.close();
	
					System.out.println("\nWhat would you like to do next: \nget: file.txt ||  stor: file.txt  || close\n");
				}


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