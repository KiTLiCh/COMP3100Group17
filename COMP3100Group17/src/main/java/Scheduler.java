package main.java;

import java.util.List;

public interface Scheduler {

    public SchedulingDecision generateDecision(Job aJob, List<Server> serverList, List<Server> dynamicServerList);
}