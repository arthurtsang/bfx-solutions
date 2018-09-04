import java.util.*;
import java.util.regex.*;
import java.io.*;

public class Position {

  public static void main( String[] argv ) throws Exception{
    //BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    //String maxKmer = in.readLine();
    //String dna = in.readLine();
    String dna = "AAACATAGGATCAAC";
    String kmer = "AA";
//StringBuilder sb = new StringBuilder();
//    for( int i = maxKmer.length()-1 ; i >= 0 ; i-- ) {
//      switch( maxKmer.charAt(i) ) {
//        case 'A':
//          sb.append('T');
//          break;
//        case 'T':
//          sb.append('A');
//          break;
//        case 'G':
//          sb.append('C');
//          break;
//        case 'C':
//          sb.append('G');
//      }
    //}
    //String complement = sb.toString();

    // Pattern pattern = Pattern.compile( String.format( "((%s)(?=%s)|(%s)(?=%s))", maxKmer.charAt(0), maxKmer.substring(1), complement.charAt(0), complement.substring(1) ) );
    Pattern pattern = Pattern.compile( String.format( "(%s)(?=%s)", kmer.charAt(0), kmer.substring(1) ));
    Matcher matcher = pattern.matcher(dna);
    while( matcher.find() ) {
      System.out.print( matcher.start() + " " );
    }
    System.out.println( "" );
  }
}
