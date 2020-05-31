package main.java;


import java.util.ArrayList;
import java.util.List;

public class Server {
    //from system.xml
    String type;
    int limit;
    int bootupTime;
    float rate;
    int coreCount;
    int memory;
    int disk;

    int state = 0;
    int expectedTime;
    int ID;
    int availableTime;
    int jobCount;

    //Create a server object
    public Server(String type, int limit, int bootupTime, float rate, int coreCount, int memory, int disk) {
    	this.type = type;
        this.limit = limit;
        this.bootupTime = bootupTime;
        this.rate = rate;
        this.coreCount = coreCount;
        this.memory = memory;
        this.disk = disk;
        this.expectedTime = bootupTime;
    }
    
    //filter server list by type of server
    public static List<Server> filterByType(String type, List<Server> serverList){
        List<Server> resultList = new ArrayList<>();
        for (Server server: serverList) {
            //find servers in list that match given type
            if(server.getType().compareTo(type) == 0){
                //we have found a server of the desired type
                resultList.add(server);
            }
        }

        return resultList;
    }
    
    // Get Server ID
    public int getID() {
        return ID;
    }
    // Set Server ID
    public void setID(int id) {
        this.ID = id;
    }
    // Get Server Type
    public String getType() {
        return type;
    }
    // Set Server Type
    public void setType(String type) {
        this.type = type;
    }
    // Get Server Limit
    public int getLimit() {
        return limit;
    }
    // Set Server Limit
    public void setLimit(int limit) {
        this.limit = limit;
    }
    // Get Server BootupTime
    public int getBootupTime() {
        return bootupTime;
    }
    // Get Server AvailableTime
    public int getAvailableTime() {
        return this.availableTime;
    }
    // Set Server AvailableTime
    public void setAvailableTime(int bootupTime) {
        this.availableTime = bootupTime;
    }
    // Get Server Rate
    public float getRate() {
        return rate;
    }
    // Set Server Rate
    public void setRate(float rate) {
        this.rate = rate;
    }
    // Get Server Core Count
    public int getCoreCount() {
        return coreCount;
    }
    // Set Server Core Count
    public void setCoreCount(int coreCount) {
        this.coreCount = coreCount;
    }
    // Get Server Memory
    public int getMemory() {
        return memory;
    }
    // Set Server Memory
    public void setMemory(int memory) {
        this.memory = memory;
    }
    // Get Server Disk
    public int getDisk() {
        return disk;
    }
    // Set Server Disk
    public void setDisk(int disk) {
        this.disk = disk;
    }
    // Get Server State   
    public int getState() {
    	return state;
    }
    // Set Server State    
    public void setState(int state) {
    	this.state = state;
    }
    
    public int getJobCount() {
    	return jobCount; 
    }
    
    public int getExpectedTime() {
    	return expectedTime;
    }
    
    public void setJobCount(int job, int exp) {
    	this.expectedTime = this.expectedTime + exp;
    	this.jobCount = this.jobCount + job;
    }
 
}