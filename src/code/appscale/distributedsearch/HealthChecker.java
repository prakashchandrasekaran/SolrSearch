package code.appscale.distributedsearch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import code.appscale.utils.XMLParser;
import java.util.ArrayList;
// import java.util.HashMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class HealthChecker { 
	final Logger logger = LoggerFactory.getLogger(SolrSearch.class);
	public XMLParser xmlParser;
	
	public HealthChecker() {
		xmlParser = new XMLParser();
		xmlParser.init();
	}
	
	public void updateXMLParser() {
		xmlParser.init();
	}
	
	public ArrayList<String> getHealthyMasters() {
		ArrayList<String> masters = xmlParser.getMasterist();
		ArrayList<String> healthyMasters = new ArrayList<String>();
		String url;
		String host;
		Integer port;
		if(masters != null) {
			Iterator<String> itr = masters.iterator();
			while(itr.hasNext()) {
				url = itr.next();
				host = url.substring(0, url.indexOf(":"));
				port = Integer.parseInt(url.substring(url.lastIndexOf(":") + 1));
				if(checkHealth(host, port))
					healthyMasters.add(url);
			}
		}
		// System.out.println(healthyMasters);
		return healthyMasters;
	}
	
	public String getHealthyMaster() {
		Random rnd = new Random();
		ArrayList<String> healthyMasters = getHealthyMasters();
		if (healthyMasters.size() == 0) {
			return null;
		}
		else {
			int randomIndex = rnd.nextInt(healthyMasters.size());
			return healthyMasters.get(randomIndex) + "/solr"; 
		}
	}
	
	
	
	/**
	 * list of healthy nodes
	 * @return
	 */
	public String getShards() {
		HashMap<String,ArrayList<String>> serverMap = xmlParser.getServermap();
		Set<String> keys=serverMap.keySet();
		Iterator<String> masters=keys.iterator();
		String url;
		String host;
		Integer port;
		String shardsQuery = new String();
		while(masters.hasNext())
		{
			String key=masters.next();
			ArrayList<String> values=serverMap.get(key);
			if(values != null) {
				Iterator<String> slaves = values.iterator();
				ArrayList<String> healthySlavelist = new ArrayList<String>();
				while(slaves.hasNext())
				{
					url = slaves.next();
					host = url.substring(0, url.indexOf(":"));
					port = Integer.parseInt(url.substring(url.lastIndexOf(":") + 1));
					if(checkHealth(host, port))
					{
						healthySlavelist.add(url);
					}
				}
				
				if(healthySlavelist.size() == 0 )
				{
					host = key.substring(0, key.indexOf(":"));
					port = Integer.parseInt(key.substring(key.lastIndexOf(":") + 1));
					if(checkHealth(host, port))
					{
						shardsQuery += key + "/solr,";
					}
				}
				else
				{
					Random rnd = new Random();
					int randomIndex = rnd.nextInt(healthySlavelist.size());
					shardsQuery += healthySlavelist.get(randomIndex)+"/solr,";
				}
			}
			else
			{  
				host = key.substring(0, key.indexOf(":"));
				port = Integer.parseInt(key.substring(key.lastIndexOf(":") + 1));
				if(checkHealth(host, port))
				{
					shardsQuery += key + "/solr,";
				}
			}
		}
		if(shardsQuery.length() > 0)
			return shardsQuery.substring(0, shardsQuery.length()-1);
		else
			return null;
	}
	
	/**
	 * 
	 * @param hostname
	 * @param port
	 * @return
	 */
	public static boolean checkHealth(String hostname, Integer port) {
        Socket echoSocket = null; 
		try {
			echoSocket = new Socket(hostname, port);
		} catch (UnknownHostException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		if (echoSocket == null) {
			return false;
		}
		else {
			return echoSocket.isConnected();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HealthChecker hc = new HealthChecker();
		// System.out.println(HealthChecker.checkHealth("www.google.com", 80));
		System.out.println(hc.getHealthyMasters());
		for(int i = 0;i<10;i++) {
			System.out.println(hc.getHealthyMasters());
			System.out.println(hc.getHealthyMaster());
		}
		System.out.println( hc.xmlParser.getServermap());
		System.out.println( hc.getShards() );
	}
}
