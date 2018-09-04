import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.Map.entry;

public class Skew {
    public static void main(String[] argv) throws Exception {
        //String dna = "TAAAGACTGCCGAGAGGCCAACACGAGTGCTAGAACGAGGGGCGTAAACGCGGGTCCGAT";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(argv[0])));
        final String dna = in.readLine();
        Map<Character, Integer> skewMap = Map.ofEntries(entry('G', 1), entry('C', -1), entry('A', 0), entry('T', 0));
        final List<Integer> skews = new ArrayList<>();
        final int[] running = new int[]{0};
        final int[] min = new int[]{1000};
        IntStream.range(0,dna.length()).forEach(i->{
            running[0] += skewMap.get((char)dna.charAt(i));
            if( running[0] < min[0] ) {
                skews.clear();
                skews.add(i);
                min[0] = running[0];
            } else if( running[0] == min[0] ) {
                skews.add(i);
            }
        });
        skews.stream().map(i->" " + (i+1)).forEach(System.out::print);

        dna.chars().mapToObj(n -> skewMap.get((char) n)).forEach(n -> {
            running[0] += n;
            skews.add(running[0]);
            System.out.print( running[0] + " " );
        });

    }
}
