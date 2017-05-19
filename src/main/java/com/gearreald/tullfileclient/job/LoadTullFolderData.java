package com.gearreald.tullfileclient.job;

import java.io.IOException;

import org.json.JSONException;

import com.gearreald.tullfileclient.models.TullFolder;
import com.gearreald.tullfileclient.worker.WorkerException;

public class LoadTullFolderData extends Job {

	public TullFolder folder;
	public boolean done;
	public final static String JOB_NAME = "LoadData";
	
	public LoadTullFolderData(TullFolder f){
		this.folder=f;
		this.done=false;
	}
	public void work() throws WorkerException {
		try {
			this.folder.fetchFolderData(false);
			this.done=true;
		} catch (IOException e) {
			throw new WorkerException("There was an error fetching the folder data.",e);
		} catch (JSONException e) {
			throw new WorkerException("The reponse from the server was malformed.",e);
		}
	}
	@Override
	public String getJobName() {
		return JOB_NAME;
	}
	@Override
	public boolean completed() {
		return this.done;
	}
}