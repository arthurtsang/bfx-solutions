package bfx;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Map.entry;

public class Hamming {
    public static void main( String[] argv ) throws Exception{
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(argv[0])));
        String pattern = in.readLine();
        String dna = in.readLine();
        int maxDistance = Integer.valueOf(in.readLine());
        int k = pattern.length();
        Stream<String> positions = IntStream.range(0, dna.length() - k)
                .mapToObj(i -> entry(i, dna.substring(i, i + k)))
                .map(e -> entry(e.getKey(), hamming(pattern, e.getValue())))
                .filter(e -> e.getValue() <= maxDistance)
                .map(e -> e.getKey() + " ");
        System.out.println( positions.count() );
    }

    public static int hamming(String a, String b) {
        return IntStream.range(0, a.length()).map(i -> (a.charAt(i) == b.charAt(i)) ? 0 : 1).reduce((x, y) -> x + y).getAsInt();
    }
}
