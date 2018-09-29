package bfx2;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.entry;
import static org.junit.Assert.*;
import static bfx2.Week3.*;

public class Week3Test {

    @Test
    public void testProteinTranslation() throws IOException {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_96_4.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String rna = in.readLine();
        System.out.println(proteinTranslation(rna));
    }

    @Test
    public void findRNA() {
        Stream<String> rna = Stream.of("CCAAGAACAGAUAUCAAU", "CCUCGUACAGAAAUCAAC", "CCAAGUACAGAGAUUAAC", "CCGAGGACCGAAAUCAAC");
        rna.map(r->entry(r,proteinTranslation(r))).filter(e->e.getValue().equals("PRTEIN")).map(e->e.getKey()).forEach(System.out::println);

    }
    @Test
    public void testEncodeAminoAcid() {
        String dna = "GAAACT";
        String compliment = reverseComplement(dna);
        String aminoAcid = proteinTranslation(rnaTranscribe(dna));
        String aminoAcid2 = proteinTranslation(rnaTranscribe(compliment));
        assertEquals("ET", aminoAcid);
        assertEquals( "SF", aminoAcid2);
    }
    @Test
    public void testFindPeptideFromAminoAcid() throws IOException {
        //InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_96_7.txt");
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("Bacillus_brevis.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String rna = in.lines().map(s->s.trim()).reduce((x,y)->x+y).get();
        //String aminoAcid = in.readLine();
        String aminoAcid = "VKLFPWFNQY";
        findPeptideForAminoAcid(rna,aminoAcid)
                .forEach(System.out::println);
    }

    @Test
    public void testFindTheoreticalSpectrum() {
        String text = "YNIHLHQRRANCLNL";
        Stream<Integer> spectrum = findTheoreticalSpectrum(text);
        spectrum.map(i->i+" ").forEach(System.out::print);
        //Stream<Integer> expected = Stream.of(0,113,114,128,129,227,242,242,257,355,356,370,371,484);
        //assertStreamEqual(expected,spectrum );
    }

    @Test
    public void findCyclicPeptides() {
        List<Integer> spectrum = Arrays.asList(0,71,101,113,131,184,202,214,232,285,303,315,345,416);
        List<List<Integer>> peptides = cyclopeptideSequencing(spectrum);
        loadIntegerMass();
        for( List<Integer> peptide : peptides ) {
            String cyclicPeptide = peptide.stream()
                    .map(i->integerMassTable.entrySet().stream().filter(e->e.getValue().intValue()==i.intValue()).findAny().get().getKey())
                    .reduce( (x,y) -> x+y ).get();
            System.out.print( cyclicPeptide + " " );
        }
    }

    @Test
    public void findConsistentPeptides() {
        Stream<String> peptides = Stream.of("CET", "CTV", "AQV", "TCE", "VAQ", "ETC" );
        List<Integer> spectrum = Arrays.asList(0,71,99,101,103,128,129,199,200,204,227,230,231,298,303,328,330,332,333);
        peptides
                .map(piptide->entry(piptide,findLinearSpectrum(piptide)))
                .filter(e->isContained(e.getValue(),spectrum))
                .map(e->e.getKey())
                .forEach(System.out::println);
    }

    @Test
    public void testCyclopeptideSequencing() throws IOException {
        //List<Integer> spectrum = Stream.of(0, 113, 128, 186, 241, 299, 314, 427).collect(Collectors.toList());
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_100_6.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        List<String> strings = Arrays.asList(in.readLine().split(" "));
        List<Integer> spectrum = strings.stream().map(s -> Integer.valueOf(s)).collect(Collectors.toList());
        List<List<Integer>> peptides = cyclopeptideSequencing(spectrum);
        for( List<Integer> peptide : peptides ) {
            System.out.print( peptide.stream().map(i->i+"").reduce((x,y)->x+"-"+y).get()+" " );
        }
    }
    private void assertStreamEqual(Stream<?> s1, Stream<?> s2) {
        Iterator<?> iter1 = s1.iterator(), iter2 = s2.iterator();
        while(iter1.hasNext() && iter2.hasNext())
            assertEquals(iter1.next(), iter2.next());
        assert !iter1.hasNext() && !iter2.hasNext();
    }
}