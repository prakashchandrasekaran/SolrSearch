package code.appscale.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import code.appscale.distributedsearch.SolrSearch;


public class HTTPGetFile {
    final Logger logger = LoggerFactory.getLogger(HTTPGetFile.class);
    
    public HTTPGetFile() {
    	
    }
    
	public String download(String fileURL) {
		logger.info("file URL " + fileURL);
		long start = System.currentTimeMillis();
		int data = 0;
		URL url = null;
		InputStream urlInputStream = null;
		BufferedInputStream urlBufferedInputStream = null;
		File localFile = null;
		FileOutputStream fileOutputStream = null;
		String tempDir = System.getProperty("java.io.tmpdir");
		logger.info("temp directory " + tempDir);
		try {
			url = new URL(fileURL);
		    urlInputStream = url.openStream();
		    urlBufferedInputStream  = new BufferedInputStream(urlInputStream);
		    localFile = new File(
		    		  tempDir 
		    		+ File.separator
		    		+ new Date().getTime()
		    		+ "_"
		    		+ fileURL.substring(fileURL.lastIndexOf('/') + 1));
		    logger.info("local file " + localFile);
		    // localFile.deleteOnExit();
		    fileOutputStream = new FileOutputStream(localFile);
		    for (;true;)
		    {
		        data = urlBufferedInputStream.read();
		        if (data == -1)
		            break;
		        else {
		        	fileOutputStream.write(data);
		        }
		    }
		}catch(MalformedURLException m) {
			m.printStackTrace();
			return null;
		}catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
		// Close all the Streams and Files !!
		return localFile.getAbsolutePath();
	}
	public static void main(String[] args) throws Exception {
		HTTPGetFile hgf = new HTTPGetFile();
		hgf.download("http://localhost:8888/queryForm.jsp");
	}
	

}
