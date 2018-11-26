package edu.nyu.cs.distributedsystem;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TransactionManager {
  static Map<Integer, Integer> transaction_variable_map = new HashMap<Integer, Integer>();
  static Map<Integer, Transaction> transactions = new HashMap<Integer, Transaction>();
  static Map<Integer, Site> sites = new HashMap<Integer, Site>();
  static Map<Integer, List<Integer>> variable_site_map = new HashMap<Integer, List<Integer>>();

  static List<Tuple<Transaction, Operation>> waitingOperations =
      new ArrayList<Tuple<Transaction, Operation>>();

  static Map<Integer, List<Variable>> variable_copies_map = new HashMap<Integer, List<Variable>>();


  private static class Tuple<X, Y> {
    private final X x;
    private final Y y;

    private Tuple(X x, Y y) {
      this.x = x;
      this.y = y;
    }
  }

  // Initialize the sites
  public static void initializeSites() {

    System.out.println("initializeSites()");
    Site site = null;
    for (int i = 1; i <= 10; i++) {
      site = new Site(i);
      sites.put(i, site);
    }
  }

  // Initialize the variables
  public static void initializeVariables() {

    for (int i = 1; i <= 20; i++) {

      System.out.println("initializeVariables()");
      List<Variable> variablecopies = new LinkedList<Variable>();
      List<Integer> siteList = new LinkedList<Integer>();
      Variable var = null;
      if (i % 2 == 0) {
        for (int j = 1; j <= 10; j++) {
          sites.get(j).addVariable(i, 10 * i);
          var = sites.get(j).getVariable(i);
          siteList.add(j);
          variablecopies.add(var);
        }
      } else {
        for (int j = 1; j <= 10; j++) {
          if (j == 1 + i % 10) {
            sites.get(j).addVariable(i, 10 * i);
            var = sites.get(j).getVariable(i);
            variablecopies.add(var);
            siteList.add(j);
          }
        }
      }
      variable_site_map.put(i, siteList);
      variable_copies_map.put(i, variablecopies);
    }
  }

  // This function creates a transaction
  public static void beginTransaction(int trans_id, String trans_type) {

    System.out.println("beginTransaction");
    long currTime = Instant.now().getEpochSecond();
    Transaction txn = new Transaction(trans_id, currTime, trans_type);
    transactions.put(trans_id, txn);
  }


  // This function will create the write operation and add it to the transaction
  public static void makeWriteOperation(int trans_id, int var_id, int var_value) {

    System.out.println("makeWriteOperation");

    Transaction txn = null;

    Operation oper = new Operation(trans_id, var_id, var_value);



    if (transactions.containsKey(trans_id))
      txn = transactions.get(trans_id);

    if (txn != null) {
      writeOperation(txn, oper);
    } else {
      // aborted
      System.out.println("Aborted :" + trans_id);
    }

  }

  private static boolean writeOperation(Transaction txn, Operation oper) {


    System.out
        .println("writeOperation:: Trans_id = " + txn.getId() + "Var id = " + oper.getvarid());
    int var_id = oper.getvarid();
    int trans_id = txn.getId();
    List<Variable> variableList = null;
    if (!isVariableLocked(var_id)) {
      // Along side locking the variable, check if the variable is justRecovered
      // If so, set the justRecovered flag of the variable to false for the site
      // which is recovering. (Not for the DOWN or UP site)
      // TO DO .. check for all the sites where this variable resides
      // If the site status is RECOVERING, set the justRecovered flag to false
      // for that copy of the variable.

      System.out.println("writeOperation::Variable not locked");
      writelockVariable(var_id);

      if (var_id % 2 == 0) {
        for (int j = 1; j <= 10; j++) {

          // Check for the site availability before making any operation
          // if(sites.get(j).getSiteStatus() != SiteStatus.DOWN)
          // {
          if (sites.get(j).getVariable(var_id).isJustRecovered())
            sites.get(j).getVariable(var_id).setJustRecovered(false);


          // TO DO ..check all the variable on the site
          // If justRecovered is set to false for each and site was recovering
          // set it to UP.

          // }
        }
      } else {
        for (int j = 1; j <= 10; j++) {
          if (j == 1 + var_id % 10) {
            if (sites.get(j).getVariable(var_id).isJustRecovered())
              sites.get(j).getVariable(var_id).setJustRecovered(false);


            // TO DO ..check all the variable on the site
            // If justRecovered is set to false for each and site was recovering
            // set it to UP.
          }
        }
      }
      // getting all variable copies
      variableList = variable_copies_map.get(var_id);
      // not sure about use of this below statement
      txn.addOperationToTransaction(oper);

      // add to transaction variable-transaction map
      addVariableToMap(trans_id, var_id);

      // since we got the lock we can execute it
      // add all the variables of each site to the commit map of transaction
      for (Variable v : variableList) {

        txn.addOperationToCommitMap(v, oper.getValue());
      }

    } else {
      // check if already waiting
      // if (!alreadyWaiting(txn, oper)) {


      // }


      // add to waiting transaction list
      // we need to add edge to the graph
      // first we check if an edge already exists that is
      // check the dependency between transactions
      // and check if there is a deadlock;
      // find the transaction which has lock on this variables
      // and add the dependency edge.


      System.out.println("writeOperation::Variable is locked");
      int independent_trans_id = transaction_variable_map.get(var_id);

      if (checkAndAddDependency(trans_id, independent_trans_id)) {

        if (!alreadyWaiting(txn, oper)) {
          System.out.println("writeOperation:: Not already waiting");
          Tuple<Transaction, Operation> t = new Tuple<Transaction, Operation>(txn, oper);
          waitingOperations.add(t);

        }

      } else {
        System.out.println("writeOperation::checkAndAddDependency returned false");
        System.out.println(" Dependent Transaction id = " + trans_id + " Independent trans id = "
            + independent_trans_id);

      }

      return false;
    }
    return true;
  }

  //
  private static boolean alreadyWaiting(Transaction txn, Operation oper) {
    System.out.println("checking already  waiting");
    if (waitingOperations.isEmpty())
      return false;
    for (Tuple<Transaction, Operation> t : waitingOperations) {
      if (t.x.equals(txn) && t.y.equals(oper))
        return true;
    }

    return false;
  }

  private static boolean checkAndAddDependency(int trans_id, int independent_trans_id) {

    System.out.println("checkAndAddDependency" + trans_id + "  " + independent_trans_id);



    // check if there is a deadlock
    if (DeadlockHandler.isThereACycleInGraph(trans_id, independent_trans_id)) {

      System.out.println("checkAndAddDependency:: Cycle in graph. DEADLOCK!!!");
      // deadlock found
      // Abort the latest transaction
      Transaction t1 = transactions.get(trans_id);
      Transaction t2 = transactions.get(independent_trans_id);
      // List<Integer> freedTrans = new ArrayList<Integer>();

      if (t1.getStartTime() <= t2.getStartTime()) {

        // freedTrans = DeadlockHandler.getFreedTransactions(trans_id);
        releaseResources(trans_id);
      } else {
        // freedTrans = DeadlockHandler.getFreedTransactions(independent_trans_id);
        releaseResources(independent_trans_id);
      }
      System.out.println("Aborted :" + trans_id);
      clearWaitingOperations();

      return false;
    }

    else {

      // if edge exists we don't need to do anything
      if (!DeadlockHandler.ifThereIsAnEdgeFromT1toT2(trans_id, independent_trans_id)) {

        System.out.println("checkAndAddDependency::Edge is not present");
        System.out.println("checkAndAddDependency::Add dependency edge");
        // no deadlock hence we can add the edge
        DeadlockHandler.addDependencyEdge(independent_trans_id, trans_id);
      }
    }

    return true;
  }

  // This function will create the read operation and add it to the transaction
  public static void makeReadOperation(int trans_id, int var_id) {

    System.out.println("makeReadOperation");
    Transaction txn = null;
    Operation oper = new Operation(trans_id, var_id);
    if (transactions.containsKey(trans_id))
      txn = transactions.get(trans_id);
    if (txn != null) {
      readOperation(txn, oper);
    } else {
      // aborted
      System.out.println("Aborted :" + trans_id);
    }

  }

  private static void readOperation(Transaction txn, Operation oper) {

    System.out.println("readOperation");
    int var_id = oper.getvarid();
    int trans_id = txn.getId();
    if (txn.getType() == TransactionType.RW) {
      txn.addOperationToTransaction(oper);

      // first we need to check if own transaction already changed its value


      if (txn.checkInCommitMap(var_id)) {
        // reading value from commit map in transaction class

      } else {
        if (!isVariableWriteLocked(var_id)) {

          readlockVariable(var_id);

          for (Variable v : variable_copies_map.get(var_id)) {

            txn.addOperationToReadMap(v);
          }

          // we have the lock so we can read it
          Variable v = variable_copies_map.get(var_id).get(0);
          txn.readOperation(v);

        } else {
          // wait
          /*
           * // if (!alreadyWaiting(txn, oper)) { Tuple<Transaction, Operation> t = new
           * Tuple<Transaction, Operation>(txn, oper); waitingOperations.add(t); // } // check the
           * dependency between transactions // and check if there is a deadlock; int
           * independent_trans_id = transaction_variable_map.get(var_id);
           * checkAndAddDependency(trans_id, independent_trans_id);
           */

          int independent_trans_id = transaction_variable_map.get(var_id);

          if (!checkAndAddDependency(trans_id, independent_trans_id)) {
            if (!alreadyWaiting(txn, oper)) {
              Tuple<Transaction, Operation> t = new Tuple<Transaction, Operation>(txn, oper);
              waitingOperations.add(t);

            }
          }
        }
      }
    } else {



      // read only transaction case

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
    if (transactions.containsKey(trans_id)) {
      if (commitTransaction(trans_id)) {
        // after committing we need to check which other transactions are waiting
        clearWaitingOperations();
      }

    } else {
      System.out.println("Aborted Transaction T" + trans_id);
    }
  }

  private static void clearWaitingOperations() {
    Iterator<Tuple<Transaction, Operation>> iter = waitingOperations.iterator();

    System.out.println("clear waiting operations");
    while (iter.hasNext()) {
      // get transaction id
      // check if read or write operation
      Tuple<Transaction, Operation> t = iter.next();
      Operation oper = t.y;
      Transaction txn = t.x;
      if (oper.isOperationWrite()) {
        if (writeOperation(txn, oper)) {
          iter.remove();
        }
      } else {
        readOperation(txn, oper);
      }


    }

  }

  // This function commits a transaction
  private static boolean commitTransaction(int trans_id) {
    Transaction txn = null;
    if (transactions.containsKey(trans_id))
      txn = transactions.get(trans_id);
    else
      return false;

    if (txn != null)
      txn.commit();

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

  // This function puts a lock on the variable being read by some transaction returns the variable
  // on one site
  private static void readlockVariable(int var_id) {
    List<Integer> s = variable_site_map.get(var_id);

    for (Integer i : s) {
      // check if site is up
      if (sites.get(i).getSiteStatus() == SiteStatus.UP) {
        // check if variable is locked

        sites.get(i).getVariable(var_id).readLockVariable();
        // need to lock only on one site



      }
    }


  }

  // This function puts a lock on the variable being written by some transaction
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



  // This function will be called when there is a write operation by any transaction
  private static boolean addVariableToMap(int trans_id, int var_id) {


    if (transaction_variable_map.containsKey(var_id))
      return false; // Some transaction has already lock on the variable

    transaction_variable_map.put(var_id, trans_id);
    return true;

  }


  // Abort/End the transaction and release resources
  private static void releaseResources(int trans_id) {
    System.out.println("Releasing resources");
    // Remove the hold of transaction from the variables
    for (int var_id : transaction_variable_map.keySet()) {
      if (transaction_variable_map.get(var_id).equals(trans_id)) {
        transaction_variable_map.remove(var_id);
        List<Variable> variables = variable_copies_map.get(var_id);
        for (Variable v : variables) {
          v.unlockVariable();
        }
      }


    }

    // remove from waiting operations
    Transaction txn = transactions.get(trans_id);
    for (Tuple<Transaction, Operation> t : waitingOperations) {
      if (t.x.equals(txn)) {
        waitingOperations.remove(t);



      }
    }
    // Remove the transaction from the current set of transactions
    transactions.remove(trans_id);

    // Remove dependency edge if any
    DeadlockHandler.removeDependencyEdge(trans_id);

  }

}
