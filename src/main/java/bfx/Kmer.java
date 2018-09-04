package bfx;

import java.util.*;
import java.io.*;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.util.Comparator.*;
import static java.util.Map.entry;

public class Kmer {


    public static void main(String[] argv) throws Exception {

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(argv[0])));
        in.mark(1024 * 1024 * 1024);
        in.readLine();
        int minSkewPosition = skew(in);
        in.reset();
        int start = minSkewPosition - 500;
        int end = minSkewPosition + 500;
        in.readLine();
        String dna;
        StringBuilder window = new StringBuilder();
        int position = 0;
        boolean inWindow = false;
        while ((dna = in.readLine()) != null) {
            position += dna.length();
            if (position > start && !inWindow) {
                window.append(dna.substring(dna.length() - position + start));
                inWindow = true;
                continue;
            }
            if (position > end) {
                window.append(dna, 0, dna.length() - position + end);
                break;
            }
            if (inWindow)
                window.append(dna);
        }
        String L = window.toString();
        System.out.println(L.length());
        maxKmer(L, 9, 1).map(s -> s + " ").forEach(System.out::print);
    }

    static int skew(BufferedReader in) throws Exception {
        final Map<Character, Integer> skewMap = Map.ofEntries(entry('G', 1), entry('C', -1), entry('A', 0), entry('T', 0));
        final List<Integer> skews = new ArrayList<>();
        final int[] running = new int[]{0};
        final int[] min = new int[]{1000};
        String[] dna = new String[1];
        final int[] j = new int[]{0};
        while ((dna[0] = in.readLine()) != null) {
            IntStream.range(0, dna[0].length()).forEach(i -> {
                running[0] += skewMap.get(dna[0].charAt(i));
                if (running[0] < min[0]) {
                    skews.clear();
                    skews.add(j[0] * dna[0].length() + i + 1);
                    min[0] = running[0];
                } else if (running[0] == min[0]) {
                    skews.add(j[0] * dna[0].length() + i + 1);
                }
            });
            j[0]++;
        }
        skews.stream().min(Integer::min);

        return skews.get(0);
    }

    public static Stream<String> kmer( String dna, int k, int maxDistance ) {
        return IntStream.range(0, dna.length() -k + 1 )
                .mapToObj(i -> dna.substring(i, i+k ))
                .flatMap( p -> getVariation(p, maxDistance ))
                .distinct();
    }

    public static Stream<String> maxKmer(String dna, int k, int maxDistance ) {
        final Map<String, Integer> map = new HashMap<>();
        IntStream.range(0, dna.length() - k + 1)
                .mapToObj(i -> dna.substring(i, i + k))
                .forEach(p -> {
                    Stream.concat(getVariation(p, maxDistance), getVariation(reverse(p), maxDistance))
                    .forEach(
                            v -> {
                                int c = map.getOrDefault(v, 0) + 1;
                                map.put(v, c);
                            });
                });
        int maxCount = map.values().stream().max(naturalOrder()).get();
        System.out.println("count = " + maxCount);
        return map.entrySet().stream().filter(e -> e.getValue() == maxCount).map(Map.Entry::getKey);
    }

    public static Stream<String> getVariation(String pattern, int maxDistance) {
        if (maxDistance == 0) return Stream.of(pattern);
        return LongStream.range(0, (long) Math.pow(2, pattern.length()))
                .mapToObj(Long::toBinaryString)
                .map(s -> String.format("%" + pattern.length() + "s", s).replace(' ', '0'))
                .map(s -> entry(s, s.chars().map(c -> (char) c - '0').reduce((x, y) -> x + y).getAsInt()))
                .filter(e -> (e.getValue() > 0 && e.getValue() <= maxDistance))
                .flatMap(e -> IntStream.range(0, (int) Math.pow(4, e.getValue()))
                        .mapToObj(i -> {
                            String sfmt = "%" + e.getValue() + "s";
                            String base4 = Integer.toString(i, 4);
                            return String.format(sfmt, base4).replaceAll(" ", "0");
                        })
                        .map(s -> s.replace("0", "A").replace("1", "C").replace("2", "G").replace("3", "T"))
                        .map(s -> {
                            Stack<Character> stack = new Stack<>();
                            IntStream.range(0, s.length()).forEach(i -> stack.push(s.charAt(i)));
                            return stack;
                        })
                        .map(s -> {
                            StringBuilder sb = new StringBuilder();
                            IntStream.range(0, e.getKey().length()).mapToObj(i -> (e.getKey().charAt(i) == '0') ? pattern.charAt(i) : s.pop()).forEach(c -> sb.append(c));
                            return sb.toString();
                        })
                ).distinct();
    }

    static String reverse(String pattern) {
        StringBuilder sb = new StringBuilder();
        for (int i = pattern.length() - 1; i >= 0; i--) {
            switch (pattern.charAt(i)) {
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
        return sb.toString();
    }
}
