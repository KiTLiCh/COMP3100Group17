package main.java;

import java.util.List;

public class FirstFitScheduler implements Scheduler{


    public SchedulingDecision generateDecision(Job aJob, List<Server> serverTypeList, List<Server> dynamicServerList){
        //given the passed job, choose a server to run the job on
        //select the first server that meets the needs of the job.
        Server firstFitServer = null;
        for (Server serverType:serverTypeList){
        	List<Server> resultList = Server.filterByType(serverType.getType(), dynamicServerList);
        	//Create a list of the specific servers that are of the specified type.
        	for(Server server:resultList){
        		//pass each server in
        		firstFitServer = server;
        		//Check if the core count of the current server is greater than or equal to what the job needs,
        		//Check the amount of jobs the server has is still less than its limit.
        		//If so create a decision for the job to be allocated to this server,
        		if(firstFitServer.getCoreCount() >= aJob.getCores()){
        		if( server.getID() >= 0 && server.getID() <= serverType.getLimit()-1) 
        		{
        			SchedulingDecision theDecision = new SchedulingDecision(aJob.getJobID(), firstFitServer.getType() ,firstFitServer.getID());
        			return theDecision;
        		} }
        	}
        }
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
        SchedulingDecision theDecision = new SchedulingDecision(aJob.getJobID(), largestServer.getType(), 0);
        return theDecision;
   }
}
