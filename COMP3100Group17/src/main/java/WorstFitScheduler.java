package main.java;

import java.util.List;

public class WorstFitScheduler implements Scheduler {

    public SchedulingDecision generateDecision(Job aJob, List<Server> serverTypeList, List<Server> dynamicServerList){
        //set variables
        int worstFit = Integer.MIN_VALUE;
        Server worstServer = null;
        int altFit = Integer.MIN_VALUE;
        Server altServer = null;
        int fitnessVal = 0;
        //this list contains server objects parsed in from system.xml, therefore server types
        for (Server serverType:serverTypeList ) {
            //call this to get a list of all servers of that type
            List<Server> resultList = Server.filterByType(serverType.getType(), dynamicServerList);
            for (Server server:resultList) {
                //from 0 to limit-1
                if(server.getID() >= 0 && server.getID() <= serverType.getLimit()-1) {
                    //dont calculate negative fitness values
                    if (server.getCoreCount() >= aJob.getCores()) {
                        fitnessVal = server.getCoreCount() - aJob.getCores();
                        //choose worst fit based on number of cores
                        if (fitnessVal > worstFit) {
                            worstFit = fitnessVal;
                            worstServer = server;
                        } else if (fitnessVal > altFit && server.getAvailableTime() < 10000) {
                            //only if available in a short amount of time
                            altFit = fitnessVal;
                            altServer = server;
                        }
                    }
                }
            }

        }
        //we have found a worst fit server
        if(worstServer != null){
            SchedulingDecision theDecision = new SchedulingDecision(aJob.jobID, worstServer.getType() , worstServer.getID());
            return theDecision;
        }else if(altServer != null){
            //we didnt find a worst fit server but we have an alternate fit
            SchedulingDecision theDecision = new SchedulingDecision(aJob.jobID, altServer.getType() , altServer.getID());
            return theDecision;
        }else{
            //we need to return the worst-fit active server based on intial resource capacity...
            //System.out.println("this is where the worst-fit active server goes");
            SchedulingDecision resultDes = getWorstFitActive(aJob,serverTypeList,dynamicServerList);
            return resultDes;

        }

    };

    public SchedulingDecision getWorstFitActive(Job aJob, List<Server> serverTypeList, List<Server> dynamicServerList){
        int worstFit = Integer.MIN_VALUE;
        Server worstServer = null;
        Server worstActiveServer = null;
        int fitnessVal = 0;
        //contains server objects parsed in from system.xml, therefore server types
        for (Server serverType:serverTypeList ) {
            //call to get a list of all servers of that type
            List<Server> resultList = Server.filterByType(serverType.getType(), dynamicServerList);
            for (Server server:resultList) {

                if(serverType.getCoreCount() >= aJob.getCores()) {
                    fitnessVal = serverType.getCoreCount() - aJob.getCores();
                    if (fitnessVal > worstFit) {
                        worstFit = fitnessVal;
                        worstServer = server;
                        //compare to active server (state 3)or booting server (state 1)
                        if (worstServer.getState() == 3 || worstServer.getState() == 1) {
                            worstActiveServer = worstServer;
                        }
                    }
                }
            }

        }
        if(worstActiveServer != null) {
            SchedulingDecision theDecision = new SchedulingDecision(aJob.jobID, worstActiveServer.getType(), worstActiveServer.getID());
            return theDecision;
        }else if(worstServer != null){
            //just incase, a back up
            SchedulingDecision theDecision = new SchedulingDecision(aJob.jobID, worstServer.getType(), worstServer.getID());
            return theDecision;
        }else{
            //use all to largest
            int maxCores = 0;
            Server largestServer = null;
            for (Server server:serverTypeList) {
                if(server.coreCount > maxCores){
                    maxCores = server.coreCount;
                    largestServer = server;
                }
            }

            if(largestServer != null) {
                SchedulingDecision theDecision = new SchedulingDecision(aJob.jobID, largestServer.type, 0);
                return theDecision;
            }else{
                return null;
            }
        }

    }

}