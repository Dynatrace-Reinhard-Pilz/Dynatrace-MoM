package com.dynatrace.mom.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppData {

	public static void main(String[] args) {
		File appData = getAppData();
		System.out.println(appData.getAbsolutePath());
	}
	
	private static File getAppData(){
	    ProcessBuilder builder = new ProcessBuilder(new String[]{"cmd", "/C echo %APPDATA%"});

	    BufferedReader br = null;
	    try {
	        Process start = builder.start();
	        br = new BufferedReader(new InputStreamReader(start.getInputStream()));
	        String path = br.readLine();
	        // TODO HACK do not know why but I get an extra '"' at the end
	        if(path.endsWith("\"")){
	            path = path.substring(0, path.length()-1);
	        }
	        return new File(path.trim());


	    } catch (IOException ex) {
	        Logger.getLogger(AppData.class.getName()).log(Level.SEVERE, "Cannot get Application Data Folder", ex);
	    } finally {
	        if(br != null){
	            try {
	                br.close();
	            } catch (IOException ex) {
	                Logger.getLogger(AppData.class.getName()).log(Level.SEVERE, null, ex);
	            }
	        }
	    }

	    return null;
	}	

}
