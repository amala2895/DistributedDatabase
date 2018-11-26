
package edu.nyu.cs.distributedsystem;

import java.util.HashMap;
import java.util.Map;

class Site {

  private SiteStatus currentStatus;
  private Map<Integer, Variable> indexVariable;
  private int siteNumber;

  Site(int i) {
    siteNumber = i;
    indexVariable = new HashMap<Integer, Variable>();
    currentStatus = SiteStatus.UP;
  }

  int getSiteNumber() {
    return siteNumber;
  }


  SiteStatus getSiteStatus() {
    return currentStatus;
  }

  void setSiteStatus(SiteStatus s) {
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

  int getVariableValue(int i) {
    return indexVariable.get(i).getVal();
  }
  
  Map<Integer, Variable> getIndexVariable() {
	  return indexVariable;
  }

  void printVariables() {
    for (int i = 1; i <= 20; i++) {
      if (indexVariable.containsKey(i)) {
        System.out.print("x" + i + ": " + indexVariable.get(i).getVal() + ", ");
      }
    }
  }


}
