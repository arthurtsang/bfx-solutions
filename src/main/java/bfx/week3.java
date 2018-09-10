package bfx;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.log;
import static java.util.Map.entry;

class week3 {

    enum Nucleobase {
        A(0), C(1), G(2), T(3);
        int index;
        Nucleobase(int index) {
            this.index = index;
        }
        int getIndex(){
            return index;
        }
        static Nucleobase valueOf(char n) {
            return valueOf( Character.toString(n) );
        }
        static Nucleobase valueOf(int index){
            Nucleobase result;
            switch (index) {
                case 0:
                    result = Nucleobase.A;
                    break;
                case 1:
                    result = Nucleobase.C;
                    break;
                case 2:
                    result = Nucleobase.G;
                    break;
                case 3:
                    result = Nucleobase.T;
                    break;
                default:
                    throw new RuntimeException("invalid index");
            }
            return result;
        }
    }

    static List<String> greedyMotifSearchWithLaplace( List<String> dna, int k, int t ) {
        return generalizedGreedyMotifSearch(dna, k, t, week3::motifs2ProfileWithLaplace);
    }

    static List<String> greedyMotifSearch( List<String> dna, int k, int t ) {
        return generalizedGreedyMotifSearch(dna, k, t, week3::motifs2Profile);
    }

    static List<String> generalizedGreedyMotifSearch( List<String> dna, int k, int t, Function<List<String>, double[][]> getProfile ) {
        assert dna.size() == t;
        List<String> bestMotifs = dna.stream().map(s->s.substring(0,k)).collect(Collectors.toList());
        double[] bestMotifsScore = new double[] { motifs2Score(bestMotifs) };

        String baseDna = dna.remove(0);

        IntStream.range(0,baseDna.length()-k+1)
                .mapToObj(i->baseDna.substring(i,i+k))
                .forEach(kmer->{
                    List<String> motifs = new ArrayList<>(Collections.singletonList(kmer));
                    for( String strand : dna ){
                        double[][] profile = getProfile.apply(motifs);
                        String motif = profileMostProbableKmer(strand,k,profile);
                        motifs.add(motif);
                    }
                    double motifsScore = motifs2Score(motifs);
                    if( motifsScore < bestMotifsScore[0] ) {
                        bestMotifsScore[0] = motifsScore;
                        bestMotifs.clear();
                        bestMotifs.addAll(motifs);
                    }
                });
        return bestMotifs;
    }

    static double motifs2Score( List<String> motifs ) {
        return consense2Score(profile2Consensus(motifs2Profile(motifs)),motifs);
    }

    static double[][] motifs2ProfileWithLaplace( List<String> motifs ) {
        return motifs2ProfileWithLaplace(motifs,-1);
    }

    static double[][] motifs2ProfileWithLaplace( List<String> motifs, int skip ) {
        int[][] count = countMotifs(motifs);
        for( int i = 0 ; i < count.length ; i++ ) {
            for( int j = 0 ; j < count[i].length ; j++ ) {
                count[i][j]++;
            }
        }
        return count2Profile( count );
    }

    static double[][] motifs2Profile( List<String> motifs ) {
        return  count2Profile( countMotifs(motifs) );
    }

    static int[][] countMotifs( List<String> motifs ) {
        return countMotifs(motifs, -1);
    }

    static int[][] countMotifs( List<String> motifs, int skip ) {
        int length = motifs.get(0).length();
        int[][] count = new int[4][length];
        IntStream.range(0, motifs.size()).filter(i -> i != skip).mapToObj(motifs::get)
                .forEach(m -> {
                    for (int i = 0; i < m.length(); i++) {
                        int index = Nucleobase.valueOf(Character.toString(m.charAt(i))).getIndex();
                        count[index][i]++;
                    }
                });
        return count;
    }

    static double[][] count2Profile( int[][] count  ) {
        double[][] profile = new double[4][count[0].length];
        int sum = 0;
        for( int i = 0 ; i < 4 ; i++ ) sum += count[i][0];
        for( int i = 0 ; i < 4 ; i++ ) {
            for( int j = 0 ; j < count[0].length ; j++ ) {
                profile[i][j] = (double)count[i][j]/(double)sum;
            }
        }
        return profile;
    }

    static String profile2Consensus( double[][] profile ) {
        StringBuilder sb = new StringBuilder();
        for( int i = 0 ; i < profile[0].length ; i++ ) {
            double max = profile[0][i];
            Nucleobase consensus = Nucleobase.A;
            for( int j = 1 ; j < 4 ; j++ ) {
                if( profile[j][i] > max ) {
                    max = profile[j][i];
                    consensus = Nucleobase.valueOf(j);
                }
            }
            sb.append(consensus.toString());
        }
        return sb.toString();
    }

    static int consense2Score(String consenus, List<String> motifs) {
        return motifs.stream().mapToInt(m->Hamming.hamming(consenus,m)).sum();
    }

    static double[] profile2Entropy(double[][] profile) {
        double[] result = new double[profile[0].length];
        for( int j = 0 ; j < profile[0].length ; j++ ) {
            for( int i = 0 ; i < 4 ; i++ ) {
                if( profile[i][j] == 0.0 ) continue;
                result[j] -= profile[i][j] * log(profile[i][j]) / log(2.0);
            }
        }
        return result;
    }

    static String profileMostProbableKmer(final String dna, final int k, final double[][] profile) {
        return IntStream.range(0,dna.length()-k+1)
                .mapToObj(i->dna.substring(i,i+k))
                .map(p->entry(p, profileProbability(p,profile)))
                .reduce( (x,y) -> (x.getValue()>=y.getValue())?x:y )
                .map(Map.Entry::getKey)
                .get();
    }

    static Double profileProbability(String pattern, double[][] profile) {
        return IntStream.range(0, pattern.length())
                .mapToDouble(i -> profile[Nucleobase.valueOf(pattern.charAt(i)).getIndex()][i])
                .reduce( (x,y)->x*y )
                .getAsDouble();
    }

    static String medianString(final Stream<String> dna, final int k) {
        List<String> dnaList = dna.collect(Collectors.toList());
        String firstDna = dnaList.get(0);
        Map<String, Integer> map = Kmer.kmer(firstDna, k, k - 1)
                .map(p -> entry(p, minDistance(p, dnaList)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        int maxValue = map.values().stream().max(Comparator.naturalOrder()).get();
        List<String> test = map.entrySet().stream().filter(e -> e.getValue() == maxValue).map(Map.Entry::getKey).collect(Collectors.toList());
        return map.entrySet().stream()
                .reduce((x, y) -> (x.getValue() > y.getValue()) ? y : x)
                .map(Map.Entry::getKey)
                .get();
    }

    static int minDistance(final String pattern, final List<String> dna) {
        return dna.stream().mapToInt(s -> minDistance(pattern, s)).sum();
    }

    static int minDistance(final String pattern, final String dna) {
        return IntStream.range(0, dna.length() - pattern.length() + 1)
                .mapToObj(i -> dna.substring(i, i + pattern.length()))
                .mapToInt(s -> Hamming.hamming(pattern, s))
                .min().getAsInt();
    }

    static Stream<String> motifEnumeration(final Stream<String> dna, final int k, final int d) {
        List<String> dnaList = dna.collect(Collectors.toList());
        String firstDna = dnaList.get(0);
        return Kmer.kmer(firstDna, k, d).filter(s -> appearsWithMismatch(s, k, d, dnaList.stream())).distinct();
    }

    static boolean appearsWithMismatch(final String pattern, final int k, final int d, final Stream<String> dna) {
        return dna.allMatch(s -> appearsWithMismatch(pattern, k, d, s));
    }

    static boolean appearsWithMismatch(final String pattern, final int k, final int d, final String dna) {
        return IntStream.range(0, dna.length() - k + 1)
                .mapToObj(i -> dna.substring(i, i + k))
                .anyMatch(s -> Hamming.hamming(s, pattern) <= d);
    }
}
