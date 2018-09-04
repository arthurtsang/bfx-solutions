import java.util.*;
import java.util.regex.*;
import java.io.*;

public class Reverse {

  public static void main( String[] argv ) throws Exception{
    //BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    //String dna = in.readLine();
    String dna = "GATTACA";

    StringBuilder sb = new StringBuilder();
    for( int i = dna.length()-1 ; i >= 0 ; i-- ) {
      switch( dna.charAt(i) ) {
        case 'A':
          sb.append('T');
          break;
        case 'T':
          sb.append('A');
          break;
        case 'G':
          sb.append('C');
          break;
        case 'C':
          sb.append('G');
      }
    }
    System.out.println( sb );
  }
}
