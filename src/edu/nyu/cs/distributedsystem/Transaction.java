package edu.nyu.cs.distributedsystem;

import java.util.ArrayList;
import java.util.List;

class Transaction {

  private TransactionType txn_type;
  private int txn_id;
  private long txn_start_time;
  private List<Operation> txn_operations;

  Transaction(int id, long start_time, String trans_type) {
    this.txn_id = id;
    this.txn_start_time = start_time;
    if (trans_type == "RW")
      this.txn_type = TransactionType.RW;
    else
      this.txn_type = TransactionType.RO;
    txn_operations = new ArrayList<>();
  }

  TransactionType getType() {
    return this.txn_type;
  }

  boolean addOperationToTransaction(Operation oper) {
    return true;
  }

  void executeOperations() {

  }

}
