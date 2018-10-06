package bfx2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static bfx2.Week3.*;

import static bfx2.Week3.findTheoreticalSpectrum;

public class Week4 {

    private static Map<String, Integer> integerMass;
    private static List<Integer> uniqueIntegerMassList;

    static int cyclopeptideScore(String peptide, List<Integer> spectrum) {
        List<Integer> theoraticalSpectrum = findTheoreticalSpectrum(peptide).collect(Collectors.toList());
        return cyclopeptideScore(theoraticalSpectrum, spectrum);
    }
    static int cyclopeptideScore(List<Integer> peptide, List<Integer> spectrum) {
        int score = 0;
        List<Integer> clone = new ArrayList<>(peptide);
        for( Integer mass : spectrum ) {
            if( clone.remove(mass) ) score++;
        }
        return score;
    }
    static List<Integer> peptideMass(String peptide) {
        if( integerMassTable == null ) integerMassTable = loadIntegerMass();
        int n = peptide.length();
        return IntStream.range(0,n).mapToObj(i->peptide.substring(i,i+1)).map(s->integerMassTable.get(s)).collect(Collectors.toList());
    }
    static List<Integer> findLinearTheoreticalSpectrum(List<Integer> peptide ) {
        if( integerMassTable == null ) integerMassTable = loadIntegerMass();
        int n = peptide.size();
        Stream<Integer> integerStream = Stream.of(0);
        for( int i = 1 ; i < n ; i++ ) {
            final int length = i;
            integerStream = Stream.concat(
                    integerStream,
                    IntStream.range(0,n-length+1)
                            .mapToObj(j->peptide.subList(j,j+length))
                            .map(s-> calcSpectrumMass(s)));
        }
        integerStream = Stream.concat(integerStream, Stream.of(calcSpectrumMass(peptide))).sorted();
        return integerStream.collect(Collectors.toList());
    }

    static class PeptideTriple implements Comparable{
        List<Integer> peptide;
        int score;
        int mass;
        PeptideTriple( List<Integer> peptide, int score, int mass ) {
            this.peptide = peptide;
            this.score = score;
            this.mass = mass;
        }
        PeptideTriple() {
            peptide = new ArrayList<>();
            score = 0;
            mass = 0;
        }
        PeptideTriple expand(int massToAdd, int parentMass, List<Integer> spectrum) {
            List<Integer> expanded = new ArrayList<>(peptide);
            expanded.add(massToAdd);
            int newMass = mass + massToAdd;
            return (newMass <= parentMass)
                    ? new PeptideTriple(expanded, cyclopeptideScore(findLinearTheoreticalSpectrum(expanded), spectrum), newMass)
                    : null;
        }
        @Override
        public String toString() {
            return (peptide.size()==0)?"":peptide.stream().map(i->i+"").reduce((x,y)->x+"-"+y).get();
        }
        @Override
        public boolean equals(Object o) {
            PeptideTriple to = (PeptideTriple)o;
            return this.toString().equals(to.toString());
        }
        @Override
        public int compareTo(Object o) {
            PeptideTriple to = (PeptideTriple)o;
            return (to.score == this.score) ? 1 : Integer.compare(this.score,to.score);
        }
        @Override
        public int hashCode() {
            return toString().hashCode();
        }
    }

    static Stream<PeptideTriple> expandPeptidesStream(Set<PeptideTriple> peptides, int parentMass, List<Integer> spectrum) {
        if( integerMassTable == null ) integerMassTable = loadIntegerMass();
        if( uniqueIntegerMassList == null ) uniqueIntegerMassList = integerMassTable.values().stream().distinct().sorted().collect(Collectors.toList());
        return peptides.stream().flatMap(p->uniqueIntegerMassList.stream().map(m->p.expand(m,parentMass,spectrum)).filter(Objects::nonNull));
    }

    static List<Integer> leaderbaordCyclopeptideSequencing( int N, List<Integer> spectrum ) {
        int parentMass = spectrum.stream().max(Comparator.naturalOrder()).get();
        TreeSet<PeptideTriple> leaderboard = new TreeSet<>();
        final PeptideTriple[] leaderPeptide = {new PeptideTriple()};
        leaderboard.add(leaderPeptide[0]);

        while (leaderboard.size() > 0) {
            //System.out.print( leaderboard.size() + " " );
            //System.out.flush();
            TreeSet<PeptideTriple> tempLeaderBoard = new TreeSet<>();
            expandPeptidesStream(leaderboard,parentMass,spectrum)
                    .peek(t->{
                        if( t.mass == parentMass && t.score > leaderPeptide[0].score ) {
                            leaderPeptide[0] = t;
                        }
                    })
                    .forEach(tempLeaderBoard::add);
            leaderboard = trimLeaderboard(N,tempLeaderBoard);
        }
        return leaderPeptide[0].peptide;
    }

    static TreeSet<PeptideTriple> trimLeaderboard(int N, TreeSet<PeptideTriple> leaderboard) {
        if( leaderboard.size() >= N ) {
            Iterator<PeptideTriple> iter = leaderboard.descendingIterator();
            TreeSet<PeptideTriple> trimmedLeaderBoard = new TreeSet<>();
            int i = 0;
            int minScore = 0;
            while( iter.hasNext() ) {
                PeptideTriple p = iter.next();
                if( i == N ) {
                    minScore = p.score;
                    //break;
                }
                if( p.score >= minScore ) trimmedLeaderBoard.add(p);
                i++;
            }
            return trimmedLeaderBoard;
        }
        return leaderboard;
    }

    static List<Integer> spectralConvolution(List<Integer> spectrum ) {
        List<Integer> result = new ArrayList<>();
        for( int i = 0 ; i < spectrum.size() ; i ++ ) {
            for( int j = 0 ; j < spectrum.size(); j++ ) {
                int diff = spectrum.get(i) - spectrum.get(j);
                if( diff > 0 ) result.add(diff);
            }
        }
        return result;
    }

    static class SpectralConvolutionComparator implements Comparator<Map.Entry> {
        @Override
        public int compare(Map.Entry e1, Map.Entry e2) {
            return Integer.compare((int)e2.getValue(), (int)e1.getValue());
        }
    }

    static List<Integer> topSpectralConvolution(List<Integer> spectrum, int M) {
        Map<Integer, Integer> resultMap = new HashMap<>();
        for( int i = 0 ; i < spectrum.size() ; i ++ ) {
            for( int j = 0 ; j < spectrum.size(); j++ ) {
                int diff = spectrum.get(i) - spectrum.get(j);
                if( diff >= 57 && diff <= 200 )  {
                    resultMap.computeIfPresent(diff, (k,v)->v+1);
                    resultMap.putIfAbsent(diff, 1);
                }
            }
        }
        List<Map.Entry<Integer, Integer>> ordered = resultMap.entrySet().stream().sorted(new SpectralConvolutionComparator()).collect(Collectors.toList());
        int minCount = ordered.get(M-1).getValue();
        return ordered.stream().filter(e->e.getValue()>=minCount).map(e->e.getKey()).collect(Collectors.toList());
    }

    static Stream<PeptideTriple> expandConvolutionPeptidesStream(List<Integer>convolutionSpectrum, Set<PeptideTriple> peptides, int parentMass, List<Integer> spectrum) {
        return peptides.stream().flatMap(p->convolutionSpectrum.stream().map(m->p.expand(m,parentMass,spectrum)).filter(Objects::nonNull));
    }

    static List<Integer> convolutionCyclopeptideSequencing( int N, int M, List<Integer> spectrum ) {
        int parentMass = spectrum.stream().max(Comparator.naturalOrder()).get();
        List<Integer> convolutionSpectrum = topSpectralConvolution(spectrum, M);
        TreeSet<PeptideTriple> leaderboard = new TreeSet<>();
        final PeptideTriple[] leaderPeptide = {new PeptideTriple()};
        leaderboard.add(leaderPeptide[0]);

        while (leaderboard.size() > 0) {
            //System.out.print( leaderboard.size() + " " );
            //System.out.flush();
            TreeSet<PeptideTriple> tempLeaderBoard = new TreeSet<>();
            expandConvolutionPeptidesStream(convolutionSpectrum,leaderboard,parentMass,spectrum)
                    .peek(t->{
                        if( t.mass == parentMass && t.score > leaderPeptide[0].score ) {
                            leaderPeptide[0] = t;
                        }
                    })
                    .forEach(tempLeaderBoard::add);
            leaderboard = trimLeaderboard(N,tempLeaderBoard);
        }
        return leaderPeptide[0].peptide;
    }
}
