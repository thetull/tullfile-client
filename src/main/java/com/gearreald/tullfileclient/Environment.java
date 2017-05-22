package com.gearreald.tullfileclient;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.gearreald.tullfileclient.controllers.InterfaceController;
import com.gearreald.tullfileclient.worker.Worker;

import javafx.stage.Stage;
import net.tullco.tullutils.NullUtils;

public class Environment {
	
	private static final int UPLOAD_WORKERS=1;
	private static final int DOWNLOAD_WORKERS=2;
	private static final int QUICK_WORKERS=2;
	
	private static HashMap<String,String> configuration = new HashMap<String,String>();
	
	private static Set<Worker> workerList = ConcurrentHashMap.<Worker>newKeySet();
	
	private static Stage primaryStage;
	private static InterfaceController interfaceController;
	
	public static void setConfiguration(String key, String value){
		configuration.put(key, value);
	}
	
	public static String getConfiguration(String key){
		return NullUtils.coalesce(configuration.get(key),"");
	}
	public static void setTesting(boolean testing){
		if(testing)
			setConfiguration("TESTING","t");
		else
			setConfiguration("TESTING","f");
	}
	public static boolean inTesting(){
		String testing = getConfiguration("TESTING");
		if(testing==null || !testing.equals("t"))
			return false;
		return true;
	}
	public static void initializeWorkers(){
		for(int i=0;i<UPLOAD_WORKERS;i++){
			workerList.add(new Worker("upload_"+i,"upload"));
		}

		for(int i=0;i<DOWNLOAD_WORKERS;i++){
			workerList.add(new Worker("download_"+i,"download"));
		}

		for(int i=0;i<QUICK_WORKERS;i++){
			workerList.add(new Worker("quick_"+i,"quick"));
		}
	}
	public static void startWorkers(){
		for(Worker w: workerList){
			if(!w.isAlive())
				w.start();
		}
	}
	public static void stopWorkers(){
		for(Worker w: workerList){
			w.noMore();
		}
	}
	public static void setPrimaryStage(Stage stage){
		primaryStage = stage;
	}
	public static Stage getPrimaryStage(){
		return primaryStage;
	}
	public static void setInterfaceController(InterfaceController c){
		interfaceController=c;
	}
	public static InterfaceController getInterfaceController(){
		return interfaceController;
	}
}
