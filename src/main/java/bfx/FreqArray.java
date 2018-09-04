package bfx;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;

import static java.util.Map.entry;

public class FreqArray {
    private static Map<Character,Integer> map = Map.ofEntries(
            entry('A',0),
            entry('C',1),
            entry('G',2),
            entry('T',3) );

    public static void main( String[] argv ) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(argv[0])));
        final String dna = in.readLine();
        final int k = Integer.parseInt( in.readLine() );

        int[] freqArray = new int[(int)Math.pow(4,k)];
        for( int i = 0 ; i < dna.length() - k +1 ; i++ )
            freqArray[patternToNumber(dna.substring(i,i+k),k)]++;
        Arrays.stream(freqArray).mapToObj(x->String.format("%d ",x)).forEach(System.out::print);
    }

    private static int patternToNumber(String p, int k) {
        int value = 0;
        for( int i = 0 ; i < p.length() ; i++ ) {
            value += (int)Math.pow(4,k-i-1)*map.get(p.charAt(i));
        }
        // System.out.println(p + " " + value);
        return value;
    }
}
