package bfx;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import static bfx.week4.*;
import static bfx.week3.*;

public class week4Test {

    @Test
    public void testRandomizedMotifSearch() throws Exception {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("input.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String[] parameter = in.readLine().split(" ");
        int k = Integer.valueOf(parameter[0]);
        int t = Integer.valueOf(parameter[1]);
        List<String> dna = in.lines().collect(Collectors.toList());
        List<String> motifs = randomizedMotifSearch(k, t, dna, 1000);
        motifs.forEach(System.out::println);
    }

    @Test
    public void testGibbsSampler() throws Exception {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("DosR.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String[] parameter = in.readLine().split(" ");
        int k = Integer.valueOf(parameter[0]);
        int t = Integer.valueOf(parameter[1]);
        int iteration = Integer.valueOf(parameter[2]);
        List<String> dna = in.lines().collect(Collectors.toList());
        List<String> motifs = gibbsSampler(k, t, dna, iteration, 1000);
        //motifs.forEach(System.out::println);
        String consensus = profile2Consensus(motifs2Profile(motifs));
        System.out.println( consensus );
    }

    @Test
    public void testWk5() throws Exception {
        List<String> motifs = new BufferedReader( new InputStreamReader( this.getClass().getClassLoader().getResourceAsStream("input.txt"))).lines().collect(Collectors.toList());
        System.out.println( profile2Consensus(motifs2Profile(motifs)) );

    }
}