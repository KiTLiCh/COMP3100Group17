package main.java;

import java.util.List;

public class TurnAroundTimeScheduler implements Scheduler{

	private int count = 0;
    public SchedulingDecision generateDecision(Job aJob, List<Server> serverTypeList, List<Server> dynamicServerList){
        //given the passed job, choose a server to run the job on
        //select the server with the shortest turn around time.
    	int currentTime = Integer.MAX_VALUE;
        Server shortestTimeServer = null;
        for (Server serverType:serverTypeList){
        	List<Server> resultList = Server.filterByType(serverType.type, dynamicServerList);
        	//Create a list of the specific servers that are of the specified type.
        	for(Server server:resultList){
        		//pass each server in
        		//Check the server has enough cores, disk space and memory for the job,
        		//get the servers current time and add the estimated job to get a turn around time.
        		//compare this with the previous turn around time, if less, set as the decision.
        		if(server.getCoreCount() >= aJob.getCores()
                	&& server.getDisk() >= aJob.getDisk() 
                	&& server.getMemory() >= aJob.getMemory() 
                	&& server.getID() < serverType.getLimit()
                	&& currentTime>aJob.getEstimatedRuntime()+server.getExpectedTime()
        			){	
        			currentTime = server.getExpectedTime()+aJob.getEstimatedRuntime();
        			shortestTimeServer = server;
        		}
        	}
        }
        // If a server is found to be available then
        // allocate the job to this server.
        if(shortestTimeServer!=null) {
        	SchedulingDecision theDecision = new SchedulingDecision(aJob.getJobID(), shortestTimeServer.getType(), shortestTimeServer.getID());
			return theDecision;
        }
        // Default
        // If no server is found to be available then check the server type list for the largest sized server and
        // allocate the job to this server, increasing to the limit of that type of server for each job.
        Server largestServer = null; 
        for(Server server:serverTypeList){
        	if(server.getCoreCount() >= aJob.getCores()
        	&& server.getDisk() >= aJob.getDisk() 
            && server.getMemory() >= aJob.getMemory() 
            && server.getID() < server.getLimit())
        	{
        		largestServer = server;
        	}
        }
		if(count>=largestServer.limit) {
			count =0;}
        SchedulingDecision theDecision = new SchedulingDecision(aJob.getJobID(), largestServer.getType(), count);
        count++;
        return theDecision;
   }
}
