package code.appscale.test;

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


public class CopyOfSolrSearch {

    private SolrServer solrServer;
    final Logger logger = LoggerFactory.getLogger(CopyOfSolrSearch.class);
    
    CopyOfSolrSearch() {
    	
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
	public void index(String f) {
		File file = new File(f);
		if(!file.exists()) {
			logger.error("Specified File not found - " + file);
		}
		 
		String fileName = file.getName();
		String filePath = file.getAbsolutePath();
		String fileType = getFileType(file.getName());
		String updateType = getUpdateType(fileType);
		
		ContentStreamUpdateRequest updateRequest = new ContentStreamUpdateRequest(updateType);
		try {
			updateRequest.addFile(file);
		} catch (IOException e) {
			logger.error("Error in File Update Request - " + updateRequest);
			e.printStackTrace();
		}
			
		updateRequest.setParam("literal.id", fileName);
		updateRequest.setParam("literal.content_id", fileName);
		//updateRequest.setParam("fileName", fileName);
		//updateRequest.setParam("filePath", filePath);
		updateRequest.setParam("stream.file",  filePath);
		//updateRequest.setParam("user", "AppScaleUser");
		//updateRequest.setParam("fileHost", "localhost");
		updateRequest.setParam("uprefix", "attr_");
		updateRequest.setParam("fmap.content", "attr_content"); 
		updateRequest.setParam("attr_user", "AppScaleUser");
		// we can do this also . 
		// ModifiableSolrParams p = new ModifiableSolrParams();
		// updateRequest.setParam(p);
		/*
		 *  ModifiableSolrParams params = null ; 
		    params = new ModifiableSolrParams(); 
		    params.add("stream.file", fileName); 
		    params.add("stream.url", fileName); 
		    params.set("literal.content_id", solrId); 
		    up.setParams(params); 
		 */
		
		updateRequest.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
		updateRequest.setAction(AbstractUpdateRequest.ACTION.OPTIMIZE, true, true);
	    
	    try{
	    	solrServer.request(updateRequest);
	    } catch(Exception e) {
	    	logger.error("Error while updating file to Solr ");
	    	e.printStackTrace();
	    }
	    System.out.println("File " + fileName + " Uploaded");
	    QueryResponse rsp;
		try {
			rsp = solrServer.query(new SolrQuery("attr_content:*"));
			   System.out.println(rsp.getHeader());
			    System.out.println(rsp);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
	}
	
	/**
	 * returns the Suitable update URL for given fileType
	 * @param fileType
	 * @return
	 */
	private String getUpdateType(String fileType) {
		if(fileType.equalsIgnoreCase("json"))
			return "update/json";
		else if(fileType.equalsIgnoreCase("csv"))
			return "update/csv";
		else
			return "update/extract";
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
	public void search(String query, Boolean withContents) {
		QueryResponse rsp = new QueryResponse();
		try {
			rsp = solrServer.query(new SolrQuery("*:*"));
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    // System.out.println(rsp.getHeader());
	    System.out.println("Query Elapsed Time : " + rsp.getElapsedTime());
	    System.out.println("Number of Results : " + rsp.getResults().getNumFound());
	    // System.out.println(rsp.getResults().get(0).getFieldValuesMap());
	    for(int i=0;i<rsp.getResults().size();i++) {
	    	System.out.println((i+1) + "." + rsp.getResults().get(i).getFieldValue("id"));
	    }
	    if(withContents) {
	    	System.out.println(rsp);
	    }
	}
	
	public void delteAll() {
		
	}
	
	public static void main(String[] args) {
		CopyOfSolrSearch solrSearch = new CopyOfSolrSearch();
		solrSearch.connect("http://localhost:8983/solr");
		solrSearch.index("/home/chandra/courses.txt");
		solrSearch.search("*", false);
	}
}
