package bfx;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static bfx.week3.*;
import static java.util.Map.entry;

public class week4 {

    static List<String> gibbsSampler(int k, int t, List<String> dna, int n, int iteration) throws InterruptedException, ExecutionException {
        assert dna.size() == t;

        List<String> bestMotifs = null;
        double bestMotifsScore = 10000.0;

//        Callable<Map.Entry<Double,List<String>>> gibbs = () -> {
//            List<String> motifs = gibbsSampler(k,t,dna,n);
//            double score = motifs2Score(motifs);
//            return entry(score,motifs);
//        };
//
//        ExecutorService executorService = Executors.newFixedThreadPool(4);
//        List<Future<Map.Entry<Double,List<String>>>> results = new ArrayList<>(iteration);
//        for( int i = 0 ; i < iteration ; i++ )
//            results.add(executorService.submit(gibbs));
//        executorService.awaitTermination(5, TimeUnit.MINUTES );
//
//        for( Future<Map.Entry<Double,List<String>>> futureResult: results ) {
//            Map.Entry<Double, List<String>> result = futureResult.get();
//            if( result.getKey() < bestMotifsScore ) {
//                bestMotifsScore = result.getKey();
//                bestMotifs = result.getValue();
//            }
//        }

        for( int i = 0 ; i < iteration; i++ ) {
            List<String> motifs = gibbsSampler(k,t,dna,n);
            double score = motifs2Score(motifs);
            if( score < bestMotifsScore ) {
                bestMotifsScore = score;
                bestMotifs = motifs;
            }
        }
        return bestMotifs;
    }

    static List<String> gibbsSampler(int k, int t, List<String> dna, int n ) {
        assert dna.size() == t;
        Random random = new Random();
        List<String> motifs = randomMotifs(k, dna);
        for( int j = 0; j < n ; j++ ) {
            int i = random.nextInt(t);
            double[][] profile = motifs2ProfileWithLaplace(motifs,i);
            String motif = profileRandomlyGeneratedKmer(dna.get(i),k,profile);
            motifs.set(i,motif);
        }
        return motifs;
    }

    static String profileRandomlyGeneratedKmer(final String strand, final int k, final double[][] profile) {
        List<Double> probabilities = IntStream.range(0,strand.length()-k+1)
                .mapToObj(i->strand.substring(i,i+k))
                .map(kmer->profileProbability(kmer,profile))
                .collect(Collectors.toList());
        Double sum = 0.0;
        for( int i = 0 ; i < probabilities.size() ; i++ ) {
            sum += probabilities.get(i);
            probabilities.set(i,sum);
        }
        Random random = new Random();
        double next = random.nextDouble()*sum;
        int kmerIndex = 0;
        for( int i = 0 ; i < probabilities.size() ; i++ ) {
            if( probabilities.get(i) > next ) {
                kmerIndex = i;
                break;
            }
        }
        return strand.substring(kmerIndex, kmerIndex+k );
    }
    static List<String> randomizedMotifSearch(int k, int t, List<String> dna, int iteration ) {
        assert dna.size() == t;
        List<String> bestMotifs = null;
        double bestMotifsScore = 10000.0;
        for( int i = 0 ; i < iteration; i++ ) {
            List<String> motifs = randomizedMotifSearch(k, dna);
            double score = motifs2Score(motifs);
            if( score < bestMotifsScore ) {
                bestMotifsScore = score;
                bestMotifs = motifs;
            }
        }
        return bestMotifs;
    }

    static List<String> randomizedMotifSearch(int k, List<String> dna) {
        List<String> bestMotifs = randomMotifs(k, dna);
        //used for week 4 quiz
        //List<String> bestMotifs = Arrays.asList("CCA","CCT","CTT","TTG");
        double bestScore = 10000.0;
        while(true) {
            double[][] profile = motifs2ProfileWithLaplace(bestMotifs);
            List<String> motifs = profileMostProbableMotifs(dna,k,profile);
            double score = motifs2Score(motifs);
            if( score < bestScore ) {
                bestScore = score;
                bestMotifs = motifs;
            } else {
                break;
            }
        }
        return bestMotifs;
    }

    static List<String> profileMostProbableMotifs(List<String> dna, int k, double[][] profile) {
        List<String> motifs = new ArrayList<>();
        for( String d : dna ) {
            motifs.add( profileMostProbableKmer(d,k,profile));
        }
        return motifs;
    }

    static List<String> randomMotifs(int k, List<String> dna) {
        Random random = new Random();
        List<String> motifs = new ArrayList<>();
        for( String d : dna ) {
            int i = random.nextInt(d.length()-k);
            motifs.add( d.substring(i,i+k));
        }
        return motifs;
    }
}
