package edu.nyu.cs.distributedsystem;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DeadlockHandler {
static Map<Integer, List<Integer>> transaction_dependency_graph = new HashMap<Integer, List<Integer>>();

void addDependencyEdge(int independent_trans, int  dependent_trans){
  if(transaction_dependency_graph.containsKey(independent_trans)){
    List<Integer> value = transaction_dependency_graph.get(independent_trans);
    value.add(dependent_trans);
    transaction_dependency_graph.remove(independent_trans);
    transaction_dependency_graph.put(independent_trans,value);
  }
  else{
    List<Integer> value = new List<Integer();
    value.add(dependent_trans);
    transaction_dependency_graph.put(independent_trans,value);
  }
}


void removeDependencyEdge(int independent_trans, int  dependent_trans){
  if(transaction_dependency_graph.containsKey(independent_trans))
  {
    List<Integer> value= transaction_dependency_graph.get(independent_trans);
    value.remove(dependent_trans);
    transaction_dependency_graph.remove(independent_trans);

    if(!value.isEmpty())
      transaction_dependency_graph.put(independent_trans, value);
  }
}

// Remove transaction from the map and return the list of oldest transaction_ids
//waiting for that transaction to
//commit/ abort so that it can continue
List<Integer> removeTransactionfromMap(int trans_id)
{
  if(transaction_dependency_graph.contains(trans_id))
     transaction_dependency_graph.remove(trans_id);

  for (int  t: transaction_variable_map.keySet())
  {
    if (transaction_variable_map.get(t).contains(trans_id)) {
          List<Integer> value = transaction_variable_map.get(t);
          value.remove(trans_id);
          if(value.isEmpty())
             transaction_variable_map.remove(t);
        }
  }
  return freedTransactions;
}


List<Integer> getFreedTransactions(int trans_id)
{
  List<Integer> freedTransactions = new List<Integer>();

  for (int  t: transaction_variable_map.keySet())
  {
    if (transaction_variable_map.get(t).contains(trans_id)) {
          List<Integer> value = transaction_variable_map.get(t);
          value.remove(trans_id);
          transaction_variable_map.remove(t);

          if(value.isEmpty())
             freedTransactions.add(t);
          else
             transaction_variable_map.put(t,value);
        }
  }
  return freedTransactions;
}

bool ifThereIsAnEdgeFromT1toT2(int T1, int T2){
  if(transaction_dependency_graph.containsKey(T2)){
    List<Integer> value = transaction_dependency_graph.get(T2);
    if(value.contains(T1))
      return true;
  }
  return false;
}

List<Integer> getDependentTransactionList(int trans_id){
  return transaction_dependency_graph.get(trans_id);
}
