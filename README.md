# Distributed Database
This project aims to understand the functionality and the complexities involved in handling  multiple transactions concurrently by distributed database systems. To achieve this, we have simulated similar environment which behaves like a distributed database system  implementing  multi-version concurrency control, deadlock detection, replication and failure recovery. 

To simulate a distributed database system, we have considered 10 sites which hold 20 variables with the conditions specified in project specifications.We have implemented available copies algorithm with two phase strict locking for handling the transactions. The cases handled by the algorithm are as follows:
1. The failed site does not participate in the transactions.
2. If a site recovers after failure, it will not let a replicated variable being read from it unless a write happens on the variable.
3. The unreplicated variable will be allowed to be read/written after the site comes up.
4. If any two transactions want to access the same variable in a manner that one of them has to wait for the other, this case will be handled by adding dependency edge in a graph which will be used  for cycle detection too if there occurs any cycle.
5. All the copies of the variable  will be updated once a transaction writing to that variable commits successfully.
6. Read Only Transaction have a snapshot of the system when they begin. Hence not affected by site failures and writes by other Transaction. 

Run the Main.java file and give input file path in the arguments.
