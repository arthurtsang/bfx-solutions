package bfx2;

import static bfx2.Week2.*;
import static bfx2.Week1.*;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Week2Test {

    @Test
    public void testEulerianCycle() {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_203_2.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        LinkedList<Step> path = eulerianCycle(text2Graph(in.lines()));
        printPath(path);
    }
    @Test
    public void testEulerianPath() {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_203_6.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        LinkedList<Step> path = eulerianPath(text2Graph(in.lines()));
        printPath(path);
    }
    @Test
    public void testRestruct() throws IOException {
        //InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_203_7.txt");
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("input.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        //int k = Integer.valueOf(in.readLine());
        Stream<String> text = deBruijnGraphFromKmer(in.lines()).stream().filter(n -> !n.next.isEmpty()).map(Week1.Node::toString);
        LinkedList<Step> path = eulerianPath(text2Graph(text));
        //printPath(path);
        System.out.println( reconstructFromPath(path));
    }

    @Test
    public void testKUniveralCircularString() throws IOException {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_203_11.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        int k = Integer.valueOf(in.readLine());
        Stream<String> text = deBruijnGraphFromKmer(getAllBinaryCombination(k)).stream().filter(n -> !n.next.isEmpty()).map(Week1.Node::toString);
        LinkedList<Step> path = eulerianCycle(text2Graph(text));
        System.out.println(reconstructFromPath(path).substring(0,(int)Math.pow(2,k)));
    }

    @Test
    public void testPairedComposition() {
       String text = "TAATGCCATGGGATGTT";
        List<ReadPair> pc = getParedComposition(text, 3, 2);
        pc.stream().sorted().forEach(System.out::print);
    }

    @Test
    public void testPairedReconstruct() throws IOException {
        //InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_204_15.txt");
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("input.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String[] params = in.readLine().split(" ");
        int k = Integer.valueOf(params[0]);
        int d = Integer.valueOf(params[1]);
        Stream<String> text = in.lines();
        Stream<ReadPair> readPairs = text2ReadPair(text);
        Map<String, PairedNode> readPairGraph = pairedDeBruijnGraph(readPairs);
        LinkedList<PairedStep> pairedPath = pairedEulerianPath(readPairGraph);
        System.out.print( reconstructFromPairedPath(pairedPath,k,d));
    }

    @Test
    public void testContig() {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_205_5.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        Stream<String> text = deBruijnGraphFromKmer(in.lines()).stream().filter(n -> !n.next.isEmpty()).map(Week1.Node::toString);
        Map<String, Week2.Node> graph = text2Graph(text);
        List<String> contigs = contig(graph);
        contigs.stream().sorted().forEach(s->System.out.print(s+" "));
    }
}