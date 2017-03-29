package simpledb.stats;

import java.util.ArrayList;
import java.util.List;


public class IntermediateFileStats {
	
	private int blockRead, blockWritten;
	private List<String>  readLog, writeLog;
	
    public IntermediateFileStats(){
    	this.blockRead= 0;
    	this.blockWritten = 0;
    	this.readLog = new ArrayList<String>();
    	this.writeLog = new ArrayList<String>();
    }
    
    public int getBlockRead() {
		return blockRead;
	}

	public void setBlockRead(int blockRead) {
		this.blockRead = blockRead;
	}

	public int getBlockWritten() {
		return blockWritten;
	}

	public void setBlockWritten(int blockWritten) {
		this.blockWritten = blockWritten;
	}
	
	public void blkWrittenInc(){
		 this.blockWritten++;
	}

	public void blkReadInc(){
		 this.blockRead++;
	}
	
	public List<String> getReadLog() {
		return readLog;
	}

	public List<String> getWriteLog() {
		return writeLog;
	}
	
	public void putReadLog(String readLog){
		this.readLog.add(readLog);
	}

	public void putWrittenLog(String writtenLog){
		this.writeLog.add(writtenLog);
	}
	
	public String toString(){
		String rb = "\n - Block read   : ".concat(Integer.toString(this.blockRead))+"\n";
		for(String str : this.readLog){
			rb=rb.concat(" --->"+str+"\n");
		}
		String wb = "\n - Block written: ".concat(Integer.toString(this.blockWritten))+"\n";
		for(String str : this.writeLog){
			wb=wb.concat(" --->"+str+"\n");
		}
		return rb+wb;
	}
	
}
