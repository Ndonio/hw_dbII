package simpledb.stats;

public class BasicFileStats{
	
	private int blockRead, blockWritten;
	
	public BasicFileStats(){
		this.blockRead = 0;
		this.blockWritten=0;
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
	
	@Override
	public String toString(){
		String rb = "\n - Block read   : ".concat(Integer.toString(this.blockRead));
		String wb = "\n - Block written: ".concat(Integer.toString(this.blockWritten));
		return rb+wb;
	}
	
}
