package edu.nyu.cs.distributedsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DeadlockHandler {
  private static Map<Integer, List<Integer>> transaction_dependency_graph =
      new HashMap<Integer, List<Integer>>();

  static void addDependencyEdge(int independent_trans, int dependent_trans) {
    if (transaction_dependency_graph.containsKey(independent_trans)) {
      List<Integer> value = transaction_dependency_graph.get(independent_trans);
      value.add(dependent_trans);
      transaction_dependency_graph.remove(independent_trans);
      transaction_dependency_graph.put(independent_trans, value);
    } else {
      List<Integer> value = new ArrayList<Integer>();
      value.add(dependent_trans);
      transaction_dependency_graph.put(independent_trans, value);
    }
  }


  static void removeDependencyEdge(int independent_trans, int dependent_trans) {
    if (transaction_dependency_graph.containsKey(independent_trans)) {
      List<Integer> value = transaction_dependency_graph.get(independent_trans);
      value.remove(dependent_trans);
      transaction_dependency_graph.remove(independent_trans);

      if (!value.isEmpty())
        transaction_dependency_graph.put(independent_trans, value);
    }
  }


  // Remove transaction from the map and return the list of oldest transaction_ids // waiting for
  // that transaction to // commit/ abort so that it can continue List<Integer>
  static boolean removeTransactionfromMap(int trans_id) {
    /*
     * if (transaction_dependency_graph.containsKey(trans_id))
     * transaction_dependency_graph.remove(trans_id);
     * 
     * for (Integer t : transaction_variable_map.keySet()) { if
     * (transaction_variable_map.get(t).contains(trans_id)) { List<Integer> value =
     * transaction_variable_map.get(t); value.remove(trans_id); if (value.isEmpty())
     * transaction_variable_map.remove(t); } } return freedTransactions;
     * 
     */
    return true;
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
    if (transaction_dependency_graph.containsKey(T2)) {
      List<Integer> value = transaction_dependency_graph.get(T2);
      if (value.contains(T1))
        return true;
    }
    return false;
  }

  static List<Integer> getDependentTransactionList(int trans_id) {
    return transaction_dependency_graph.get(trans_id);
  }
}
