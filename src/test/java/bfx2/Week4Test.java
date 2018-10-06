package bfx2;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static bfx2.Week4.*;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class Week4Test {

    @Test
    public void testPeptideTripleTreeSet() {
        TreeSet<PeptideTriple> treeSet = new TreeSet<>();
        PeptideTriple p1 = new PeptideTriple(Arrays.asList(10,20,30), 20, 30);
        PeptideTriple p2 = new PeptideTriple(Arrays.asList(11,21,31), 20, 30);
        System.out.println( p1.hashCode() );
        System.out.println( p2.hashCode() );
        assert( p1.hashCode() != p2.hashCode() );
        assertFalse( p1.equals(p2) );
        assertTrue( treeSet.add(p1) );
        assertTrue( treeSet.add(p2) );
    }
    @Test
    public void testFindLinearTheoreticalSpectrum() {
        List<Integer> peptide = Arrays.asList(1,2,3);
        List<Integer> spectrum = findLinearTheoreticalSpectrum(peptide);
        assertEquals( Arrays.asList(0,1,2,3,3,5,6), spectrum);
    }
    @Test
    public void testLinearScore() {
        String peptide = "PEEP";
        List<Integer> spectrum = Arrays.asList(0,97,97,129,194,196,226,226,244,258,323,323,452);
        List<Integer> peptideMass = peptideMass(peptide);
        List<Integer> theoreticalSpectrum = findLinearTheoreticalSpectrum(peptideMass);
        System.out.println( cyclopeptideScore(theoreticalSpectrum,spectrum) );
    }
    @Test
    public void testCyclopetideScore() throws IOException {
        /*
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_102_3.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String peptide = in.readLine();
        List<Integer> spectrum = Arrays.stream(in.readLine().split(" ")).map(Integer::valueOf).collect(Collectors.toList());
        */
        String peptide = "MAMA";
        List<Integer> spectrum = Arrays.asList(0,71,98,99,131,202,202,202,202,202,299,333,333,333,503);
        int score = Week4.cyclopeptideScore(peptide, spectrum);
        System.out.println( "score is " + score );
    }
    @Test
    public void testLeaderboardCyclopetideSequencing() throws IOException {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_102_8.txt");
        //InputStream input = this.getClass().getClassLoader().getResourceAsStream("input.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        int N = Integer.valueOf(in.readLine());
        //N = 20;
        List<Integer> spectrum = Arrays.stream(in.readLine().split(" ")).map(Integer::valueOf).collect(Collectors.toList());
        List<Integer> leaderPeptide = leaderbaordCyclopeptideSequencing(N, spectrum);
        System.out.println(leaderPeptide.stream().map(i->i+"").reduce((x,y)->x+"-"+y).get());
    }
    @Test
    public void testSpectralConvolution() throws IOException {
        List<Integer> result = spectralConvolution(Arrays.asList(0, 137, 186, 323));
        /*
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_104_4.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        List<Integer> spectrum = Arrays.stream(in.readLine().split(" ")).map(s->Integer.valueOf(s)).collect(Collectors.toList());
        List<Integer> result = spectralConvolution(spectrum);
        */
        System.out.println(result.stream().map(i->i+"").reduce((x,y)->x+" " +y).get());
    }

    @Test
    public void testTopSpectralConvolution() {
        List<Integer> spectrum = Arrays.asList(0, 137, 186, 323);
        assertEquals( Arrays.asList(137,186), topSpectralConvolution(spectrum, 2) );
        assertEquals( Arrays.asList(137,186), topSpectralConvolution(spectrum, 1) );
        spectrum = Arrays.asList(0,86,160,234,308,320,382);
        topSpectralConvolution(spectrum,1);
    }
    @Test
    public void testConvolutionCyclopetideSequencing() throws IOException {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_104_7.txt");
        //InputStream input = this.getClass().getClassLoader().getResourceAsStream("input.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        int M = Integer.valueOf(in.readLine());
        int N = Integer.valueOf(in.readLine());
        List<Integer> spectrum = Arrays.stream(in.readLine().split(" ")).map(Integer::valueOf).collect(Collectors.toList());
        List<Integer> leaderPeptide = convolutionCyclopeptideSequencing(N, M, spectrum);
        System.out.println(leaderPeptide.stream().map(i->i+"").reduce((x,y)->x+"-"+y).get());
    }

}
