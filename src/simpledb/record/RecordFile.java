package simpledb.record;

import java.util.*;

import simpledb.file.Block;
import simpledb.stats.BasicRecordStats;
import simpledb.tx.Transaction;

/**
 * Manages a file of records.
 * There are methods for iterating through the records
 * and accessing their contents.
 * @author Edward Sciore
 */
public class RecordFile {
	private TableInfo ti;
	private Transaction tx;
	private String filename;
	private RecordPage rp;
	private int currentblknum;


	private Map<RID,BasicRecordStats> statsRecord;
	private RID lastReadRecord;
	private RID lastWrittenRecord;

	/**
	 * Constructs an object to manage a file of records.
	 * If the file does not exist, it is created.
	 * @param ti the table metadata
	 * @param tx the transaction
	 */
	public RecordFile(TableInfo ti, Transaction tx) {
		this.ti = ti;
		this.tx = tx;
		this.statsRecord = new HashMap<RID,BasicRecordStats>(); //
		filename = ti.fileName();
		this.lastReadRecord = new RID();
		this.lastWrittenRecord = new RID();
		if (tx.size(filename) == 0)
			appendBlock();
		moveTo(0);
	}

	/**
	 * Closes the record file.
	 */
	public void close() {
		rp.close();
	}

	/**
	 * Positions the current record so that a call to method next
	 * will wind up at the first record. 
	 */
	public void beforeFirst() {
		moveTo(0);
	}

	/**
	 * Moves to the next record. Returns false if there
	 * is no next record.
	 * @return false if there is no next record.
	 */
	public boolean next() {
		while (true) {
			if (rp.next()){
				this.lastReadRecord = this.currentRid();
				this.updateRead();
				return true; 
			}
			if (atLastBlock()){
				return false; 
			}
			moveTo(currentblknum + 1);
		}	
	}

	/**
	 * Returns the value of the specified field
	 * in the current record.
	 * @param fldname the name of the field
	 * @return the integer value at that field
	 */
	public int getInt(String fldname) {
		this.updateReadFields(); 
		return rp.getInt(fldname);
	}

	/**
	 * Returns the value of the specified field
	 * in the current record.
	 * @param fldname the name of the field
	 * @return the string value at that field
	 */
	public String getString(String fldname) {
		this.updateReadFields();
		return rp.getString(fldname);
	}

	/**
	 * Sets the value of the specified field 
	 * in the current record.
	 * @param fldname the name of the field
	 * @param val the new value for the field
	 */
	public void setInt(String fldname, int val) {
		rp.setInt(fldname, val);
		this.updateWrittenFields();
	}

	/**
	 * Sets the value of the specified field 
	 * in the current record.
	 * @param fldname the name of the field
	 * @param val the new value for the field
	 */
	public void setString(String fldname, String val) {
		rp.setString(fldname, val);
		this.updateWrittenFields();
	}

	/**
	 * Deletes the current record.
	 * The client must call next() to move to
	 * the next record.
	 * Calls to methods on a deleted record 
	 * have unspecified behavior.
	 */
	public void delete() {
		rp.delete();
	}

	/**
	 * Inserts a new, blank record somewhere in the file
	 * beginning at the current record.
	 * If the new record does not fit into an existing block,
	 * then a new block is appended to the file.
	 */
	public void insert() {
		while (!rp.insert()) {
			if (atLastBlock()){
				appendBlock();
			}
			moveTo(currentblknum + 1);
		}
		this.lastWrittenRecord = this.currentRid();
		this.updateWrite();
	}

	/**
	 * Positions the current record as indicated by the
	 * specified RID. 
	 * @param rid a record identifier
	 */
	public void moveToRid(RID rid) {
		moveTo(rid.blockNumber());
		rp.moveToId(rid.id());
	}

	/**
	 * Returns the RID of the current record.
	 * @return a record identifier
	 */
	public RID currentRid() {
		int id = rp.currentId();
		return new RID(currentblknum, id);
	}

	private void moveTo(int b) {
		if (rp != null){ //editato qui
			rp.close(); 
		}
		currentblknum = b;
		Block blk = new Block(filename, currentblknum);
		rp = new RecordPage(blk, ti, tx);
	}

	private boolean atLastBlock() {
		return currentblknum == tx.size(filename) - 1;
	}

	private void appendBlock() {
		RecordFormatter fmtr = new RecordFormatter(ti);
		tx.append(filename, fmtr);
	}

	private void updateRead(){
		RID rid = this.lastReadRecord;
		if(!this.statsRecord.containsKey(rid)){
			this.statsRecord.put(rid, new BasicRecordStats());
		}
		this.statsRecord.get(rid).incReadRecord();
	}

	private void updateWrite(){
		RID rid = this.lastWrittenRecord;//this.currentRid();
		if(!this.statsRecord.containsKey(rid)){
			this.statsRecord.put(rid, new BasicRecordStats());
		}
		this.statsRecord.get(rid).incWrittenRecord();
	}

	private void updateReadFields(){
		RID rid = this.lastReadRecord;
		if(!this.statsRecord.containsKey(rid)){
			this.statsRecord.put(rid, new BasicRecordStats());
			this.statsRecord.get(rid).incReadRecord();
		}
		this.statsRecord.get(rid).incReadFieldsRecord();
	}

	private void updateWrittenFields(){
		RID rid = this.lastWrittenRecord;//this.currentRid();
		if(!this.statsRecord.containsKey(rid)){
			this.statsRecord.put(rid, new BasicRecordStats());
			this.statsRecord.get(rid).incWrittenRecord();
		}
		this.statsRecord.get(rid).incWrittenFieldsRecord();
	}

	
	public String recordFileStatsToString(){
		StringBuilder st = new StringBuilder();
		Map<RID,BasicRecordStats> allStats = this.statsRecord;
		st.append("-----------RECORD STATS-----------");
		for (Map.Entry<RID, BasicRecordStats> entry : allStats.entrySet()) {
			st.append("\n "+entry.getKey().toString() + " : " + entry.getValue().toString());
		}
		st.append("\n Ultimo RID scritto: "+ this.lastWrittenRecord);
		st.append("\n Ultimo RID   letto: "+ this.lastReadRecord);
		st.append("\n -----------END RECORD STATS-----------");
		return st.toString();
	}
	
	public String toString(){
		StringBuilder st = new StringBuilder();
		st.append("\n-  filename: " + this.filename);
		st.append("\n-crnt blk_n: " + this.currentblknum);
		st.append("\n- lastReadR: " + this.lastReadRecord.toString());
		st.append("\n-lastWriteR: " + this.lastWrittenRecord.toString());
		return st.toString();
	}
	
	public void resetStats(){
		this.statsRecord = new HashMap<RID,BasicRecordStats>();
		this.lastReadRecord = null;
		this.lastWrittenRecord = null;
	}

	public Map<RID, BasicRecordStats> getStatsRecord() {
		return statsRecord;
	}
	
	

}