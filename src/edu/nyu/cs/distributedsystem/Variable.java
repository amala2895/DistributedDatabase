package edu.nyu.cs.distributedsystem;

class Variable {

  private int index;
  private int val;
  private boolean writelock;
  private boolean readLock;
  private boolean justRecovered;

  Variable(int i, int v) {
    index = i;
    val = v;
    writelock = false;
    readLock = false;
    justRecovered = false;

  }

  int getIndex() {
    return index;
  }

  int getVal() {
    return val;
  }

  void setVal(int v) {
    val = v;
  }

  boolean isWriteLocked() {
    return writelock;
  }

  boolean isReadLocked() {
    return readLock;
  }


  void writeLockVariable() {
    writelock = true;
  }

  void readLockVariable() {
    readLock = true;
  }

  void unlockVariable() {
    writelock = false;
    readLock = false;
  }

  boolean isJustRecovered() {
    return justRecovered;
  }

void setJustRecovered(boolean status) {
	justRecovered = status;
 }

}
