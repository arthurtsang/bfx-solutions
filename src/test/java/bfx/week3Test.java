package bfx;

import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static bfx.week3.*;

public class week3Test {

    @Test
    public void testMotifEnumeration() throws Exception {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_156_8.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String parameter = in.readLine();
        String[] kd = parameter.split(" ");
        int k = Integer.valueOf(kd[0]);
        int d = Integer.valueOf(kd[1]);
        Stream<String> dna = in.lines();
        motifEnumeration(dna,k,d).map(s->s+" ").forEach(System.out::print);
    }

    @Test
    public void testMedianString() throws Exception{
        //InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_158_9.txt");
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("input.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        int k = Integer.valueOf(in.readLine());
        Stream<String> dna = in.lines();
        System.out.println(medianString(dna,k));
    }

    @Test
    public void testProfileMostProbableKmer() throws Exception {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_159_3.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String dna = in.readLine();
        int k = Integer.valueOf(in.readLine());
        double[][] profile = new double[4][k];
        for( int i = 0 ; i < 4 ; i++ ) {
            String[] p = in.readLine().split(" ");
            for( int j = 0 ; j < k ; j++ ) {
                profile[i][j] = Double.valueOf(p[j]);
            }
        }
        System.out.println(profileMostProbableKmer(dna,k,profile));
    }

    @Test
    public void testGreedyMotifSearchWithLaplace() throws Exception {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_160_9.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String[] parameters = in.readLine().split(" ");
        int k = Integer.valueOf(parameters[0]);
        int t = Integer.valueOf(parameters[1]);
        List<String> dna = new ArrayList<>();
        for( int i = 0 ; i < t ; i++ ) {
            dna.add( in.readLine() );
        }
        List<String> motifs = greedyMotifSearchWithLaplace(dna, k, t);
        motifs.forEach(System.out::println);
    }

    @Test
    public void testGreedyMotifSearch() throws Exception {
        //InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_159_5.txt");
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("input.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String[] parameters = in.readLine().split(" ");
        int k = Integer.valueOf(parameters[0]);
        int t = Integer.valueOf(parameters[1]);
        List<String> dna = new ArrayList<>();
        for( int i = 0 ; i < t ; i++ ) {
            dna.add( in.readLine() );
        }
        List<String> motifs = greedyMotifSearch(dna, k, t);
        motifs.forEach(System.out::println);
    }

    @Test
    public void testConsensusString() {
        List<String> motifs = Arrays.asList("TCGGGGGTTTTT", "CCGGTGACTTAC", "ACGGGGATTTTC", "TTGGGGACTTTT",
                "AAGGGGACTTCC", "TTGGGGACTTCC", "TCGGGGATTCAT", "TCGGGGATTCCT",
                "TAGGGGAACTAC", "TCGGGTATAACC");
        int[][] counts = countMotifs(motifs);
        double[][] profile = count2Profile(counts);
        String consensus = profile2Consensus(profile);
        assertEquals(consensus, "TCGGGGATTTCC");
        int score = consense2Score(consensus, motifs);
        assertEquals(score, 30);
        double[] entropies = profile2Entropy(profile);
        double sum = Arrays.stream(entropies).sum();
        assertEquals(entropies[4],0.467, 0.01 );
        Double prob = profileProbability("TCGTGGATTTCC", profile);
        assertEquals(prob, 0, 0);
    }
}