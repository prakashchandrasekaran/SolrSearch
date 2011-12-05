package code.appscale.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;

import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SolrSrch {

    private static SolrServer solrServer;
    final Logger logger = LoggerFactory.getLogger(SolrSrch.class);
    
    SolrSrch() {
    	
    }
    
    public void connect(String url) {
    	try {
    		logger.info("Creating a connection with URL " + url);
			solrServer = new CommonsHttpSolrServer(url);
		} catch (MalformedURLException e) {
			logger.error("Malformed URL Exception"); 
			e.printStackTrace();
		}
    }
   	
    /**
     * 
     * @param f
     */
	public void Index(String f) {
		File file = new File(f);
		if(!file.exists()) {
			logger.error("Specified File not found - " + file);
		}
		 
		String fileName = file.getName();
		String filePath = file.getAbsolutePath();
		String fileType = getFileType(file.getName());
		String updateType = getUpdateType(fileType);
		logger.info("update Type --> " + updateType);
		ContentStreamUpdateRequest updateRequest = new ContentStreamUpdateRequest(updateType);
		try {
			updateRequest.addFile(file);
		} catch (IOException e) {
			logger.error("Error in File Update Request - " + updateRequest);
			e.printStackTrace();
		}
		
		updateRequest.setParam("literal.id", fileName);
		updateRequest.setParam("literal.content_id", fileName);
		updateRequest.setParam("stream.file",  filePath);
		updateRequest.setParam("uprefix", "attr_");
		updateRequest.setParam("fmap.content", "attr_content"); 
		
		updateRequest.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
		updateRequest.setAction(AbstractUpdateRequest.ACTION.OPTIMIZE, true, true);
	    
	    try{
	    	solrServer.request(updateRequest);
	    } catch(Exception e) {
	    	logger.error("Error while updating file to Solr ");
	    	e.printStackTrace();
	    }
	    System.out.println("File " + fileName + " Uploaded");
	}
	
	/**
	 * returns the Suitable update URL for given fileType
	 * @param fileType
	 * @return
	 */
	private String getUpdateType(String fileType) {
		if(fileType.equalsIgnoreCase("json"))
			return "/update/json";
		else if(fileType.equalsIgnoreCase("csv"))
			return "/update/csv";
		else
			return "/update/extract";
	}

	/**
	 * Inputs a fileName and returns the fileType(extension) ..
	 * @param fileName - inputs the fileName in String
	 * @return fileType - in lowercase String
	 */
	private String getFileType(String fileName) {
		return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
	}
	
	/**
	 * Searches for given query in SolrServer 
	 * @param query - String
	 */
	public void Search(String query, Boolean withContents) {
		QueryResponse rsp = new QueryResponse();
		SolrQuery solrQuery, solrQuery1;
		// = new SolrQuery(query);
		solrQuery = new SolrQuery("attr_content:" + query);
		solrQuery1 = new SolrQuery(query);
		solrQuery.set("distrib", true);
		solrQuery1.set("distrib", true);
		try {
			//////////
			rsp = solrServer.query(solrQuery);
			for(int i=0;i<rsp.getResults().size();i++) {
		    	System.out.println((i+1) + "." + rsp.getResults().get(i).getFieldValue("id"));
		    }
		    if(withContents) {
		    	System.out.println(rsp);
		    }
		    //////////
		    rsp = solrServer.query(solrQuery1);
			for(int i=0;i<rsp.getResults().size();i++) {
		    	System.out.println((i+1) + "." + rsp.getResults().get(i).getFieldValue("id"));
		    }
		    if(withContents) {
		    	System.out.println(rsp);
		    }
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	    // System.out.println(rsp.getHeader());
	    // System.out.println("Query Elapsed Time : " + rsp.getElapsedTime());
	    // System.out.println("Number of Results : " + rsp.getResults().getNumFound());
	    // System.out.println(rsp.getResults().get(0).getFieldValuesMap());
	    
	}
	
	public static void main(String[] args) {
		SolrSrch solrSearch = new SolrSrch();
		solrSearch.connect("http://localhost:8983/solr");
		// solrSearch.Index("/home/chandra/set_java.csh");
		solrSearch.Search("monitor", false);
		
		/*
		 *server.setSoTimeout(1000);  // socket read timeout
  server.setConnectionTimeout(100);
  server.setDefaultMaxConnectionsPerHost(100);
  server.setMaxTotalConnections(100);
  server.setFollowRedirects(false);  // defaults to false
  // allowCompression defaults to false.
  // Server side must support gzip or deflate for this to have any effect.
  server.setAllowCompression(true);
  server.setMaxRetries(1); // defaults to 0.  > 1 not recommended.
  server.setParser(new XMLResponseParser());  
		 */
	}
}
