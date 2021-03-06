
package edu.nyu.cs.distributedsystem;

import java.util.HashMap;
import java.util.Map;

/**
 * This class holds the site details, like whether site is up or down and all the variables present
 * on this site.
 * 
 * @author Amala Deshpande and Anshu Tomar
 *
 */
class Site {

  private SiteStatus currentStatus;
  /**
   * List of variables residing on this site
   * 
   */
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

    if (s == SiteStatus.DOWN) {
      for (Integer k : indexVariable.keySet()) {
    	if(k % 2 == 0)
    		indexVariable.get(k).setJustRecovered(true);
      }
    }
  }

  /**
   * Creates new variable object, adds it
   * 
   * @param i
   * @param val
   */
  void addVariable(int i, int val) {
    Variable newVariable = new Variable(i, val, siteNumber);
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

  void changeRecoveringStatus() {

    if (currentStatus == SiteStatus.RECOVERING) {
      for (Variable v : indexVariable.values()) {
        if (v.isJustRecovered() == true) {
          return;
        }
      }
      setSiteStatus(SiteStatus.UP);
    } else {
      System.out.println("Something is wrong");
    }
  }


}
