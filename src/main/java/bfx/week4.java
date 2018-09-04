package bfx;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static bfx.week3.*;

public class week4 {
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
