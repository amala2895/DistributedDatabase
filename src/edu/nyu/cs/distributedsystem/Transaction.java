package edu.nyu.cs.distributedsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class Transaction {

  private TransactionType txn_type;
  private int txn_id;
  private long txn_start_time;
  private List<Operation> txn_operations;
  private Map<Variable, Integer> commitmap;

  Transaction(int id, long start_time, String trans_type) {
    this.txn_id = id;
    this.txn_start_time = start_time;
    if (trans_type == "RW")
      this.txn_type = TransactionType.RW;
    else
      this.txn_type = TransactionType.RO;
    txn_operations = new ArrayList<>();
    commitmap = new HashMap<>();
  }

  TransactionType getType() {
    return this.txn_type;
  }

  void addOperationToTransaction(Operation oper) {
    txn_operations.add(oper);
  }

  void addOperationToCommitMap(Variable v, int i) {
    System.out.println("Writing to variable x" + v.getIndex() + ": " + i);
    commitmap.put(v, i);
  }

  boolean checkInCommitMap(int i) {
    for (Entry<Variable, Integer> e : commitmap.entrySet()) {

      if (e.getKey().getIndex() == i) {
        System.out.println("Read value of x" + e.getKey().getIndex() + ": " + e.getValue());
        return true;
      }
    }
    return false;
  }


  void readOperation(Variable v) {

    System.out.println("Read value of x" + v.getIndex() + ": " + v.getVal());

  }

  void commit() {
    for (Entry<Variable, Integer> e : commitmap.entrySet()) {
      Variable v = e.getKey();
      v.setVal(e.getValue());

    }

  }

}
