package com.gearreald.tullfileclient.job;

import java.util.UUID;

import com.gearreald.tullfileclient.worker.HardStopException;
import com.gearreald.tullfileclient.worker.WorkerException;
import com.gearreald.tullfileclient.worker.WorkerQueues;

/**
 * An abstract class for jobs and their associated methods.
 * @author Tull Gearreald
 */
public abstract class Job {
	private final UUID uuid;
	
	private static final int DEFAULT_RETRIES=5;
	private boolean attempted;
	private boolean done;
	
	public Job(){
		this.uuid=UUID.randomUUID();
		this.attempted=false;
		this.done=false;
	}
	protected void completeJob(){
		this.done=true;
	}
	public boolean completed(){
		return this.done;
	}
	/**
	 * Gets the UUID associated with this job.
	 * @return A unique UUID object for this job.
	 */
	public UUID getUUID(){
		return this.uuid;
	}
	public abstract void work() throws WorkerException, HardStopException;
	public abstract void theJob() throws WorkerException;
	public abstract String getJobName();
	public int getRetries(){
		return Job.DEFAULT_RETRIES;
	}
	public void failPermanently() throws HardStopException{
		this.attempted=true;
		int failures = WorkerQueues.failCount(this);
		if(failures>this.getRetries()){
			throw new HardStopException(this.getFailureString());
		}
	}
	public String getFailureString(){
		return "The job "+getJobName()+" has failed too many times and is being abandoned.";
	}
	public boolean attempted(){
		return this.attempted;
	}
	@Override
	public int hashCode() {
		return this.getUUID().hashCode();
	}
	@Override
	public boolean equals(Object o){
		if(o==this)
			return true;
		if(!(o instanceof Job))
			return false;
		Job j = (Job) o;
		return j.getUUID().equals(this.getUUID());
	}
}
