package edu.nyu.cs.distributedsystem;

import java.util.ArrayList;
import java.util.List;

class Transaction{
	private
		int transact_id;
		int transact_start_time;
		List<Operation> transact_operations;
		
		Transaction(int id, int start_time){
			this.transact_id = id;
			this.transact_start_time = start_time;
			transact_operations = new ArrayList<>();
		}
		
		boolean addOperationToTransaction(Operation oper) {
			return true;
		}

}
