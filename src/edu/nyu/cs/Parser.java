package edu.nyu.cs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import edu.nyu.cs.distributedsystem.TransactionManager;

/*
 * This class parses the input file line by line and calls appropriate functions of the Transaction
 * manager
 */
public class Parser {

  private static String filepath;

  // Suppress default constructor for noninstantiability
  private Parser() {
    throw new AssertionError();
  }

  /**
   * This method initializes all sites by bringing them up and initializes all variables
   * 
   * @param filename
   */
  public static void initfilename(String filename) {
    filepath = filename;
    TransactionManager.initializeSites();
    TransactionManager.initializeVariables();

  }


  /**
   * This method decides what the given line is and calls the appropriate Transaction manager
   * function.
   * 
   * @param line
   */
  private static void parse(String line) {
    line = line.replaceAll("\\s+", "");

    if (line.startsWith("begin")) {
      int tid = Integer.parseInt(line.substring(line.indexOf("T") + 1, line.indexOf(")")));
      String type = "RW";
      if (line.contains("RO"))
        type = "RO";
      System.out.println("begin - " + tid + " " + type);
      TransactionManager.beginTransaction(tid, type);

    } else if (line.startsWith("end")) {
      int tid = Integer.parseInt(line.substring(line.indexOf("T") + 1, line.indexOf(")")));
      System.out.println("end -" + tid);
      TransactionManager.endTransaction(tid);

    } else if (line.startsWith("dump")) {
      System.out.println("dump");
      if (line.equalsIgnoreCase("dump()")) {
        TransactionManager.dump();
      } else if (line.startsWith("dump(x")) {
        int vid = Integer.parseInt(line.substring(line.indexOf("x") + 1, line.indexOf(")")));
        TransactionManager.dumpVariable(vid);
      } else {
        int sid = Integer.parseInt(line.substring(line.indexOf("(") + 1, line.indexOf(")")));
        TransactionManager.dumpSite(sid);
      }

    } else if (line.contains("R(")) {
      int tid = Integer.parseInt(line.substring(line.indexOf("T") + 1, line.indexOf(",")));
      int vid = Integer.parseInt(line.substring(line.indexOf("x") + 1, line.indexOf(")")));
      System.out.println("Read -" + tid + " " + vid);
      TransactionManager.makeReadOperation(tid, vid);

    } else if (line.contains("W(")) {
      int tid = Integer.parseInt(line.substring(line.indexOf("T") + 1, line.indexOf(",")));
      int vid = Integer.parseInt(line.substring(line.indexOf("x") + 1, line.lastIndexOf(",")));
      int value = Integer.parseInt(line.substring(line.lastIndexOf(",") + 1, line.indexOf(")")));
      System.out.println("Write -" + tid + " " + vid + " " + value);
      TransactionManager.makeWriteOperation(tid, vid, value);

    } else if (line.contains("fail")) {
      int sid = Integer.parseInt(line.substring(line.indexOf("(") + 1, line.indexOf(")")));
      System.out.println("fail" + sid);
      TransactionManager.failSite(sid);

    } else if (line.contains("recover")) {
      int sid = Integer.parseInt(line.substring(line.indexOf("(") + 1, line.indexOf(")")));
      System.out.println("recover" + sid);
      TransactionManager.recoverSite(sid);
    }
  }

  /**
   * This methods reads the file line by line and calls the parse method
   * 
   *
   * @throws FileNotFoundException
   */
  public static void readFile() throws FileNotFoundException {
    File file = new File(filepath);
    Scanner sc = new Scanner(file);
    while (sc.hasNextLine()) {
      parse(sc.nextLine());
    }
    sc.close();
  }


}
