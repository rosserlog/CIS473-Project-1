import java.io.*; 
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

    public class ftpserver extends Thread{ 
    private Socket connectionSocket;
    int port;
    int count=1;
    boolean notEnd = true;

    public ftpserver(Socket connectionSocket)  {
	    this.connectionSocket = connectionSocket;
    }


    public void run() 
    {
        if(count==1){
            System.out.println("User connected" + connectionSocket.getInetAddress());
            count++;
        }

	    try {
		    processRequest();
		
	    } catch (Exception e) {
		    System.out.println(e);
	    }
	 
	}
	
	
	private void processRequest() throws Exception
	{
            String fromClient;
            String clientCommand;
            byte[] data;
            String frstln;
                    
            while(true)
            {
                if(count==1)
                    System.out.println("User connected" + connectionSocket.getInetAddress());
                count++;
         
                DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                fromClient = inFromClient.readLine();

                StringTokenizer tokens = new StringTokenizer(fromClient);
            
                frstln = tokens.nextToken();
                port = Integer.parseInt(frstln);
                clientCommand = tokens.nextToken();

                if(clientCommand.equals("list:"))
                { 
                    String curDir = System.getProperty("user.dir");
       
                    Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                    DataOutputStream  dataOutToClient = 
                    new DataOutputStream(dataSocket.getOutputStream());
                    File dir = new File(curDir);
    
                    String[] children = dir.list();
                    if (children == null) 
                    {
                        // Either dir does not exist or is not a directory
                    } 
                    else 
                    {
                        for (int i=0; i<children.length; i++)
                        {
                           // Get filename of file or directory
                            String filename = children[i];

                            if(filename.endsWith(".txt"))
                            {
                                dataOutToClient.writeUTF(children[i]);
                            }  
                            if(i-1==children.length-2)
                            {
                                dataOutToClient.writeUTF("eof");
                            }   
                          }

                        dataSocket.close();
                    }
                }


            if(clientCommand.equals("get:"))
            {
                String filename = fromClient.substring(10).trim();
                System.out.println("Client requested file: " + filename);
                File file = new File(filename);

                //If file doe not exist do not continue
                if (!file.exists()) {
                    outToClient.writeUTF("F");
                    System.out.println("File not found");
                    
                } 
                //If file exists continue to send over file
                else { 
                    outToClient.writeUTF("T");  

                    Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                    DataOutputStream  dataOutToClient =
                    new DataOutputStream(dataSocket.getOutputStream());

                    BufferedWriter dataWriter = new BufferedWriter(new OutputStreamWriter(dataOutToClient));
                    BufferedReader fileReader = new BufferedReader(new FileReader(file));
                    String line;
        
                    while ((line = fileReader.readLine()) != null) {
                        dataOutToClient.writeUTF(line + "\r\n");
                    }

                    dataOutToClient.writeUTF("eof");
                    dataWriter.newLine();
                    
                    //close sockets
                    dataWriter.close();
                    fileReader.close();
                    dataSocket.close();
                }

            }


             if(clientCommand.equals("stor:")){
                String filename = fromClient.substring(11);
                File file = new File(filename);
                System.out.println("***** Storing file: " + filename + " *****");

                Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);

                DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
                BufferedReader dataReader = new BufferedReader(new InputStreamReader(inData));
				BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filename));
				String line;

                while(notEnd)
					{
						line = inData.readUTF();
						if(line.equals("eof"))
							break;

						fileWriter.write(line);
					}

                dataSocket.close();
                dataReader.close();
                fileWriter.close();
             }


            if(clientCommand.equals("close")){
                connectionSocket.close();
            }
        }
    }

    public static void main(String[] args) {
        try {
            int portNumber = 1235; 
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("***** FTP Server is running on port " + portNumber + " *****");

            while (true) {
                Socket connectionSocket = serverSocket.accept();
                ftpserver server = new ftpserver(connectionSocket);
                server.start();
            }
        } catch (IOException e) {
            System.err.println("***** Could not start the server on the specified port. *****");
            e.printStackTrace();
        }
    }
}
	

