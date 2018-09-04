import java.util.*;
import java.io.*;

import static java.util.stream.Collectors.*;

public class Clump {

    public static void main(String[] argv) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(argv[0])));
        final String dna = in.readLine();
        final String[] params = in.readLine().split("\\s+");
        final int k = Integer.parseInt(params[0]);
        final int L = Integer.parseInt(params[1]);
        final int t = Integer.parseInt(params[2]);

        System.out.println(String.format("Finding %s-mer (%s,%s)", k, L, t));

        HashMap<String, List<Integer>> map = new HashMap<>();
        for (int i = 0; i <= dna.length() - k; i++) {
            String pattern = dna.substring(i, i + k);
            List<Integer> positions = (map.containsKey(pattern)) ? map.get(pattern) : new ArrayList<>();
            positions.add(i);
            map.put(pattern, positions);
        }

        List<String> kmers = map.entrySet().stream().filter(entry -> entry.getValue().size() >= t).map(Map.Entry::getKey).collect(toList());
        for (int i = 0; i < kmers.size(); i++) {
            //System.out.println( "\nChecking " + kmers.get(i) + " with " + map.get(kmers.get(i)).size() + " hits" );
            List<Integer> positions = map.get(kmers.get(i));
            for (int j = 0; j <= positions.size() - t; j++) {
                if (positions.get(j + t - 1) + k - positions.get(j) <= L) {
                    System.out.println(kmers.get(i) + " ");
                    break;
                }
            }
        }
    }
}
