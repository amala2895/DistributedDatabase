package edu.nyu.cs.distributedsystem;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TransactionManager {

  static Map<Integer, Integer> transaction_variable_map = new HashMap<Integer, Integer>();
  static Map<Integer, Transaction> transactions = new HashMap<Integer, Transaction>();
  static Map<Integer, Site> sites = new HashMap<Integer, Site>();
  static Map<Integer, List<Integer>> variable_site_map = new HashMap<Integer, List<Integer>>();

  // Initialize the sites
  public static void initializeSites() {
    Site site = null;
    for (int i = 1; i <= 10; i++) {
      site = new Site(i);
      sites.put(i, site);
    }
  }

  // Initialize the variables
  public static void initializeVariables() {
    Variable var = null;
    for (int i = 1; i <= 20; i++) {
      var = new Variable(i, 10 * i);
      List<Integer> siteList = new LinkedList<Integer>();
      if (i % 2 == 0) {
        for (int j = 1; j <= 10; j++) {
          sites.get(j).addVariable(i, 10 * i);
          siteList.add(j);
        }
      } else {
        for (int j = 1; j <= 10; j++) {
          if (j == 1 + i % 10) {
            sites.get(j).addVariable(i, 10 * i);
            siteList.add(j);
          }
        }
      }
      variable_site_map.put(i, siteList);
    }
  }

  // This function creates a transaction
  public static void beginTransaction(int trans_id, String trans_type) {

    long currTime = Instant.now().getEpochSecond();
    Transaction txn = new Transaction(trans_id, currTime, trans_type);
    transactions.put(trans_id, txn);
  }


  // This function will create the write operation and add it to the transaction
  public static void makeWriteOperation(int trans_id, int var_id, int var_value) {

    Transaction txn = null;
    Operation oper = new Operation(trans_id, var_id, var_value);
    List<Integer> variableList = null;
    
    
    if (transactions.containsKey(trans_id))
      txn = transactions.get(trans_id);
    
    if (txn != null) {
      if (!isVariableLocked(var_id)) {
    	 // Along side locking the variable, check if the variable is justRecovered
    	  //If so, set the justRecovered flag of the variable to false for the site
    	  //which is recovering. (Not for the DOWN or UP site)
    	  // TO DO .. check for all the sites where this variable resides
    	  // If the site status is RECOVERING, set the justRecovered  flag to false 
    	  // for that copy of the variable.
        writelockVariable(var_id);
        
        if (var_id % 2 == 0) {
            for (int j = 1; j <= 10; j++) {
              if(sites.get(j).getVariable(var_id).isJustRecovered())
            	  sites.get(j).getVariable(var_id).setJustRecovered(false);
                   variableList = variable_site_map.get(j);
                   //TO DO ..check all the variable on the site 
                   //If justRecovered is set to false for each and site was recovering
                   // set it to UP.
                      
                   }
          } else {
            for (int j = 1; j <= 10; j++) {
              if (j == 1 + var_id % 10) {
            	  if(sites.get(j).getVariable(var_id).isJustRecovered())
                	  sites.get(j).getVariable(var_id).setJustRecovered(false);
            	      variableList = variable_site_map.get(j);
            	   //TO DO ..check all the variable on the site 
                  //If justRecovered is set to false for each and site was recovering
                  // set it to UP.
              }
            }
          }
        
        txn.addOperationToTransaction(oper);
      } else {
        // check the dependency between transactions
        // and check if there is a deadlock;
      }
    }

  }

  // This function will create the read operation and add it to the transaction
  public static void makeReadOperation(int trans_id, int var_id) {
    Transaction txn = null;
    Operation oper = new Operation(trans_id, var_id);
    if (transactions.containsKey(trans_id))
      txn = transactions.get(trans_id);
    if (txn != null) {
      if (txn.getType() == TransactionType.RO)
        txn.addOperationToTransaction(oper);
      else {
        if (!isVariableWriteLocked(var_id)) {
          readlockVariable(var_id);
          txn.addOperationToTransaction(oper);
        } else {
          // check the dependency between transactions
          // and check if there is a deadlock;
        }
      }
    }
  }

  // This function will make a site down
  public static void failSite(int site_id) {

	  sites.get(site_id).setSiteStatus(SiteStatus.DOWN);
  }

  // This function will recover a site from failure
  public static void recoverSite(int site_id) {
	  sites.get(site_id).setSiteStatus(SiteStatus.RECOVERING);
  }

  public static void dump() {
    for (int i = 1; i <= 10; i++) {
      Site s = sites.get(i);
      System.out.print("site " + i + " - ");
      s.printVariables();
      System.out.println();
    }
  }

  public static void dumpSite(int site) {
    Site s = sites.get(site);
    System.out.print("site " + site + " - ");
    s.printVariables();

  }

  public static void dumpVariable(int vid) {
    List<Integer> list = variable_site_map.get(vid);
    for (Integer i : list) {
      Site s = sites.get(i);
      System.out.println("site " + i + ": " + s.getVariableValue(i));
    }
  }

  // This function ends a transaction by calling unlockVariables function
  public static void endTransaction(int trans_id) {

    commitTransaction(trans_id);
  }


  // This function commits a transaction
  private static boolean commitTransaction(int trans_id) {
    Transaction txn = null;
    if (transactions.containsKey(trans_id))
      txn = transactions.get(trans_id);
    else
      return false;

    if (txn != null)
      txn.executeOperations();
    return true;
  }

  // check if variable is locked read or write
  // check on each site. returns false when found locked
  private static boolean isVariableLocked(int var_id) {
    boolean toReturn = false;
    for (Integer i : variable_site_map.get(var_id)) {
      Site s = sites.get(i);
      if (s.getSiteStatus() == SiteStatus.UP) {
        // if not just recovered only then check lock
        Variable v = s.getVariable(var_id);
        if (!v.isJustRecovered()) {
          // if read or write locked return true
          if (v.isReadLocked() || v.isWriteLocked()) {
            toReturn = true;

          }
          // break as we need to check on only one UP site. The rest of the sites should have the
          // same values
          break;
        }
      }

    }
    return toReturn;

  }

  // checks if variable is write locked
  private static boolean isVariableWriteLocked(int var_id) {
    boolean toReturn = false;
    for (Integer i : variable_site_map.get(var_id)) {
      Site s = sites.get(i);
      if (s.getSiteStatus() == SiteStatus.UP) {
        // if not just recovered only then check lock
        Variable v = s.getVariable(var_id);
        if (!v.isJustRecovered()) {
          // if read or write locked return true
          if (v.isWriteLocked()) {
            toReturn = true;

          }
          // break as we need to check on only one UP site. The rest of the sites should have the
          // same values
          break;
        }
      }

    }
    return toReturn;

  }

  // This function puts a lock on the variable being read by some transaction
  private static void readlockVariable(int var_id) {
    List<Integer> s = variable_site_map.get(var_id);
    for (Integer i : s) {
      // check if site is up
      if (sites.get(i).getSiteStatus() == SiteStatus.UP) {
        // check if variable is locked

        sites.get(i).getVariable(var_id).readLockVariable();

      }
    }

  }

  // This function puts a lock on the variable being read by some transaction
  private static void writelockVariable(int var_id) {
    List<Integer> s = variable_site_map.get(var_id);
    for (Integer i : s) {
      // check if site is up
      if (sites.get(i).getSiteStatus() == SiteStatus.UP) {
        // check if variable is locked

        sites.get(i).getVariable(var_id).writeLockVariable();

      }
    }

  }

  // This function remove lock from the variable once the transaction commits/aborts
  private static void unlockVariable(int var_id) {

  }


  // This function will be called when there is a write operation by any transaction
  private static boolean addVariableToMap(int trans_id, int var_id) {

    /*
     * Before adding to the map check if any other transaction has lock on the variable
     */
    if (transaction_variable_map.containsKey(var_id))
      return false; // Some transaction has already lock on the variable

    transaction_variable_map.put(var_id, trans_id);
    return true;

  }

}
