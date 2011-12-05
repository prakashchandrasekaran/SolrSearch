package code.appscale.distributedsearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Props {

	final Logger logger = LoggerFactory.getLogger(Props.class);
	public String propsFileName;
	public ArrayList<String> hostList; 
	public Integer replicationFactor; 
	
	Props() {
		propsFileName = new String();
		propsFileName = "config" + File.separator + "solr.props";
	}
	
	/*
	 * returns the PropertyFile name to the calling function
	 */
	public String getPropertyFile() {
		return propsFileName;
	}
	
	/*
	 * Loads the property file and 
	 * Extracts the property information from the file 
	 */
	public void initAndLoad() { 
		File propsFile = new File(propsFileName);
		if(! propsFile.exists()) {
			logger.error("Property File not Found - " + propsFileName);
			System.exit(0);
		}
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(propsFile);
		} catch (FileNotFoundException e) {
			logger.error("Property File failed to open - " + propsFileName);
			e.printStackTrace();
		}
		
		Properties property = new Properties();
		try {
			property.load(fis);
		} catch (IOException e) {
			logger.error("Failed to load Property File - " + propsFileName);
			e.printStackTrace();
		}
		
		hostList = new ArrayList<String>();
		for(String host : property.getProperty("hostnames").split(";")) {
			hostList.add(host);
		}
		logger.info("Host List : " + hostList);
		replicationFactor = Integer.parseInt(property.getProperty("replicationfactor"));
		logger.info("Replication Factor : " + replicationFactor);
	}
	
	public static void main(String[] args) {
		Props props = new Props();
		props.initAndLoad();
	}
}
