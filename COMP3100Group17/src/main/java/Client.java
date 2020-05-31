package main.java;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Client {

	// input and output streams
	private Socket socket = null;
	private PrintStream socketPrint;
	private BufferedReader socketReader = null;
	public static String cmd_HELO = "HELO";
	public static String cmd_AUTH = "AUTH xxxx";
	public static String cmd_REDY = "REDY";
	public static String cmd_QUIT = "QUIT";
    public static String cmd_SCHD = "SCHD";
    public static String cmd_OK = "OK";
	public static String resp_OK = "OK";
	public static String resp_JOBN = "JOBN";
    public static String resp_QUIT = "QUIT";
    public static String resp_DATA = "DATA";
    public static String resp_DOT = ".";
	private Scheduler activeScheduler;
    List<Server> serverTypeList = new ArrayList<>();
    List<Server> dynamicServerList = new ArrayList<>();
	

	public Client(String address, int port) {

		try {
			socket = new Socket(address, port);
			System.out.println("Connected");
		} catch(UnknownHostException h){
			System.out.println();
		} catch(IOException i) {
			System.out.println("tried to connect: " + i);
		}

		//create a reader to receive messages from server and printer to send messages to server
		try{
			socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// Use string friendly printStream
			socketPrint = new PrintStream(socket.getOutputStream());
		} catch(IOException i) {
			System.out.println("tried to connect: " + i);
		}
	}

	// create connection and authorize
	private void doAuth(){
        //send HELO to server
        socketPrint.print(cmd_HELO);

        try {
            //wait for response
            if (waitForResponse().compareTo(resp_OK) == 0){
                System.out.println("successfully received");
                //next send REDY
                socketPrint.print(cmd_AUTH);
                if (waitForResponse().compareTo(resp_OK) == 0){
                    //go to next stage, AUTH done
                    System.out.println("Authorization completed");
                }
            }
        }
        catch (IOException ioExp) {
            // Cannot get response from server
            System.out.println("Failed to get response from server");
        }

	}

	//method to create servers from system.xml
    public void getListOfServers(String systemXmlPath){
	    //parse the xml file in, and create an object for each server in the file.
        try {
            //modify file location.
            File file = new File(systemXmlPath);
            
            if(!file.exists()) {
                System.out.println("File not found from file path");
                FileNotFoundException notFound = new FileNotFoundException(systemXmlPath);
                throw notFound;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document serverFile = dBuilder.parse(file);
            //check what this does
            serverFile.getDocumentElement().normalize();

            NodeList nList = serverFile.getElementsByTagName("server");

            for(int id = 0; id < nList.getLength(); id++) {
                //create a new object and append it and our elements to our server list
                Node nNode = nList.item(id);


                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String type = eElement.getAttribute("type");
                    int limit = Integer.parseInt(eElement.getAttribute("limit"));
                    int bootupTime = Integer.parseInt(eElement.getAttribute("bootupTime"));
                    float rate = Float.parseFloat(eElement.getAttribute("rate"));
                    int coreCount = Integer.parseInt(eElement.getAttribute("coreCount"));
                    int memory = Integer.parseInt(eElement.getAttribute("memory"));
                    int disk = Integer.parseInt(eElement.getAttribute("disk"));

                    //add these to a server objects
                    Server aServer = new Server(type, limit, bootupTime, rate, coreCount, memory, disk);
                    //add server objects to list
                    serverTypeList.add(aServer);
                    }

            }



        }catch(Exception e){
            System.out.println("Parsing error" + e);
        }
    }

    //send REDY to the server, to say we are available to receive a job
    public void sendReady(){
	    socketPrint.print(cmd_REDY);
    }


    //get job from server, create job object and add to list
    //return a job if there is a job or null otherwise
    public Job getJob(){
	    sendReady();
        //get the response
        try {
            String aResponse = waitForResponse();
            //parse the response to see if there is a job or NONE
            String command = aResponse.substring(0,resp_JOBN.length());

            //WILL NEED TO MODIFY THIS IN FUTURE TO TAKE IN OTHER COMMANDS
            if(command.compareTo(resp_JOBN) == 0){
                //we got a job
                Job aJob = new Job(aResponse);
                return aJob;

            }else{
                //none
                return null;
            }
        }catch(IOException ioExcp){
            System.out.println("Failed to get response from server");
        }
        //need to get scheduling decision
        //need to respond to the server a
        return null;

    }

    //may change return type
    //get the scheduling decision from the scheduler
    public SchedulingDecision getSchedulingDescicion(Job aJob){
	    //with the scheduler of a choice, find out which server to choose for the job
        SchedulingDecision resultDecision = activeScheduler.generateDecision(aJob, serverTypeList, dynamicServerList);
        return resultDecision;
    }

    //use the scheduling decision to send this decision back to the server a.k.a schedule the job
    public void scheduleJob(Job aJob){
        //get the scheduling decision for our current job
        SchedulingDecision theDecision = getSchedulingDescicion(aJob);
        //return that job to the server
        if(theDecision == null){
            //print
        }else{
            //create the command to the server
            String command = cmd_SCHD + " " + theDecision.toString();
            socketPrint.print(command);
            //wait for OK
            try {
                if (waitForResponse().compareTo(resp_OK) == 0) {
                    //we done
                }
            }catch(IOException ioExp){
                System.out.println("main.java.Job schedule not completed");
            }
        }
    }

    public void setActiveScheduler(Scheduler aScheduler){
	    activeScheduler = aScheduler;
    }
    
    //Sends Quit Command to the server to end connection
    public void sendQuit(){
	    try{
	        socketPrint.print(cmd_QUIT);
	        if(waitForResponse().compareTo(resp_QUIT)==0){
	            terminateProgram();
            }
        }catch(IOException ioExp){
            System.out.println("Did not get quit response");
        }
    }
    
    //Terminates the program, closes connection to server then exits.
	private void terminateProgram(){
		try {
			socketPrint.close();
			socketReader.close();
			socket.close();
			System.exit(0);
		}
		catch(IOException i){
			System.out.println(i);
		}
	}

    //return a string containing the response from the server
    public String waitForResponse() throws IOException {
        StringBuffer respBuffer = new StringBuffer();

        //wait for start of response
        int firstByte = socketReader.read();
        // Append to response buffer
        respBuffer.append((char)firstByte);

        // Now read until there is no more data pending
        while(socketReader.ready()){
            //read from server
            int nextByte = socketReader.read();
            // Append to response buffer
            respBuffer.append((char)nextByte);

        }
        return respBuffer.toString();
    }
    
    // Choose which algorithm to sort jobs by, default to all to largest if no parameters are specified.
    public void setAlgorithm(String args[], Client client) {
    	
    	Scheduler myScheduler = null;

    	if(args.length>1) {
    		if (args[0].compareTo("-a") == 0) {
    		String algType = args[1].toLowerCase();
    		System.out.println("The Chosen Algorithm is: ");
    		switch(algType)
    		{
	    		case "ff":
	    			System.out.println("FirstFit");
	    			myScheduler = new FirstFitScheduler();
	    			break;    			
	    		case "wf":
	   				System.out.println("WorstFit");
	   				myScheduler = new WorstFitScheduler();
	   				break;
	   			case "bf":
    				System.out.println("BestFit");
	    			myScheduler = new BestFitScheduler();
	    			break;
	   			case "tt":
	   				System.out.println("TurnTime");
	   				myScheduler = new TurnAroundTimeScheduler();
	   				break;
	   			case "cr":
	   				System.out.println("CheapestRate");
	   				myScheduler = new CheapestScheduler();
	   				break;
    			default:
    				System.out.println("Alert! No valid algorithm defined, defaulting to: AllToLargest");
    				myScheduler = new VanillaScheduler();
    		}
    	}} else 
    	{
    		System.out.println("Alert! No valid algorithm defined, defaulting to: AllToLargest");
    		myScheduler = new VanillaScheduler();
    	}
    	client.setActiveScheduler(myScheduler);        			
    }
    
    
public static void main(String args[]) {
		Client client = new Client("127.0.0.1", 50000);
		client.setAlgorithm(args, client);
		client.doAuth();
		client.getListOfServers("system.xml");

		//if the server list is empty don't proceed
        if(client.serverTypeList.size() == 0){
            //error
            System.out.println("No servers found from system.xml");
            System.out.println("Not processing jobs");
        }
        else
        {
            //get each job until there are no more a.k.a NONE
            boolean areThereMoreJobs = true;
            while(areThereMoreJobs){
                //get a job
                Job currentJob = client.getJob();
                //are we done?
                if(currentJob == null){
                    //we received NONE from the server
                    areThereMoreJobs = false;
                    
                }
                else
                {    
                	client.dynamicServerList = new ArrayList<>();
                    client.socketPrint.print("RESC All "+ currentJob.getCores() + " " + currentJob.getMemory() + " " + currentJob.getDisk());
                    try{
                        String response = client.waitForResponse();
                        while(response.compareTo(resp_DOT) != 0){
                                //while we are in the RESC response            
                                if (response.compareTo(resp_DATA) == 0){
                                    client.socketPrint.print(cmd_OK);
                                    response = client.waitForResponse();
                                }
                                else 
                                {
                                    //else we must have data in the response
                                    //split the data, HOPEFULLY don't contain spaces but check this
                                    String[] responseArray = response.split("\\s");
                                    //set information to servers to create a server list
                                    //get these from serverList
                                    //find the matching server type from serverList
                                    int foundIdx = 0;
                                    for(int idx= 0; idx < client.serverTypeList.size(); idx++){
                                        //check if matching types
                                        if(responseArray[0].compareTo(client.serverTypeList.get(0).getType()) == 0){
                                            //we found the matching
                                            foundIdx = idx;
                                        }
                                        //else do nothing
                                    }//while we are in the RESC response
                                    String type = responseArray[0];
                                    int limit = client.serverTypeList.get(foundIdx).getLimit();
                                    int bootupTime = client.serverTypeList.get(foundIdx).getBootupTime();
                                    float rate = client.serverTypeList.get(foundIdx).getRate();
                                    int coreCount = Integer.parseInt(responseArray[4]);
                                    int memory = Integer.parseInt(responseArray[5]);
                                    int disk = Integer.parseInt(responseArray[6]);

                                    //add these to a server object
                                    Server aServer = new Server(type, limit, bootupTime, rate, coreCount, memory, disk);
                                    //set extra variables
                                    aServer.setID(Integer.parseInt(responseArray[1]));
                                    aServer.setState(Integer.parseInt(responseArray[2]));
                                    aServer.setAvailableTime(Integer.parseInt(responseArray[3]));
                                    //add server object to list
                                    client.dynamicServerList.add(aServer);

                                    //reply OK and get next message
                                    client.socketPrint.print(cmd_OK);
                                    response = client.waitForResponse();
                                }
                        	}
                        }catch(IOException ioExp){
                        System.out.println("Did not get a response");
                    }
                    //request more information before this (to use in the decision)
                    // making the scheduling decision
                    client.scheduleJob(currentJob);
                }
            }
        }
        client.sendQuit();
	}
}