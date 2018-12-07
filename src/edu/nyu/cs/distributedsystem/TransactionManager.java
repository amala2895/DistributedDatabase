package edu.nyu.cs.distributedsystem;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is the Transaction Manager.
 * 
 * @author Amala Deshpande and Anshu Tomar
 *
 */
public class TransactionManager {

  static Map<Integer, Integer> transaction_variable_map = new HashMap<Integer, Integer>();
  // may be redundant
  static Map<Integer, Integer> transaction_variable_readOnly_map = new HashMap<Integer, Integer>();

  /**
   * This map holds all the transactions currently running. If a transaction is aborted it is
   * removed from this map. When tranaction ends it is removed from this map.
   */
  static Map<Integer, Transaction> transactions = new HashMap<Integer, Transaction>();

  /**
   * Stores all the sites with key as id and object as value
   */
  static Map<Integer, Site> sites = new HashMap<Integer, Site>();

  /**
   * Variable id is the key, and list of sites ids on which variable resides is the value
   */
  static Map<Integer, List<Integer>> variable_site_map = new HashMap<Integer, List<Integer>>();

  /**
   * This holds all the operations that wait due to variable being locked.
   */
  static List<Tuple<Transaction, Operation>> waitingOperations =
      new ArrayList<Tuple<Transaction, Operation>>();
  
  /**
   * This holds all the reading transactions-operations that wait due to either site being down or the
   * variable just got recovered.
   */
  static List<Tuple<Transaction, Operation>> waitingReadOperations =
      new ArrayList<Tuple<Transaction, Operation>>();
  
  /**
   * This holds all the writing transactions-operations that wait due to either site being down or the
   * variable just got recovered.
   */
  static List<Tuple<Transaction, Operation>> waitingWriteOperations =
      new ArrayList<Tuple<Transaction, Operation>>();

  /**
   * Variable id the key, List of Variable objects which are copies of the variable is the list
   */
  static Map<Integer, List<Variable>> variable_copies_map = new HashMap<Integer, List<Variable>>();


  private static class Tuple<X, Y> {
    private final X x;
    private final Y y;

    private Tuple(X x, Y y) {
      this.x = x;
      this.y = y;
    }
  }

  /**
   * Initializes the site
   */
  public static void initializeSites() {

    // System.out.println("initializeSites()");
    Site site = null;
    for (int i = 1; i <= 10; i++) {
      site = new Site(i);
      sites.put(i, site);
    }
  }

  /**
   * Initialize the 20 variables
   */
  public static void initializeVariables() {

    for (int i = 1; i <= 20; i++) {

      // System.out.println("initializeVariables()");
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



  /**
   * This function creates new Transaction
   * 
   * @param trans_id
   * @param trans_type
   */
  public static void beginTransaction(int trans_id, String trans_type) {

    // System.out.println("beginTransaction");
    long currTime = Instant.now().getEpochSecond();
    Transaction txn = new Transaction(trans_id, currTime, trans_type);
    transactions.put(trans_id, txn);

    if (trans_type == "RO") {
      setVariableListForReadOnlyTxn(txn);
    }
  }



  private static void setVariableListForReadOnlyTxn(Transaction t) {
    for (int j = 1; j <= 10; j++) {
      if (sites.get(j).getSiteStatus() == SiteStatus.UP) {
        for (Integer k : sites.get(j).getIndexVariable().keySet()) {
          Variable v = sites.get(j).getIndexVariable().get(k);

          t.getReadOnlyVarMap().put(k, v.getVal());
        }
      }
    }
  }


  private static void makeReadOnlyOperation(int trans_id, int var_id) {

    // System.out.println("makeReadOnlyOperation");

    Transaction txn = transactions.get(trans_id);
    txn.readVariableReadOnly(var_id);
    // may be redundant
    addVariableToReadOnlyMap(trans_id, var_id);

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


    // System.out.println("writeOperation:: Trans_id = " + txn.getId() + "Var id = " +
    // oper.getvarid());
    int var_id = oper.getvarid();
    int trans_id = txn.getId();
    List<Variable> variableList = null;
    // ********NEED TO CHANGE

    if (txn.checkForWrite(var_id, oper.getValue())) {
      // checking value in own
      System.out.println("writing new value to self locked variable");
      return true;
    }

    if (!isVariableLocked(var_id)) {
      // Along side locking the variable, check if the variable is justRecovered
      // If so, set the justRecovered flag of the variable to false for the site
      // which is recovering. (Not for the DOWN or UP site)
      // TO DO .. check for all the sites where this variable resides
      // If the site status is RECOVERING, set the justRecovered flag to false
      // for that copy of the variable.

      // System.out.println("writeOperation::Variable not locked");
      // getting all variable copies
      variableList = writelockVariable(var_id);
      
      if (!variableList.isEmpty()) {
    	  
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
      	  Tuple<Transaction, Operation> t = new Tuple<Transaction, Operation>(txn, oper);

            waitingWriteOperations.add(t);
          //releaseResources(trans_id);
          //clearWaitingOperations();
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

  /**
   * This method is called when a read operation arrives. If the the transaction was read only then
   * makereadOnlyOperation is called. If we dont find the transaction in the transaction list then
   * we assume that transaction was aborted.
   * 
   * @param trans_id
   * @param var_id
   */
  public static void makeReadOperation(int trans_id, int var_id) {

    Transaction txn = null;
    if (transactions.containsKey(trans_id))
      txn = transactions.get(trans_id);
    if (txn != null) {
      if (txn.getType() == TransactionType.RO) {
        makeReadOnlyOperation(trans_id, var_id);
        return;
      }
      // System.out.println("makeReadOperation");

      Operation oper = new Operation(trans_id, var_id);
      readOperation(txn, oper);
    } else {
      // aborted
      System.out.println("Aborted : " + trans_id);
    }

  }

  private static boolean readOperation(Transaction txn, Operation oper) {

    // System.out.println("readOperation");
    int var_id = oper.getvarid();
    int trans_id = txn.getId();

    //If the transaction has write lock on the variable already, it should
    // read the new value from its local copy.
    if (txn.checkForRead(var_id)) {
      // reading value from commit map in transaction class
      return true;
    }



    if (!isVariableWriteLocked(var_id)) {

      List<Variable> variablelist = readlockVariable(var_id);
      if (!variablelist.isEmpty()) {
        for (Variable v : variablelist) {

          txn.addOperationToReadMap(v);
        }

        // we have the lock so we can read it
        Variable v = variablelist.get(0);
        txn.readOperation(v);
        addVariableToMap(trans_id, var_id);
      } else {
    	  Tuple<Transaction, Operation> t = new Tuple<Transaction, Operation>(txn, oper);

          waitingOperations.add(t);
        //releaseResources(trans_id);
        //clearWaitingOperations();
      }
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

      if (checkAndAddDependency(trans_id, independent_trans_id)) {
        // System.out.println(txn.getId());
        if (!alreadyWaiting(txn, oper)) {
          Tuple<Transaction, Operation> t = new Tuple<Transaction, Operation>(txn, oper);

          waitingOperations.add(t);

        }
      }
      return false;
    }


    return true;
  }

  // This function will make a site down
  public static void failSite(int site_id) {

    sites.get(site_id).setSiteStatus(SiteStatus.DOWN);
    Map<Integer, Variable> variables = sites.get(site_id).getIndexVariable();

    for (Integer var : variables.keySet()) {
    	
        variables.get(var).unlockVariable();

        if (transaction_variable_map.containsKey(var))
          releaseResources(transaction_variable_map.get(var));
    }

  }

  // This function will recover a site from failure
  public static void recoverSite(int site_id) {
    sites.get(site_id).setSiteStatus(SiteStatus.UP);
    clearWaitingWriteOperations();
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
        transactions.remove(trans_id);
        Iterator<Map.Entry<Integer, Integer>> it = transaction_variable_map.entrySet().iterator();


        while (it.hasNext()) {
          Map.Entry<Integer, Integer> pair = (Map.Entry<Integer, Integer>) it.next();

          if (pair.getValue().equals(trans_id)) {


            it.remove();
          }


        }

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
      System.out.println("txn " + txn.getId());
      if (oper.isOperationWrite()) {
        if (writeOperation(txn, oper)) {
          iter.remove();
        }
      } else {
        if (readOperation(txn, oper)) {
          iter.remove();
        }
      }


    }

  }
  
  
  private static void clearWaitingWriteOperations() {
	    Iterator<Tuple<Transaction, Operation>> iter = waitingWriteOperations.iterator();

	    System.out.println("clear waiting operations");
	    while (iter.hasNext()) {
	      // get transaction id

	      // check if read or write operation
	      Tuple<Transaction, Operation> t = iter.next();
	      Operation oper = t.y;
	      Transaction txn = t.x;
	      System.out.println("txn " + txn.getId());
	        if (writeOperation(txn, oper)) 
	          iter.remove();
	     
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
    	  Variable v = s.getVariable(var_id);
     
          // if read or write locked return true
          if (v.isReadLocked() || v.isWriteLocked()) 
            return true;
     
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
          // if read or write locked return true

          if (v.isWriteLocked())
            return true;
      }

    }
    return toReturn;

  }

  // This function puts a lock on the variable being read by some transaction returns the variable
  // on one site
  private static List<Variable> readlockVariable(int var_id) {
    List<Variable> variablelist = new ArrayList<Variable>();
    List<Integer> s = variable_site_map.get(var_id);

    //If the variable is odd, simply get the lock
    if (var_id % 2 != 0) {
      if (sites.get(s.get(0)).getSiteStatus() == SiteStatus.UP) {
        Variable v = sites.get(s.get(0)).getVariable(var_id);
        v.readLockVariable();
        variablelist.add(v);
      }
    } else {
      for (Integer i : s) {
        // check if site is up
        if (sites.get(i).getSiteStatus() == SiteStatus.UP) {

          Variable v = sites.get(i).getVariable(var_id);
          if(!v.isJustRecovered()) {
        	  v.readLockVariable();
        	  variablelist.add(v);
          }

        }
      }
    }

    return variablelist;
  }

  // This function puts a lock on the variable being written by some transaction
  private static List<Variable> writelockVariable(int var_id) {
    List<Variable> variablelist = new ArrayList<Variable>();
    List<Integer> s = variable_site_map.get(var_id);
    for (Integer i : s) {
      // check if site is up
      if (sites.get(i).getSiteStatus() == SiteStatus.UP)
      {
        // lock variable
        Variable v = sites.get(i).getVariable(var_id);
        v.writeLockVariable();
        variablelist.add(v);

      }
    }
    return variablelist;
  }


  // This function will be called to add variables to the read only transaction map
  private static boolean addVariableToReadOnlyMap(int trans_id, int var_id) {


    if (transaction_variable_readOnly_map.containsKey(var_id))
      return false; // Some transaction has already lock on the variable

    transaction_variable_readOnly_map.put(var_id, trans_id);
    return true;

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

    Iterator<Map.Entry<Integer, Integer>> it = transaction_variable_map.entrySet().iterator();


    while (it.hasNext()) {
      Map.Entry<Integer, Integer> pair = (Map.Entry<Integer, Integer>) it.next();

      if (pair.getValue().equals(trans_id)) {

        List<Variable> variables = variable_copies_map.get(pair.getKey());
        for (Variable v : variables) {
          v.unlockVariable();
        }
        it.remove();
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
