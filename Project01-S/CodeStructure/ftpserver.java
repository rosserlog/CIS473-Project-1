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
            
      		    //System.out.println(fromClient);
                StringTokenizer tokens = new StringTokenizer(fromClient);
            
                frstln = tokens.nextToken();
                port = Integer.parseInt(frstln);
                clientCommand = tokens.nextToken();
                //System.out.println(clientCommand);


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
                            //System.out.println(filename);
                            if(i-1==children.length-2)
                            {
                                dataOutToClient.writeUTF("eof");
                                // System.out.println("eof");
                            }//if(i-1)

     
                          }//for

                        dataSocket.close();
		            //System.out.println("Data Socket closed");
                    }//else
        

                }//if list:


            if(clientCommand.equals("get:"))
            {
                String filename = clientCommand.substring(4).trim();
                //String filename = tokens.nextToken();
                System.out.println("Client requested file: " + filename);


                File file = new File(filename);
               
                //Check if the file exists send correct satus code
                if (!file.exists()) {
                    outToClient.writeBytes("550 File not found\r\n");
                } else {
                    outToClient.writeBytes("200 Command Ok\r\n");
                }


                Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                  DataOutputStream  dataOutToClient =
                  new DataOutputStream(dataSocket.getOutputStream());

                //might need to use scanner idk gotta read file handling pdf.
                BufferedWriter dataWriter = new BufferedWriter(new OutputStreamWriter(dataOutToClient));
                BufferedReader fileReader = new BufferedReader(new FileReader(filename));
                String line;


                while ((line = fileReader.readLine()) != null) {
                    dataWriter.write(line);
                    dataWriter.newLine();
                }
                dataOutToClient.writeUTF("eof");
                dataWriter.newLine();
               
                //close socket
                dataWriter.close();
                fileReader.close();
                dataSocket.close();

             } //main

            //  if(clientCommand.equals("stor: ")){
            //     String filename = tokens.nextToken();
            //     File file = new File(filenmae);

            //     ServerSocket dataSocket = new ServerSocket(port);
            //     Socket dataConnection = dataSocket.accoet();
            //     DataInputStream inFromClient = new DataInputStream(new BufferedInputStream(dataConnection.getInputStream()));


            //  }

            if(clientCommand.equals("close")){
                connectionSocket.close();
            }
        }
    }

    public static void main(String[] args) {
        try {
            int portNumber = 1235; // Change this to your desired port number
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("FTP Server is running on port " + portNumber);

            while (true) {
                Socket connectionSocket = serverSocket.accept();
                ftpserver server = new ftpserver(connectionSocket);
                server.start();
            }
        } catch (IOException e) {
            System.err.println("Could not start the server on the specified port.");
            e.printStackTrace();
        }
    }
}
	

