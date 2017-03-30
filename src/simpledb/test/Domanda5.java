package simpledb.test;

import simpledb.tx.Transaction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import simpledb.file.FileMgr;
import simpledb.record.RID;
import simpledb.record.RecordFile;
import simpledb.record.Schema;
import simpledb.record.TableInfo;
import simpledb.server.SimpleDB;
import simpledb.stats.BasicRecordStats;
import simpledb.stats.IntermediateFileStats;

/**
 * Utilizzando SimpleDB (o meglio, i suoi moduli di livello pi`u basso), scrivere una classe di test che esegue alcune operazioni su un file.
 * 
 * 1. l’inserimento di una sequenza di 10000 record (con valori generati casualmente)
 * 2. la lettura di tutti i record
 * 3. l’eliminazione di una parte dei record (orientativamente il 50% dei record; 
 * 			ad esempio, quelli per cui il valore di un campo soddisfa una certa condizione)
 * 4. la scansione di tutti i record (con la lettura di un campo numerico e un qualche calcolo, ad esempio il valore medio)
 * 5. l’inserimento di altri 7000 record (sempre con valori generati casualmente)
 * 6. nuovamente la lettura di tutti i record
 * 
 * Per ciascuna delle operazioni, stampare il numero di record letti e scritti e il numero di accessi a memoria secondaria
 * per il file in questione 
 * (anche in questo caso possono servire metodi che stampano le statistiche, che vanno per`o richiamati nella classe di test).
 * 
 * FLOW:
 *  Init new database
 *  Schema definition
 *  Table info
 *  Insert
 *  Read all records
 *  Delete half
 *  Read and calculate
 *  Insert again
 *  Read all records
 *  **/

public class Domanda5 {
	public static int FIRST_INS       = 10000;
	public static int SECOND_INS = 7000;
	public static int HALF_DEL      = FIRST_INS/2;

	public static void main(String[] args) {
		initTest("testDB","prova",500,"naive");
		initTest("testDB","prova",500,"clock");
		initTest("testDB","prova",500,"lru");
		
		initTest("testDB","prova",1000,"clock");
		initTest("testDB","prova",1000,"lru");
	}


	private static void initTest(String dbName, String tblName, int buff_num, String strg){
		try {
			String homedir = System.getProperty("user.home");
			String dbname = dbName;//"testDB";
			File dbDirectory = new File(homedir, dbname);

			if(dbDirectory.exists()){
				try{
					cleanDBFolder(dbDirectory);
				}
				catch(IOException e){
					System.out.println("File non cancellato " + e.toString());
				}	
			}
			SimpleDB.init(dbname,buff_num,strg,false,true);

			Transaction tx;

			System.out.println("------ Building Table "+tblName+" ------");

			Schema sch = new Schema();
			sch.addIntField("id");
			sch.addStringField("code", 10);
			sch.addStringField("text", 10);
			System.out.println("Schema:"+ sch.toString()+"\n");

			TableInfo ti = new TableInfo(tblName,sch);

			System.out.println("TableInfo:"+ ti.toString()+"\n");

			System.out.println("------ Table "+tblName+" Builded ------");        


			System.out.println("------ Populating Table "+tblName+" with "+FIRST_INS+" records ------");

			tx = new Transaction();
			RecordFile rf = new RecordFile(ti,tx);
			int i= 0;
			for(i=0; i<FIRST_INS;i++){
				int      id = getRandomInt(10);
				int    code = getRandomInt(10);
				String text = getRandomString(10);
				//System.out.println("\n--------------------------------------");
				//System.out.println("Inserisco <"+id+", "+code+", "+text+">");
				rf.insert();
				//System.out.println("id");
				rf.setInt("id", id);
				//System.out.println("code");
				rf.setInt("code", code); 
				//System.out.println("text");
				rf.setString("text", text);
				//System.out.println("Inserito <"+id+", "+code+", "+text+">");
				//System.out.println("--------------------------------------\n");
			} 
			rf.close();
			tx.commit();
			System.out.println("------ "+i+" record added to "+ tblName+" ------");
			
			printRecordStats(rf, null);
			printFileStats(SimpleDB.fileMgr(),tx.toString());
			
			rf.resetStats();
			SimpleDB.fileMgr().resetMapStats();
			
			System.out.println("\n-------- Read all "+i+" records from"+tblName+" ----------");
			tx = new Transaction();
			ti = new TableInfo(tblName, sch);
			rf = new RecordFile(ti, tx);
			i=0;
			while (rf.next()) {
				//System.out.println("\n--------------------------------------");
				//System.out.println("Reading <"+rf.getInt("id")+", "+rf.getInt("code")+", "+rf.getString("text")+">");
				//System.out.println("id " + rf.getInt("id"));
				//System.out.println("code " + rf.getInt("code"));
				//System.out.println("text " +rf.getString("text"));
				//System.out.println("--------------------------------------\n");
				i++;
			}
			rf.close();
			tx.commit();
			System.out.println("\n-------- "+i+" record read from "+tblName+" ----------");

			printRecordStats(rf, null);
			printFileStats(SimpleDB.fileMgr(),tx.toString());
			
			rf.resetStats();
			SimpleDB.fileMgr().resetMapStats();
			
			System.out.println("\n-------- Delete "+HALF_DEL+" records from "+tblName+" ----------");

			tx = new Transaction();
			ti = new TableInfo(tblName, sch);
			rf = new RecordFile(ti, tx);
			i =0;
			while (rf.next()&&i<=HALF_DEL) {
				if(rf.getInt("id")%2==0){//se ha un id pari
					rf.delete();
					i++;
				}
				//System.out.println("Deleting: <"+rf.getInt("id")+", "+rf.getInt("code")+", "+rf.getString("text")+">");
			}
			rf.close();
			tx.commit();
			System.out.println("\n-------- Deleted "+i+" records from "+tblName+" ----------");

			printRecordStats(rf, null);
			printFileStats(SimpleDB.fileMgr(),tx.toString());
			
			rf.resetStats();
			SimpleDB.fileMgr().resetMapStats();
			
			System.out.println("\n-------- Read all "+(FIRST_INS-i)+" records from"+tblName+" ----------");
			tx = new Transaction();
			ti = new TableInfo(tblName,sch);
			rf = new RecordFile(ti,tx);
			i=0;
			while (rf.next()) {
				i++;
				//System.out.println("\n--------------------------------------");
				//System.out.println("Reading <"+rf.getInt("id")+", "+rf.getInt("code")+", "+rf.getString("text")+">");
				//System.out.println("id " + rf.getInt("id"));
				//System.out.println("code " + rf.getInt("code"));
				//System.out.println("text " +rf.getString("text"));
				//System.out.println("--------------------------------------\n");
			}
			rf.close();
			tx.commit();
			System.out.println("\n-------- "+i+" record read from "+tblName+" ----------");

			printRecordStats(rf, null);
			printFileStats(SimpleDB.fileMgr(),tx.toString());
			
			rf.resetStats();
			SimpleDB.fileMgr().resetMapStats();
			
			System.out.println("\n-------- Add additional "+SECOND_INS+" records to "+tblName+"----------");

			tx = new Transaction();
			ti = new TableInfo(tblName,sch);
			rf = new RecordFile(ti,tx);
			i=0;
			for(i=0; i<SECOND_INS;i++){
				int      id = getRandomInt(10);
				int    code = getRandomInt(10);
				String text = getRandomString(10);
				//System.out.println("\n--------------------------------------");
				//System.out.println("Inserisco <"+id+", "+code+", "+text+">");
				rf.insert();
				rf.setInt("id", id); //System.out.println("id");
				rf.setInt("code", code); //System.out.println("code");
				rf.setString("text", text); //System.out.println("text");
				//System.out.println("Inserito <"+id+", "+code+", "+text+">");
				//System.out.println("--------------------------------------\n");
			} 
			rf.close();
			tx.commit();
			System.out.println("\n-------- "+i+" records added to "+tblName+"----------");

			printRecordStats(rf, null);
			printFileStats(SimpleDB.fileMgr(),tx.toString());
			
			rf.resetStats();
			SimpleDB.fileMgr().resetMapStats();
			
			System.out.println("\n-------- Read all records from "+tblName+"----------");
			tx = new Transaction();
			ti = new TableInfo(tblName,sch);
			rf = new RecordFile(ti,tx);
			i=0;
			while (rf.next()) {
				i++;
				//System.out.println("\n--------------------------------------");
				//System.out.println("Leggo <"+rf.getInt("id")+", "+rf.getInt("code")+", "+rf.getString("text")+">");
				//System.out.println("id " + rf.getInt("id"));
				//System.out.println("code " + rf.getInt("code"));
				//System.out.println("text " +rf.getString("text"));
				//System.out.println("--------------------------------------\n");
			}
			rf.close();
			tx.commit();
			System.out.println("\n-------- "+i+" records were read from "+tblName+"----------");

			printRecordStats(rf, null);
			printFileStats(SimpleDB.fileMgr(),tx.toString());
			
			rf.resetStats();
			SimpleDB.fileMgr().resetMapStats();
			
		}
		catch(Exception e ){
			e.printStackTrace();
		}
	}
	
	/* Ho qualsiasi informazione che voglio sulle statistiche dei files */
	public static void printFileStats(FileMgr fileMgr, String info){
		//System.out.println(SimpleDB.fileMgr().printStats(info));
		IntermediateFileStats stats= fileMgr.getMapStats().get("prova.tbl");
		System.out.println("prova.tbl < blkRead: "+ stats.getBlockRead() +", blkWrite: "+ stats.getBlockWritten()+" >");
	}

	/*Ho qualsiasi informazione che voglio su un record */
	public static void printRecordStats(RecordFile rf,String info){
		//System.out.println(rf.recordFileStatsToString());
		int totWrittenRec = 0;
		int totReadRec    = 0;
		int totWrittenFld = 0;
		int totReadFld    = 0;
		for (Map.Entry<RID, BasicRecordStats> entry : rf.getStatsRecord().entrySet()) {
			totWrittenRec += entry.getValue().getWrittenRecord();
			totReadRec += entry.getValue().getReadRecord();
			totWrittenFld += entry.getValue().getWrittenFieldsRecord();
			totReadFld += entry.getValue().getReadFieldsRecord();
		}
		StringBuilder res = new StringBuilder("Totali: ");
		res.append(" < writtenRecords: "+totWrittenRec);
		res.append(", readRecords: "+totReadRec);
		res.append(", writtenFields : "+totWrittenFld);
		res.append(", readFields: "+totReadFld +" >");
		System.out.println(res.toString());
	}
	
	public static void cleanDBFolder(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				cleanDBFolder(c);
		}
		if (!f.delete())
			throw new FileNotFoundException("Failed to delete file: " + f);
	}

	private static int getRandomInt(int len){
		int mul =(int)Math.pow(10, len-1);
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
