package edu.nyu.cs.distributedsystem;

/**
 * This class holds the variable, its value and whether it is locked or not.
 * 
 * @author Amala Deshpande and Anshu Tomar
 *
 */
class Variable {

  private int index;
  private int val;
  private int siteindex;
  private boolean writelock;
  private boolean readLock;
  private boolean justRecovered;

  Variable(int i, int v, int s) {
    index = i;
    val = v;
    siteindex = s;
    writelock = false;
    readLock = false;
    justRecovered = false;

  }

  int getIndex() {
    return index;
  }

  int getSiteIndex() {
    return siteindex;
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
