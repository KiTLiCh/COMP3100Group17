package main.java;

import java.util.List;

public class VanillaScheduler implements Scheduler {


    public SchedulingDecision generateDecision(Job aJob, List<Server> serverList, List<Server> dynamicServerList){
        //given the passed job, and the aim choose a server to run it on
        //select largest server and make that the decision
        int maxCores = 0;
        Server largestServer = null;
        for (Server server:serverList) {
            if(server.coreCount > maxCores){
                maxCores = server.coreCount;
                largestServer = server;
            }
        }

        SchedulingDecision theDecision = new SchedulingDecision(aJob.jobID, largestServer.type ,0);
        return theDecision;



    }

}