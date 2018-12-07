package edu.nyu;

import java.io.FileNotFoundException;
import edu.nyu.cs.Parser;

/**
 * This is the main driver class. It contains the main method and takes file path has argument in
 * the format src/inputfilename.txt
 * 
 * @author Amala Deshpande and Anshu Tomar
 *
 */
public class Main {
  public static void main(String[] args) {

    String filepath = args[0];
    Parser.initfilename(filepath);
    try {
      Parser.readFile();
    } catch (FileNotFoundException e) {
      System.out.println("Unable to open file");
    }
  }

}
