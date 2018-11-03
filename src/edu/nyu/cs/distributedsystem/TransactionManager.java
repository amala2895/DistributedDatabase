package edu.nyu.cs.distributedsystem;

import java.util.HashMap;
import java.util.Map;
import java.time.Instant;

public class TransactionManager {
	
	static Map<Integer,Integer> transaction_variable_map = new HashMap<Integer,Integer>();
	static Map<Integer,Transaction> transactions = new HashMap<Integer,Transaction>();
	
	//This function creates a transaction
	public static void beginTransaction(int trans_id, String tran_type) {
		
		long currTime = Instant.now().getEpochSecond();
		Transaction txn = new Transaction(trans_id, currTime);
		
		transactions.put(trans_id, txn);
	}
	
	//This function commits a transaction
	public static void  commitTransaction(int trans_id) {
		
		Transaction txn;
		if(transactions.containsKey(trans_id))
			txn = transactions.get(trans_id);
		
	}
	
	//This function ends a transaction by calling unlockVariables function
	public static void endTransaction(int trans_id) {
	
		commitTransaction(trans_id);
	}
	
	//This function puts a lock on the variable being written by some transaction
	public static void lockVariable(int var) {
		
	}
	
	//This function remove lock from the variable once the transaction commits/aborts
	public static void unlockVariable(int var) {
		
	}
	
	
	//This function will be called when there is a write operation by any transaction
	public static boolean addVariableToMap(int trans_id, int var_id) {
		
		/*Before adding to the map check if any other transaction 
		 * has lock on the variable */
		if(transaction_variable_map.containsKey(var_id))
			return false; //Some transaction has already lock on the variable
		
		transaction_variable_map.put(var_id, trans_id);
		return true;	
		
	}
	
	public static void makeWriteOperation(int trans_id,int var_id, int var_value) {
		
	}
	
	public static void makeReadOperation(int trans_id, int var_id) {
	
	} 
	
	//This function will make a site down
	public static void failSite(int site_id) {
	
	}

	 //This function will recover a site from failure
	 public static void recoverSite(int site_id) {
		 
	 }

}
