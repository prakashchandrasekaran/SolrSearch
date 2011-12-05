package code.appscale.utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParser  {
	HashMap<String,ArrayList<String>> serverMap;
	ArrayList<String> masterList;
	ArrayList<String> slaveList;
	Document dom;

	public XMLParser(){
		masterList = new ArrayList<String>();
		slaveList =new ArrayList<String>();
		serverMap = new HashMap<String,ArrayList<String>>();
	}

	public boolean init() {
		try {
			run("./config/host-config.xml");
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void run(String file) {
		parseXmlFile(file);
		parseDocument();
	}
	private void parseXmlFile(String file){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(file);
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private void parseDocument(){
		Element docEle = dom.getDocumentElement();
		NodeList nl = docEle.getElementsByTagName("Master");
		String hostPort = new String();
			if(nl != null && nl.getLength() > 0) {
				for(int i = 0 ; i < nl.getLength();i++) {
					Element el = (Element)nl.item(i);
					hostPort = getValue(el);
					masterList.add(hostPort);
					serverMap.put(hostPort, null);
				}
			}
	    NodeList n2 = docEle.getElementsByTagName("Slave");
			if(n2 != null && n2.getLength() > 0) {
				for(int i = 0 ; i < n2.getLength();i++) {
					Element el = (Element)n2.item(i);
					String slaveName=getValue(el);
					slaveList.add(slaveName);
					addtoMap(getSlavemaster(el),slaveName);
				}
			}
	}

    private String getValue(Element serverElem) {
		String hostname = getTextValue(serverElem,"hostname");
		String port = getTextValue(serverElem,"port");
		String name=hostname + ":" + port;
		return name;
	}
    private String getSlavemaster(Element serverElem) {
    	String mastername = getTextValue(serverElem,"mastername");
        String masterport = getTextValue(serverElem,"masterport");
        String slaveMaster=mastername+":"+masterport;
        return slaveMaster;
    }
    void addtoMap(String key,String value)
    {
    	if(serverMap.get(key) != null)
    	    serverMap.get(key).add(value);
    	else
    	{
    		ArrayList<String> values=new ArrayList<String>();
    		values.add(value);
    		serverMap.put(key, values);
    	}
    }

	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

	private int getIntValue(Element ele, String tagName) {
		return Integer.parseInt(getTextValue(ele,tagName));
	}
	
	private void printData(){
		
		System.out.println("\n\nNo of Masters '" + masterList.size() + "'.");
	    Iterator<String> it = masterList.iterator();
		while(it.hasNext()) {
			System.out.println(it.next().toString());
		}
		System.out.println("\n\nNo of Slaves '" + slaveList.size() + "'.");
	    Iterator<String> it1 = slaveList.iterator();
		while(it1.hasNext()) {
			System.out.println(it1.next().toString());
		}
		System.out.println("\n\nMap Relations");
		Set<String> keys=serverMap.keySet();
		Iterator<String> it2=keys.iterator();
		while(it2.hasNext())
		{
			String key=it2.next();
			ArrayList<String> values=serverMap.get(key);
			
			System.out.print("\n"+key+"    ----->   ");
			if(values != null) {
				Iterator<String> it4=values.iterator();
				while(it4.hasNext())
				{
					System.out.print(it4.next()+"  ");
				}
			}
			else {
				System.out.println("NoSlaves");
			}
		}
	}
    public ArrayList<String> getMasterist()
    {
    	return masterList;
    }
    public ArrayList<String> getSlaveList()
    {
    	return slaveList;
    }
    public HashMap<String,ArrayList<String>> getServermap()
    {
    	return serverMap;
    }
	
	public static void main(String[] args){
		XMLParser dpe = new XMLParser();
		dpe.init();
		dpe.printData();
		// dpe.getMasterist();
	}

}