package edu.nyu;

import java.io.FileNotFoundException;
import edu.nyu.cs.Parser;

public class Main {
  public static void main(String[] args) {

    String filepath = "src/input1.txt";
    Parser.initfilename(filepath);
    try {
      Parser.readFile();
    } catch (FileNotFoundException e) {
      System.out.println("wrong file");
    }
  }

}
