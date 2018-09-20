package bfx2;

import static bfx2.Week2.*;
import static bfx2.Week1.*;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
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
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_203_7.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        int k = Integer.valueOf(in.readLine());
        Stream<String> text = deBruijnGraphFromKmer(in.lines()).stream().filter(n -> !n.next.isEmpty()).map(Week1.Node::toString);
        LinkedList<Step> path = eulerianPath(text2Graph(text));
        //printPath(path);
        System.out.println( reconstructFromPath(path));
    }
}