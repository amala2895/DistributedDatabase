package edu.nyu.cs.distributedsystem;

class Operation {
  private int transaction_id;
  private int variable_id;
  private int variable_value;
  private long start_time;
  private boolean is_write_operation;
  private boolean is_end_operation;

  Operation() {
    this.is_write_operation = false;
    this.is_end_operation = false;
  }

  Operation(int trans_id, int var_id, int var_value) {
    this.transaction_id = trans_id;
    this.variable_id = var_id;
    this.variable_value = var_value;
    this.is_write_operation = true;
    this.is_end_operation = false;
    // this.start_time = start_time;
  }

  Operation(int trans_id, int var_id) {
    this.transaction_id = trans_id;
    this.variable_id = var_id;
    this.is_write_operation = false;
    this.is_end_operation = false;
    // this.start_time = start_time;
  }

}
