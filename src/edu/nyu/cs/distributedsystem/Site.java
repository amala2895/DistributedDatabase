
package edu.nyu.cs.distributedsystem;

import java.util.HashMap;
import java.util.Map;

class Site {
  enum Status {
    DOWN, RECOVERING, UP;
  }

  private Status currentStatus;
  private Map<Integer, Variable> indexVariable;
  private int siteNumber;

  Site(int i) {
    siteNumber = i;
    indexVariable = new HashMap<Integer, Variable>();
    currentStatus = Status.UP;
  }

  int getSiteNumber() {
    return siteNumber;
  }
  
 
  Status getSiteStatus() {
    return currentStatus;
  }

  void setSiteStatus(Status s) {
    currentStatus = s;
  }

  void addVariable(int i, int val) {
    Variable newVariable = new Variable(i, val);
    indexVariable.put(i, newVariable);
  }
  boolean hasVariable(int idx) {
    return indexVariable.containsKey(idx);
  }

  Variable getVariable(int val_id) {
	  return indexVariable.get(val_id);
  }

}
