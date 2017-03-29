package simpledb.test;

import simpledb.tx.Transaction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import simpledb.query.Plan;
import simpledb.query.Scan;
import simpledb.server.SimpleDB;
import simpledb.stats.IntermediateFileStats;

public class Domanda4 {
	public static void main(String[] args) {
		try {
			//genero le statistiche che mostrano le implementazioni delle strategie			
			getStatsFor(8,"naive",true);
//			getStatsFor(8,"clock",true);
//			getStatsFor(8,"fifo",true);
//			getStatsFor(8,"lru",true);
//			//cerco di capire il numero di buffer quanto mi risparmia la vita
//			getStatsFor(20,"naive",false);
//			getStatsFor(20,"lru",false);
//			getStatsFor(100,"naive",false);
//			getStatsFor(100,"lru",false);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void getStatsFor(int buffsize, String strategy, boolean chatty){
		SimpleDB.init("studentdb",buffsize,strategy,chatty,true);
		Transaction tx = new Transaction();
		String result = executeQuery(tx);
		tx.commit();
		System.out.println(result);
	}
	
	private static String executeQuery(Transaction tx){
		
		System.out.println("LOG FOR TRANSACTION: "+ tx.toString());
		
		SimpleDB.fileMgr().resetMapStats();
		String qry = "select SName from STUDENT";	
		Plan p = SimpleDB.planner().createQueryPlan(qry, tx);
		Scan s = p.open();
		StringBuilder result = new StringBuilder("--------- Student's names---------");
		int i = 1;
		while (s.next()) {
			String sname = s.getString("sname"); //SimpleDB stores field names
			result.append(i+") "+ sname + "\n");
			i++;
		}
		result.append("---------End Student's names-----------");
		s.close();
		
		printAllBlockStats(tx.toString());
		SimpleDB.fileMgr().resetMapStats();
		return result.toString();
	}
	
	/* Edited for print stats on transaction! */
	private static void printAllBlockStats(String additionalInfo){
		Map<String,IntermediateFileStats> allStats = SimpleDB.fileMgr().getMapStats();
		System.out.println("----------------STATS----------------\n");
		System.out.println("BUFFER SIZE : "+ SimpleDB.BUFFER_SIZE);
		if(!(additionalInfo==null)){
			System.out.println("for transaction: "+additionalInfo);
		}
		for (Map.Entry<String, IntermediateFileStats> entry : allStats.entrySet()) {
		    printBlockStats(entry.getKey(),entry.getValue());
		}
        System.out.println("-------------------------------------");
		System.out.println("-------------------------------------");
	}

	private static void printBlockStats(String filename, IntermediateFileStats stats){
		System.out.println("______________________________________");
		System.out.println("Stats for : "+filename+" \n"+stats.toString());
		System.out.println("______________________________________");
	}

}
