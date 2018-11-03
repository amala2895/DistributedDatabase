package edu.nyu.cs.distributedsystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.Instant;

public class TransactionManager {
	
	static Map<Integer,Integer> transaction_variable_map = new HashMap<Integer,Integer>();
	static Map<Integer,Transaction> transactions = new HashMap<Integer,Transaction>();
	static Map<Integer,Site> sites = new HashMap<Integer,Site>();
	static Map<Integer,List<Site>> variable_site_map = new HashMap<Integer,List<Site>>();
	
	
	//Initialize the sites
	public static void initializeSites() {
		Site site = null;
		for(int i=1;i<=10;i++)
		{
			site = new Site(i);
			sites.put(i, site);
		}
	}
	
	//Initialize the variables
	public static void initializeVariables() {
		Variable var = null;
		
		for(int i=1;i<=20;i++) {
			var = new Variable(i , 10 * i);
			
			if(i%2 == 0){
				for(int j=1;j<=10;j++) {
					sites.get(j).addVariable(i,10*i);
				}
			}
			else {
				for(int j=1;j<=10;j++) {
					if(j == 1 + i % 10)
						sites.get(j).addVariable(i,10*i);
				}
			}
		}
	}
	
	//This function creates a transaction
	public static void beginTransaction(int trans_id, String trans_type) {
		
		long currTime = Instant.now().getEpochSecond();
		Transaction txn = new Transaction(trans_id, currTime, trans_type);
		
		transactions.put(trans_id, txn);
	}
	
	//This function commits a transaction
	public static boolean  commitTransaction(int trans_id) {
		
		Transaction txn = null;
		
		if(transactions.containsKey(trans_id))
			txn = transactions.get(trans_id);
		else
			return false;
		
		if(txn != null)
			txn.executeOperations();
		
		return true;
		
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
	
	//This function will create the write operation and add it to the transaction 
	public static void makeWriteOperation(int trans_id,int var_id, int var_value) {
		
		Transaction txn = null;
		Operation oper = new Operation(trans_id, var_id, var_value);
		
		if(transactions.containsKey(trans_id))
			txn = transactions.get(trans_id);
		
		
		if(txn != null)
			txn.addOperationToTransaction(oper);
		
	}
	
	//This function will create the read operation and add it to the transaction
	public static void makeReadOperation(int trans_id, int var_id) {

		Transaction txn = null;
		Operation oper = new Operation(trans_id, var_id);
		
		if(transactions.containsKey(trans_id))
			txn = transactions.get(trans_id);
		
		
		if(txn != null)
			txn.addOperationToTransaction(oper);
		
	
	} 
	
	//This function will make a site down
	public static void failSite(int site_id) {
	
	}

	 //This function will recover a site from failure
	 public static void recoverSite(int site_id) {
		 
	 }

}
