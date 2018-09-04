import java.util.*;
import java.util.regex.*;
import java.io.*;

public class Find {

  public static void main( String[] argv ) throws Exception{
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String dna = in.readLine();
    System.out.println( "DNA string" + dna );
    String pattern = in.readLine();
    String regex = String.format( "(%s)(?=%s)", pattern.charAt(0), pattern.substring(1) );
    System.out.println( "Searching for " + regex );
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(dna);
    int count = 0;
    while( m.find() ) count ++;
    System.out.println( count );
  }
}
