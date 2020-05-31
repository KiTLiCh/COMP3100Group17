package main.java;

import java.util.List;

public class CheapestScheduler implements Scheduler{


    public SchedulingDecision generateDecision(Job aJob, List<Server> serverTypeList, List<Server> dynamicServerList){
        //given the passed job, choose a server to run the job on
        //select the server with the shortest turn around time.
        Server currentServer = dynamicServerList.get(0);
        Server cheapServer = null;
        float costRate = currentServer.getRate();
        for (Server serverType:serverTypeList){
        	List<Server> resultList = Server.filterByType(serverType.type, dynamicServerList);
        	//Create a list of the specific servers that are of the specified type.
        	for(Server server:resultList){
        		//pass each server in
        		currentServer = server;
        		//Check the server has enough cores and disk space for the job,
        		//check servers job limit, then check costRate to server rate,
        		//If less set as cheapest server.
        		if(server.getCoreCount() >= aJob.getCores()
                && server.getMemory() >= aJob.getMemory()
        		&& currentServer.getDisk() >= aJob.getDisk()
        		&& server.getID() < serverType.getLimit()		
        		&& server.getRate()<costRate) 
        		{
        			cheapServer = server;
        			costRate = server.getRate();
        		}
        	}
        }
        //if cheapest server is found set scheduling decision.
        if(cheapServer!=null) {
        SchedulingDecision theDecision = new SchedulingDecision(aJob.getJobID(), cheapServer.getType(), cheapServer.getID());
        cheapServer.setJobCount(cheapServer.getJobCount()+1, aJob.getEstimatedRuntime());
		return theDecision;
        }
        // Default
        // If no server is found to be available than check the server type list for the largest sized server and
        // allocate the job to this server.
        int maxCores = 0;
        Server largestServer = null;
        for(Server server:serverTypeList){
        	if(server.getCoreCount() > maxCores){
        		maxCores = server.getCoreCount();
        		largestServer = server;
        	}
        }
        // create and return the scheduling decision
        SchedulingDecision theDecision = new SchedulingDecision(aJob.getJobID(), largestServer.getType(), 0);
        return theDecision;
   }
}
