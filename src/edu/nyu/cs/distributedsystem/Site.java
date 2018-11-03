
package edu.nyu.cs.distributedsystem;

class Site {
	private:
		enum siteStatus 
	    { 
	        DOWN,RECOVERING, UP; 
	    }

		Map<int,Variable> varMap; 
		
		bool processTransaction(int transactionType, Variable var);
		

}
