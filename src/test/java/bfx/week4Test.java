package bfx;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import static bfx.week4.*;

import static org.junit.Assert.*;

public class week4Test {

    @Test
    public void testRandomizedMotifSearch() throws Exception {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_161_5.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String[] parameter = in.readLine().split(" ");
        int k = Integer.valueOf(parameter[0]);
        int t = Integer.valueOf(parameter[1]);
        List<String> dna = in.lines().collect(Collectors.toList());
        List<String> motifs = randomizedMotifSearch(k, t, dna, 1000);
        motifs.forEach(System.out::println);
    }
}