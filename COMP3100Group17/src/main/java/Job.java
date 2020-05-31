package main.java;

import static java.lang.Integer.getInteger;
import static java.lang.Integer.valueOf;

public class Job {
    int submitTime;
    int jobID;
    int estimatedRuntime;
    int cores;
    int memory;

    public int getSubmitTime() {
        return submitTime;
    }

    public int getJobID() {
        return jobID;
    }

    public int getEstimatedRuntime() {
        return estimatedRuntime;
    }

    public int getCores() {
        return cores;
    }

    public int getMemory() {
        return memory;
    }

    public int getDisk() {
        return disk;
    }

    int disk;
    public Job(String jobCommand){
        //parse job command to initialise an object from it, in the format below
        // JOBN (string)
        //	submit_time	(int)	job_ID	(int)
        //estimated_runtime	(int)	#CPU_cores
        //(int)	memory	(int)	disk	(int)
        String[] jobParts = jobCommand.split(" ");
        //validate there are 7 parts
        if(jobParts.length == 7){
            submitTime = Integer.parseInt(jobParts[1]);
            jobID = Integer.parseInt(jobParts[2]);
            estimatedRuntime = Integer.parseInt(jobParts[3]);
            cores = Integer.parseInt(jobParts[4]);
            memory = Integer.parseInt(jobParts[5]);
            disk = Integer.parseInt(jobParts[6]);

        }
    }



}