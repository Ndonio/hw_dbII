package z_example_city.simpledb;

import java.util.Random;

public class prova {
	
	public static void main(String[] args) {
		getData(1000);
	}
	
	private static String[] getData(int numOfRecords){
		String[] res = new String[numOfRecords];
		String sep = ", ";
		String op="(";
		String cl=")";
		for(int i=0;i<numOfRecords;i++){
			res[i]=op+getRandomInt(4)+sep+getRandomInt(4)+sep+getRandomString(10)+cl;
			System.out.println(res[i]);
		}
		return res;
	}
	
	private static int getRandomInt(int len){
		int mul =(int)Math.pow(10, len);
	    return  (int)(Math.random()*mul);
	}
	
	private static String getRandomString(int strlen) {
	    int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    StringBuilder buffer = new StringBuilder(strlen);
	    for (int i = 0; i < strlen; i++) {
	        int randomLimitedInt = leftLimit + (int) 
	          (new Random().nextFloat() * (rightLimit - leftLimit + 1));
	        buffer.append((char) randomLimitedInt);
	    }
	    return buffer.toString();
	}
}
