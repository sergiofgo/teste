import java.lang.*;
import java.util.Scanner;
import java.util.regex.*;
class teste {
  public static void main (String[] args) {
    Pattern p = Pattern.compile("\\d+");
    Matcher matcher = p.matcher("");
    while (matcher.find()) {
      System.out.println (matcher.group());
    }
  }
}