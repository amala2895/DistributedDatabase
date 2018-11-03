package edu.nyu.cs.distributedsystem;

class Variable {

  private int index;
  private int val;
  private boolean writelock;

  Variable(int i, int v) {
    index = i;
    val = v;
    writelock = false;
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

  boolean isLocked() {
    return writelock;
  }

  void lockVariable() {
    writelock = true;
  }

  void unlockVariable() {
    writelock = false;
  }
}
