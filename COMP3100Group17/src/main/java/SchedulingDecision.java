package main.java;

public class SchedulingDecision {
    int jobID;
    String serverType;
    int serverID;
    static String SERVTYPE_TINY = "tiny";
    static String SERVTYPE_SMALL = "small";
    static String SERVTYPE_MEDIUM = "medium";
    static String SERVTYPE_LARGE = "large";
    static String SERVTYPE_XLARGE = "xlarge";
    static String SERVTYPE_2XLARGE = "2xlarge";

    public int getJobID() {
        return jobID;
    }

    public void setJobID(int jobID) {
        this.jobID = jobID;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public int getServerID() {
        return serverID;
    }

    public void setServerID(int serverID) {
        this.serverID = serverID;
    }

    public SchedulingDecision(int jobID, String serverType, int serverID) {
        this.jobID = jobID;
        this.serverType = serverType;
        this.serverID = serverID;
    }

    //return the decision formatted as JOBID SERVERTYPE SERVERID
    public String toString(){
        return String.format("%d %s %d",jobID, serverType, serverID);
    }
}