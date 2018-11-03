package edu.nyu.cs.distributedsystem;

import java.util.ArrayList;
import java.util.List;

class Transaction{
	private
		int txn_id;
		long txn_start_time;
		List<Operation> txn_operations;
		
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
