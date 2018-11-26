package edu.nyu.cs.distributedsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DeadlockHandler {
  // key is dependent
  private static Map<Integer, List<Integer>> transaction_dependency_graph =
      new HashMap<Integer, List<Integer>>();


  static Map<Integer, List<Integer>> getDependencyGraph() {
    return transaction_dependency_graph;
  }

  static void addDependencyEdge(int independent_trans, int dependent_trans) {

    if (transaction_dependency_graph.containsKey(dependent_trans)) {
      List<Integer> value = transaction_dependency_graph.get(dependent_trans);
      value.add(independent_trans);
      transaction_dependency_graph.remove(dependent_trans);
      transaction_dependency_graph.put(dependent_trans, value);
    } else {
      List<Integer> value = new ArrayList<Integer>();
      value.add(independent_trans);
      transaction_dependency_graph.put(dependent_trans, value);
    }
  }


  static void removeDependencyEdge(int dependent_trans) {
    System.out.println("Removing dependency edge " + dependent_trans);
    // Remove the edge which contains dependent_trans as key
    if (transaction_dependency_graph.containsKey(dependent_trans))
      transaction_dependency_graph.remove(dependent_trans);


    // Remove the edge which contains dependent_trans as value

    for (Integer key : transaction_dependency_graph.keySet()) {
      List<Integer> value = transaction_dependency_graph.get(key);

      if (value.isEmpty()) {


        continue;
      }
      Iterator<Integer> itr = value.iterator();
      while (itr.hasNext()) {
        int val = itr.next();
        if (val == dependent_trans) {

          itr.remove();
        }
        if (value.isEmpty()) {

          transaction_dependency_graph.remove(key);
        } else {
          transaction_dependency_graph.put(key, value);
        }
      }

    }
  }



  static List<Integer> getFreedTransactions(int trans_id) {
    List<Integer> freedTransactions = new ArrayList<Integer>();
    /*
     * for (int t : transaction_variable_map.keySet()) { if
     * (transaction_variable_map.get(t).contains(trans_id)) { List<Integer> value =
     * transaction_variable_map.get(t); value.remove(trans_id); transaction_variable_map.remove(t);
     * 
     * if (value.isEmpty()) freedTransactions.add(t); else transaction_variable_map.put(t, value); }
     * }
     */
    return freedTransactions;
  }


  static boolean ifThereIsAnEdgeFromT1toT2(int T1, int T2) {
    if (transaction_dependency_graph.containsKey(T1)) {
      List<Integer> value = transaction_dependency_graph.get(T1);
      if (value.contains(T2))
        return true;
    }
    return false;
  }

  static boolean isThereACycleInGraph(int T1, int T2) {


    if (transaction_dependency_graph.containsKey(T2)) {
      List<Integer> t = transaction_dependency_graph.get(T2);

      for (Integer e : t) {
        if (e == T1)
          return true;

        return isThereACycleInGraph(T1, e);
      }
    }
    return false;

  }



  static List<Integer> getDependentTransactionList(int trans_id) {
    return transaction_dependency_graph.get(trans_id);
  }
}
