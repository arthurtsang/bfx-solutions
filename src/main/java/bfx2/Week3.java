package bfx2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Map.entry;

public class Week3 {

    static final String STOP_CODON = "";
    static Map<String, String> rnaTable = null;
    static Map<String, Integer> integerMassTable= null;
    private static List<Integer> uniqueIntegerMassList;

    static List<List<Integer>> cyclopeptideSequencing(List<Integer> spectrum) {
        List<List<Integer>> foundPeptide = new ArrayList<>();
        int parentMass = spectrum.stream().max(Comparator.naturalOrder()).get();
        Set<List<Integer>> peptides = new HashSet<>();
        peptides.add(new ArrayList<>());
        while( peptides.size() > 0 ) {
            peptides = expandPeptides(peptides);
            Iterator<List<Integer>> iter = peptides.iterator();
            while( iter.hasNext() ) {
                List<Integer> peptide = iter.next();
                if( calcSpectrumMass(peptide) ==  parentMass ) {
                    if( isListEqual( cyclospectrum(peptide), spectrum ) ) {
                        foundPeptide.add(peptide);
                    }
                    iter.remove();
                } else if ( !isContained( peptide, spectrum ) ) {
                    iter.remove();
                }
            }
        }
        return foundPeptide;
    }



    static Set<List<Integer>> expandPeptides(Set<List<Integer>> peptides) {
        if( integerMassTable == null ) integerMassTable = loadIntegerMass();
        if( uniqueIntegerMassList == null ) uniqueIntegerMassList = integerMassTable.values().stream().distinct().sorted().collect(Collectors.toList());
        Set<List<Integer>> result = new HashSet<>();
        for( List<Integer> peptide : peptides ) {
            for( Integer mass : uniqueIntegerMassList ) {
                List<Integer> expanded = new ArrayList<>(peptide);
                expanded.add(mass);
                result.add(expanded);
            }
        }
        return result;
    }

    static boolean isContained(List<Integer> l1, List<Integer> l2) {
        List<Integer> clone = new ArrayList<>(l2);
        for( Integer i : l1 ) {
            if( !clone.remove(i) )
                return false;
        }
        return true;
    }

    static boolean isListEqual(List<Integer> l1, List<Integer> l2) {
        if( l1.size() != l2.size() ) return false;
        Iterator<Integer> i1 = l1.iterator();
        Iterator<Integer> i2 = l2.iterator();
        while( i1.hasNext() && i2.hasNext() ) {
            Integer v1 = i1.next();
            Integer v2 = i2.next();
            if( v1.intValue() != v2.intValue() )
                return false;
        }
        return true;
    }

    static List<Integer> cyclospectrum(List<Integer> peptide) {
        ArrayList<Integer> cyclopeptide = new ArrayList<>(peptide);
        cyclopeptide.addAll(peptide);
        int n = peptide.size();
        List<Integer> integerStream = new ArrayList<>();
        integerStream.add(0);
        integerStream.addAll(peptide);
        for( int i = 2; i < n ; i++ ) {
            final int length = i;
            List<Integer> spectrum = IntStream.range(0,n)
                    .mapToObj(j->cyclopeptide.subList(j,j+length))
                    .map(p->calcSpectrumMass(p))
                    .collect(Collectors.toList());
            integerStream.addAll(spectrum);
        }
        integerStream.add(calcSpectrumMass(peptide));
        integerStream.sort(Comparator.naturalOrder());
        return integerStream;
    }

    static Map<String,Integer> loadIntegerMass() {
        InputStream input = Week3.class.getClassLoader().getResourceAsStream("integer_mass_table.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        return in.lines().map(s->s.split(" ")).map(s->entry(s[0],Integer.valueOf(s[1]))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    static List<Integer> findLinearSpectrum( String peptide ) {
        List<Integer> result = new ArrayList<>();
        result.add(0);
        if( integerMassTable == null ) integerMassTable = loadIntegerMass();
        int n = peptide.length();
        for( int i = 1 ; i < n ; i++ ) {
            for( int j = 0 ; j < n-i ; j++ ) {
                String p = peptide.substring(j,j+i);
                Integer mass = calcSpectrumMass(p);
                result.add(mass);
            }
        }
        result.sort(Comparator.naturalOrder());
        return result;
    }
    static Stream<Integer> findTheoreticalSpectrum( String peptide) {
        if( integerMassTable == null ) integerMassTable = loadIntegerMass();
        int n = peptide.length();
        String text = peptide + peptide;
        Stream<Integer> integerStream = Stream.of(0);
        for( int i = 1 ; i < n ; i++ ) {
            final int length = i;
           integerStream = Stream.concat(
                   integerStream,
                   IntStream.range(0,n)
                           .mapToObj(j->text.substring(j,j+length))
                           .map(s-> calcSpectrumMass(s)));
        }
        integerStream = Stream.concat(integerStream, Stream.of(calcSpectrumMass(peptide))).sorted();
        return integerStream;
    }

    static int calcSpectrumMass(List<Integer> peptide) {
        return peptide.stream().reduce( (x,y)-> x+y ).get();
    }

    static int calcSpectrumMass(String text){
        return text.chars()
                .mapToObj(c->Character.toString((char)c))
                .map(s->integerMassTable.get(s))
                .reduce((x,y)->x+y).get();
    }

    static Map<String,String> loadRnaCodonTable() {
        InputStream input = Week3.class.getClassLoader().getResourceAsStream("RNA_codon_table_1.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        return in.lines().map(s->s.split(" ")).map(s->entry(s[0],(s.length==2)?s[1]:STOP_CODON)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    static String proteinTranslation(String rna) {
        assert rna.length()%3 == 0;
        if(rnaTable==null) rnaTable = loadRnaCodonTable();
        return IntStream.range(0,rna.length()/3)
                .mapToObj(i->rna.substring(i*3,i*3+3))
                .map(key -> rnaTable.get(key))
                .reduce( (x, y)->x+y ).get();
    }

    static String reverseComplement(String text) {
        String reversed = IntStream.range(0,text.length())
                .mapToObj(i-> {
                    switch (text.charAt(i)) {
                        case 'A':
                            return "T";
                        case 'T':
                            return "A";
                        case 'G':
                            return "C";
                        default:
                            return "G";
                    }
                })
                .reduce( (x,y)->x+y ).get();
        return new StringBuilder(reversed).reverse().toString();
    }

    static String rnaTranscribe(String dna) {
        return dna.replace('T', 'U');
    }

    static Stream<String> findPeptideForAminoAcid(String rna, String aminoAcid) {
        int peptideLength = aminoAcid.length()*3;
        return IntStream.range(0,rna.length()-peptideLength)
                .mapToObj(i->rna.substring(i,i+peptideLength))
                .flatMap(d->
                        Stream.of(
                                entry(d,proteinTranslation(rnaTranscribe(d))),
                                entry(d,proteinTranslation(rnaTranscribe(reverseComplement(d))))
                        ))
                .filter(e->e.getValue().equals(aminoAcid))
                .map(Map.Entry::getKey);
    }
}
