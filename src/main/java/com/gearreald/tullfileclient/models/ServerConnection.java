package com.gearreald.tullfileclient.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;

import com.gearreald.tullfileclient.Environment;

import net.tullco.tullutils.NetworkUtils;

public class ServerConnection {
	
	private static final String LIST_URL = "list/";
	private static final String UPLOAD_URL = "upload/";
	private static final String AUTH_URL = "";
	
	private static final int CHUNK_SIZE = 2097152;
	
	public static boolean checkKey() throws MalformedURLException, IOException{
		NetworkUtils.getDataFromURL(
				getURLFor("AUTH")
				,false
				,NetworkUtils.HEAD
				,Pair.of("Authorization", Environment.getConfiguration("API_KEY")));
		return true;
	}
	public static JSONObject getFileListing(TullFolder f) throws IOException{
		JSONObject request = new JSONObject();
		request.put("directory", f.getLocalPath());
		String response = NetworkUtils.sendDataToURL(
				getURLFor("LIST")
				,false
				,NetworkUtils.POST
				,request.toString()
				,Pair.of("Authorization", Environment.getConfiguration("API_KEY")));
		return new JSONObject(response);
	}
	public static File downloadFile(TullFile f){
		//TODO Need to download files at some point. (wasntme)
		return null;
	}
	public static void uploadFile(File file, String filePath) throws IOException{
		byte[] byteBuffer = new byte[CHUNK_SIZE];
		FileInputStream f = new FileInputStream(file);
		for(int i=1; f.read(byteBuffer)!=-1; i++){
			int bytesInBuffer = f.read(byteBuffer);
			sendByteStream(byteBuffer, bytesInBuffer, i, filePath);
		}
		f.close();
	}
	public static JSONObject sendByteStream(
			byte[] byteBuffer
			,int bytesInBuffer
			,int pieceNumber
			,String filePath) throws MalformedURLException, IOException {
		String response = NetworkUtils.sendBinaryDataToURL(
				getURLFor("UPLOAD")
				,false
				,NetworkUtils.POST
				,byteBuffer
				,bytesInBuffer
				,Pair.of("pieceNumber", Integer.toString(pieceNumber))
				,Pair.of("localPath", filePath));
		return new JSONObject(response);
	}
	private static String getURLFor(String location){
		String baseURL = Environment.getConfiguration(("HOSTNAME"));
		if(!baseURL.endsWith("/"))
			baseURL+="/";
		switch(location){
			case "LIST":
				return baseURL + LIST_URL;
			case "AUTH":
				return baseURL + AUTH_URL;
			case "UPLOAD":
				return baseURL + UPLOAD_URL;
			default:
				return baseURL;
		}
	}
}
