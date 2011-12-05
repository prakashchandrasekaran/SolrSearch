package code.appscale.utils;


import java.io.File;
import java.io.IOException;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;

import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;

/**
 * @author 
 */

public class Solr {

  public static void main(String[] args) {
    try {
      
      String fileName = "/home/chandra/MSWord.doc"; 
      //this will be unique Id used by Solr to index the file contents.
      String solrId = "MSWord.doc"; 
      
      indexFilesSolrCell(fileName, solrId);
      
    } catch (Exception ex) {
      System.out.println(ex.toString());
    }
  }
  
 
  public static void indexFilesSolrCell(String fileName, String solrId) 
    throws IOException, SolrServerException {
    
    String urlString = "http://localhost:8983/solr"; 
    SolrServer solr = new CommonsHttpSolrServer(urlString);
    
    ContentStreamUpdateRequest up = new ContentStreamUpdateRequest("/update/extract");
    
    up.addFile(new File("/home/chandra/index.txt"));
    
    up.setParam("literal.id", "index.txt");
    up.setParam("uprefix", "attr_");
    up.setParam("fmap.content", "attr_content");
    
    up.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);

    System.out.println("i reached here !! ");
    try{
    	solr.request(up);
    }catch(Exception e)
    {
    	e.printStackTrace();
    }
    System.out.println("i reached here 2 !! ");
    QueryResponse rsp = solr.query(new SolrQuery("attr_content:content"));
    System.out.println(rsp.getHeader());
    System.out.println(rsp);
  }
}