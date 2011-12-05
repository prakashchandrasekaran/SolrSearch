package code.appscale.test;

import java.io.File;
import java.io.IOException;

public class FileExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File f = new File("FileExample.java");
		System.out.println(f);
		System.out.println("getAbsPath -> " + f.getAbsolutePath());
		try {
			System.out.println("getCanonicalPath -> " + f.getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("getName -> " + f.getName());
		System.out.println("getPath -> " + f.getPath());
	}

}
