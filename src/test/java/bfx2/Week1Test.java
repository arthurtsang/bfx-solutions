package bfx2;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static bfx2.Week1.*;

public class Week1Test {

    @Test
    public void testCompositionK() throws IOException {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_197_3.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        int k = Integer.valueOf(in.readLine());
        String text = in.readLine();
        compositionK(text,k).forEach(System.out::println);
    }

    @Test
    public void testReconstructGenomeWithPath() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader( this.getClass().getClassLoader().getResourceAsStream("dataset_198_3.txt") ));
        System.out.println( reconstructGenomeWithPath(in.lines()) );
    }

    @Test
    public void testOverlayGraph() {
        BufferedReader in = new BufferedReader(new InputStreamReader( this.getClass().getClassLoader().getResourceAsStream("dataset_198_10.txt") ));
        overlayGraph(in.lines()).stream().filter(n->!n.next.isEmpty()).forEach(System.out::println);
    }

    @Test
    public void testDeBruijnGraph() throws Exception{
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("dataset_199_6.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        int k = Integer.valueOf(in.readLine());
        String text = in.readLine();
        dbNodeToNode(deBruijnGraph(text,k)).stream().filter(n->!n.next.isEmpty()).forEach(System.out::println);
    }

    @Test
    public void testDeBruijnGraphFromKmer() throws Exception{
        BufferedReader in = new BufferedReader(new InputStreamReader( this.getClass().getClassLoader().getResourceAsStream("dataset_200_8.txt") ));
        deBruijnGraphFromKmer(in.lines()).stream().filter(n->!n.next.isEmpty()).forEach(System.out::println);
    }
}