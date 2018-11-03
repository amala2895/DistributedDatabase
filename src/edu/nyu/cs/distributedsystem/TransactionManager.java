package edu.nyu.cs.distributedsystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TransactionManager {
	
	static Map<Integer,List<Integer>> transaction_variable_map = new HashMap<Integer,List<Integer>>();
	
	//This function creates a transaction
	static void beginTransaction(int trans_id, String tran_type) {
		
	}
	
	//This function commits a transaction
	static void  commitTransaction(int trans_id) {
		
	}
	
	//This function ends a transaction by calling unlockVariables function
	static void endTransaction(int trans_id) {
	
		commitTransaction(trans_id);
	}
	
	//This function puts a lock on the variable being written by some transaction
	static void lockVariable(int var) {
		
	}
	
	//This function remove lock from the variable once the transaction commits/aborts
	static void unlockVariable(int var) {
		
	}
	
	
	//This function will be called when there is a write operation by any transaction
	static void addVariableToMap(int trans_id, int var_id) {
		
		/*Before adding to the map check if any other transaction 
		 * has lock on the variable */
		
		
	}
	
	static void writeOperation(int trans_id,int var_id, int var_value) {
		
	}
	static void readOperation(int trans_id, int var_id) {
	
	} 
	
	//This function will make a site down
	static void failSite(int site_id) {
	
	}

	 //This function will recover a site from failure
	 static void recoverSite(int site_id) {
		 
	 }

}
