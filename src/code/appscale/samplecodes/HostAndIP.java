package code.appscale.samplecodes;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostAndIP {

   public static void main(String args[]) {
	   InetAddress addr;
	try {
		addr = InetAddress.getLocalHost();
		byte[] ipAddr = addr.getAddress();
	    String hostname = addr.getHostName();
	    System.out.println(hostname);
	    System.out.println(addr.getAddress());
	    System.out.println(addr.getLocalHost());
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}
