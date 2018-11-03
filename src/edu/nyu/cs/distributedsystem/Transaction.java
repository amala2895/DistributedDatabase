package edu.nyu.cs.distributedsystem;

import java.util.ArrayList;
import java.util.List;

import edu.nyu.cs.distributedsystem.Site.Status;

class Transaction{
	
		enum Type {
	    RW, RO;
		}

		private Type txn_type;
		private int txn_id;
		private long txn_start_time;
		private List<Operation> txn_operations;
		
		Transaction(int id, long start_time, String trans_type){
			this.txn_id = id;
			this.txn_start_time = start_time;
			txn_operations = new ArrayList<>();
		}
		
		boolean addOperationToTransaction(Operation oper) {
			return true;
		}
		
		void executeOperations() {
			
		}
		


}
