package simpledb.remote;

import simpledb.server.SimpleDB;
import simpledb.stats.*;
import simpledb.tx.Transaction;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

/**
 * The RMI server-side implementation of RemoteConnection.
 * @author Edward Sciore
 */
@SuppressWarnings("serial") 
class RemoteConnectionImpl extends UnicastRemoteObject implements RemoteConnection {
	private Transaction tx;

	/**
	 * Creates a remote connection
	 * and begins a new transaction for it.
	 * @throws RemoteException
	 */
	RemoteConnectionImpl() throws RemoteException {
		SimpleDB.fileMgr().resetMapStats();
		tx = new Transaction();
		printAllBlockStats(tx.toString()); //printStatsfor all block
	}

	/**
	 * Creates a new RemoteStatement for this connection.
	 * @see simpledb.remote.RemoteConnection#createStatement()
	 */
	public RemoteStatement createStatement() throws RemoteException {
		return new RemoteStatementImpl(this);
	}

	/**
	 * Closes the connection.
	 * The current transaction is committed.
	 * @see simpledb.remote.RemoteConnection#close()
	 */
	public void close() throws RemoteException {
		tx.commit();
	}

	// The following methods are used by the server-side classes.

	/**
	 * Returns the transaction currently associated with
	 * this connection.
	 * @return the transaction associated with this connection
	 */
	Transaction getTransaction() {  
		return tx;
	}

	/**
	 * Commits the current transaction,
	 * and begins a new one.
	 */
	void commit() {
		printAllBlockStats(tx.toString());
		tx.commit();
		tx = new Transaction();
	}

	/**
	 * Rolls back the current transaction,
	 * and begins a new one.
	 */
	void rollback() {
		tx.rollback();
		tx = new Transaction();
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

