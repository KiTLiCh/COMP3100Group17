package main.java;

import java.util.List;

public class BestFitScheduler implements Scheduler {


    public SchedulingDecision generateDecision(Job aJob, List<Server> serverTypeList, List<Server> dynamicServerList) {
        int bestFit = Integer.MAX_VALUE;
        int minAvail = Integer.MAX_VALUE;
        int fitnessVal = 0;
        Server bestServer = null;

        for (Server serverType : serverTypeList) {

            List<Server> resultList = Server.filterByType(serverType.getType(), dynamicServerList);

            for (Server server : resultList) {
                if (server.getCoreCount() >= aJob.getCores()) {
                    fitnessVal = server.getCoreCount() - aJob.getCores();

                    if ((fitnessVal < bestFit) || (fitnessVal == bestFit && server.getAvailableTime() < minAvail)) {
                        bestFit = fitnessVal;
                        bestServer = server;
                        minAvail = server.getAvailableTime();

                    }
                }
            }
        }

        if (bestServer != null) {
            SchedulingDecision theDecision = new SchedulingDecision(aJob.jobID, bestServer.getType(), bestServer.getID());
            return theDecision;
        } else {
            SchedulingDecision resultDes = getBestFitActive(aJob, serverTypeList, dynamicServerList);
            return resultDes;
        }

    }

    ;

    public SchedulingDecision getBestFitActive(Job aJob, List<Server> serverTypeList, List<Server> dynamicServerList) {
        int bestFit = Integer.MAX_VALUE;
        int minAvail = Integer.MAX_VALUE;
        int fitnessVal = 0;
        Server bestServer = null;
        Server bestActiveServer = null;

        for (Server serverType : serverTypeList) {

            List<Server> resultList = Server.filterByType(serverType.getType(), dynamicServerList);

            for (Server server : resultList) {
                if (server.getCoreCount() >= aJob.getCores()) {
                    fitnessVal = server.getCoreCount() - aJob.getCores();

                    if ((fitnessVal < bestFit) || (fitnessVal == bestFit && server.getAvailableTime() < minAvail)) {
                        bestFit = fitnessVal;
                        bestServer = server;
                        minAvail = server.getAvailableTime();

                        if (bestServer.getState() == 3) {
                            bestActiveServer = bestServer;
                        }
                    }
                }
            }
        }
        int maxCores = 0;
        Server largestServer = null;
        for(Server server:serverTypeList){
        	if(server.coreCount > maxCores){
        		maxCores = server.coreCount;
        		largestServer = server;
        	}
        }
        SchedulingDecision theDecision = new SchedulingDecision(aJob.jobID, largestServer.type, 0);
        return theDecision;
    }
}