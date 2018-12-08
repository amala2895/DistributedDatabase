package edu.nyu;

import java.io.FileNotFoundException;
import java.util.Scanner;
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
    Scanner sc = new Scanner(System.in);

    System.out.println(
        "Enter 1 if you want to give filename \nEnter 2 if you want to give standard input");
    String i = sc.nextLine();

    if (i.equals("1")) {

      String filepath = sc.nextLine();

      Parser.initfilename(filepath);
      try {
        Parser.readFile();
      } catch (FileNotFoundException e) {
        System.out.println("Unable to open file");
      }

    } else if (i.equals("2")) {

      Parser.initfilename("");
      System.out.println("Start Entering commands");
      while (sc.hasNext()) {
        String str = sc.next();

        Parser.parse(str);
        if (str.equalsIgnoreCase("stop"))
          break;

      }

    } else {
      System.out.println("Wrong input");
    }
    sc.close();
  }


}
