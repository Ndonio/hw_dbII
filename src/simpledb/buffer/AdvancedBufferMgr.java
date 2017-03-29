package simpledb.buffer;

import java.util.Calendar;

import simpledb.file.*;
import simpledb.server.SimpleDB;

/**
 * Manages the pinning and unpinning of buffers to blocks.
 * @author Edward Sciore
 *
 */
class AdvancedBufferMgr {
	private Buffer[] bufferpool;
	private int numAvailable;

	private int indexOfLastReplacedPage; //aggiunta stats
	private static final boolean ELOQUENT = SimpleDB.CHATTY;//aggiunta stats

	/**
	 * Creates a buffer manager having the specified number 
	 * of buffer slots.
	 * This constructor depends on both the {@link FileMgr} and
	 * {@link simpledb.log.LogMgr LogMgr} objects 
	 * that it gets from the class
	 * {@link simpledb.server.SimpleDB}.
	 * Those objects are created during system initialization.
	 * Thus this constructor cannot be called until 
	 * {@link simpledb.server.SimpleDB#initFileAndLogMgr(String)} or
	 * is called first.
	 * @param numbuffs the number of buffer slots to allocate
	 */
	AdvancedBufferMgr(int numbuffs) {
		bufferpool = new Buffer[numbuffs];
		numAvailable = numbuffs;
		this.indexOfLastReplacedPage=-1; //aggiunta stats
		for (int i=0; i<numbuffs; i++)
			bufferpool[i] = new Buffer();
	}

	/**
	 * Flushes the dirty buffers modified by the specified transaction.
	 * @param txnum the transaction's id number
	 */
	synchronized void flushAll(int txnum) {
		for (Buffer buff : bufferpool)
			if (buff.isModifiedBy(txnum))
				buff.flush();
	}

	/**
	 * Pins a buffer to the specified block. 
	 * If there is already a buffer assigned to that block
	 * then that buffer is used;  
	 * otherwise, an unpinned buffer from the pool is chosen.
	 * Returns a null value if there are no available buffers.
	 * @param blk a reference to a disk block
	 * @return the pinned buffer
	 */
	synchronized Buffer pin(Block blk) {
		if(ELOQUENT)
			System.out.println("Devo fare pin di: "+ blk.toString());
		Buffer buff = findExistingBuffer(blk);
		if (buff == null) {
			if(ELOQUENT)
				System.out.println("Blocco non in buffer ecco lo "+ this.toString());
			buff = chooseUnpinnedBuffer();
			if (buff == null)
				return null;
			if(ELOQUENT)
				System.out.println("Sostituisco: "+buff.toString());
			buff.assignToBlock(blk);
		}
		else{
			if(ELOQUENT)
				System.out.println("Blocco già in buffer");
		}
		if (!buff.isPinned())
			numAvailable--;
		buff.pin();
		buff.setReadInTimestamp(Calendar.getInstance().getTimeInMillis());
		if(ELOQUENT)
			System.out.println("È stato effettuato il pin! Nuovo "+this.toString());
		return buff;
	}

	/**
	 * Allocates a new block in the specified file, and
	 * pins a buffer to it. 
	 * Returns null (without allocating the block) if 
	 * there are no available buffers.
	 * @param filename the name of the file
	 * @param fmtr a pageformatter object, used to format the new block
	 * @return the pinned buffer
	 */
	synchronized Buffer pinNew(String filename, PageFormatter fmtr) {
		Buffer buff = chooseUnpinnedBuffer();
		if (buff == null)
			return null;
		buff.assignToNew(filename, fmtr);
		numAvailable--;
		buff.pin();
		return buff;
	}

	/**
	 * Unpins the specified buffer.
	 * @param buff the buffer to be unpinned
	 */
	synchronized void unpin(Buffer buff) {
		buff.unpin();
		if (!buff.isPinned())
			numAvailable++;
	}

	/**
	 * Returns the number of available (i.e. unpinned) buffers.
	 * @return the number of available buffers
	 */
	int available() {
		return numAvailable;
	}

	private Buffer findExistingBuffer(Block blk) {
		for (Buffer buff : bufferpool) {
			Block b = buff.block();
			if (b != null && b.equals(blk))
				return buff;
		}
		return null;
	}

	//test pourpose only.
	private Buffer chooseUnpinnedBuffer() {
		switch (SimpleDB.STRATEGY) {
        case "lru":
           return this.chooseUnpinnedBuffer_lru();
        case "fifo":
           return this.chooseUnpinnedBuffer_fifo();
        case "clock":
           return this.chooseUnpinnedBuffer_clock();
        case "lpu":
        	return this.chooseUnpinnedBuffer_lup();
        default : 
        	return this.chooseUnpinnedBuffer_naive();
		}
	}

	private Buffer chooseUnpinnedBuffer_naive(){
		if(ELOQUENT)
			System.out.println("Strategia: NAIVE");
		for (Buffer buff : bufferpool)
			if (!buff.isPinned())
				return buff;
		return null;
	}

	private Buffer chooseUnpinnedBuffer_clock(){
		if(ELOQUENT)
			System.out.println("Strategia: CLOCK");

		int printIndex = this.indexOfLastReplacedPage+1;
		if(ELOQUENT)
			System.out.println("Ultima pagina usata: " + printIndex + "\n Dovrei scegliere: "+(printIndex+1));
		for (int i=0;i<this.bufferpool.length;i++){
			printIndex = this.updateIndexOfLastReplacedPage()+1;		
			Buffer app = this.bufferpool[this.indexOfLastReplacedPage];
			if (!app.isPinned()){
				if(ELOQUENT)
					System.out.println("Scelta: "+printIndex);
				return app;	
			}
			if(ELOQUENT)
				System.out.println("La pagina è pinnata da qualche altro thread... continuo la ricerca");
		}
		return null;
	}

	private int updateIndexOfLastReplacedPage(){
		if(this.indexOfLastReplacedPage==this.bufferpool.length-1){
			this.indexOfLastReplacedPage=0; //restart
		}
		else{
			this.indexOfLastReplacedPage++;
		}
		return this.indexOfLastReplacedPage;
	}

	private Buffer chooseUnpinnedBuffer_fifo(){
		if(ELOQUENT)
			System.out.println("Strategia: FIFO");
		Buffer res = null;
		Long timeRef = Calendar.getInstance().getTimeInMillis(); //NOW
		for(Buffer buff : this.bufferpool){
			if(buff.getReadInTimestamp()<timeRef){
				timeRef=buff.getReadInTimestamp(); //update min unpintime ref
				if(!buff.isPinned()){
					res = buff;	
				}
			}
		}
		return res;
	}

	private Buffer chooseUnpinnedBuffer_lru(){
		if(ELOQUENT)
			System.out.println("Strategia: LRU");
		Buffer res = null;
		Long timeRef = Calendar.getInstance().getTimeInMillis(); //NOW
		for(Buffer buff : this.bufferpool){
			if(buff.getLastUnpinTimestamp()<timeRef){
				timeRef=buff.getLastUnpinTimestamp(); //update min unpintime ref
				if(!buff.isPinned()){
					res = buff;	
				}
			}
		}
		return res;
	}

	private Buffer chooseUnpinnedBuffer_lup(){
		if(ELOQUENT)
			System.out.println("Strategia: LUP");
		Buffer res = null;
		int pins = this.bufferpool[0].getNumOfPins();
		for(Buffer buff : this.bufferpool){
			if(buff.getNumOfPins()<=pins){
				if(!buff.isPinned()){
					res = buff;	
				}
			}
		}
		return res;
	}

	/**
	 * Return a description for the buffer manager: 
	 * */
	public String toString(){
		String str = "Stato del buffer: ";
		int i =1;
		for(Buffer buff : this.bufferpool){
			str=str.concat(("\n  "+i+")"+buff.toString()));
			i++;
		}
		return str; 
	}


}
