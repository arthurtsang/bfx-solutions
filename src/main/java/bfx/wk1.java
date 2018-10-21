package bfx;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class wk1 {
    public static void main( String[] argv ) {
        System.out.println( PatternCount("GACCATCAAAACTGATAAACTACTTAAAAATCAGT", "AAA"));
        //FrequentWords("ACGTTGCATGTCGCATGATGCATGAGAGCT", 4);
    }

    public static ArrayList<String> FrequentWords(String text, int k) {
        Map<String, Long> map = IntStream.rangeClosed(0, text.length() - k).mapToObj(i -> text.substring(i, i + k)).collect( Collectors.groupingBy(Function.identity(), Collectors.counting()) );
        Long maxCount = map.values().stream().max(Long::compareTo).get();
        return map.entrySet().stream().filter(e->e.getValue().equals(maxCount)).map(e->e.getKey()+" ").collect(Collectors.toCollection(ArrayList::new));
    }

    public static int PatternCount(String text, String pattern) {
        int count = 0;
        int position = 0;
        while( (position = text.indexOf(pattern,position)) != -1 ) {
            count++;
            position++;
        }
        return count;
    }
}
