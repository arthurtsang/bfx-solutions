package bfx2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Map.entry;

class Week2 {

    static class Node {
        String value;
        List<Node> incoming = new ArrayList<>(), outgoing = new ArrayList<>();
        Node(String value) {
            this.value = value;
        }
        int inDegree() { return incoming.size(); }
        int outDegree() { return outgoing.size(); }
        boolean isBalance() { return incoming.size() == outgoing.size(); }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    static Map<String, Node> text2Graph(Stream<String> text) {
        Map<String,Node> graph = new HashMap<>();
        text.forEach(t->{
            int index = t.indexOf("->");
            String leftNodeValue = t.substring(0,index).trim();
            String[] rightNodeValues = t.substring(index+2).trim().split(",");
            Node leftNode = ( graph.containsKey(leftNodeValue) ) ? graph.get(leftNodeValue) : new Node(leftNodeValue);
            graph.putIfAbsent(leftNodeValue, leftNode);
            for (String rightNodeValue : rightNodeValues) {
                rightNodeValue = rightNodeValue.trim();
                Node rightNode = (graph.containsKey(rightNodeValue)) ? graph.get(rightNodeValue) : new Node(rightNodeValue);
                leftNode.outgoing.add(rightNode);
                rightNode.incoming.add(leftNode);
                graph.putIfAbsent(rightNodeValue, rightNode);
            }
        });
        return graph;
    }
    static class Step {
        Node node;
        Step next;
        public Step(Node node) {
            this.node = node;
        }
    }

    static LinkedList<Step> eulerianCycle(Map<String,Node> graph) {
        //Random rand = new Random();
        //int start = rand.nextInt(graph.size());
        return eulerian(graph.values().iterator().next().value, graph);
    }

    static LinkedList<Step> eulerianPath(Map<String,Node> graph) {
        Optional<Node> unbalanceNode = graph.values().stream().filter(n -> n.outDegree() > n.inDegree()).findFirst();
        if( unbalanceNode.isPresent() ) {
            return eulerian(unbalanceNode.get().value, graph);
        } else {
            throw new RuntimeException("the graph is perfectly balanced");
        }
    }
    private static LinkedList<Step> eulerian(String start, Map<String,Node> graph) {
        LinkedList<Step> path = new LinkedList<>();
        int insertPos = 0;
        do {
            LinkedList<Step> part = walkGraph(graph.get(start));
            if( path.size() > 0 ) part.removeFirst();
            path.addAll(insertPos, part);
            Optional<Map.Entry<Integer, Step>> entry = IntStream.range(0, path.size()).mapToObj(i -> entry(i, path.get(i))).filter(e -> e.getValue().node.outDegree() > 0).findFirst();
            if( entry.isPresent() ) {
                start = entry.get().getValue().node.value;
                insertPos = entry.get().getKey()+1;
            } else {
                start = null;
            }
        }while ( start != null );
        return path;
    }

    static void printPath(LinkedList<Step> path) {
        path.stream().map(s->s.node.value).reduce( (s1,s2) -> s1 + "->" + s2 ).ifPresent(System.out::print);
    }

    private static LinkedList<Step> walkGraph(Node start) {
        LinkedList<Step> path = new LinkedList<>();
        Step current = new Step(start);
        path.add(current);
        do {
            Node next = current.node.outgoing.remove(0);
            current.next = new Step(next);
            current = current.next;
            path.add(current);
        } while ( current.node.outDegree() != 0 );
        return path;
    }

    static String reconstructFromPath( LinkedList<Step> path ) {
        StringBuilder sb = new StringBuilder(path.removeFirst().node.value);
        for( Step step : path ) {
            String value = step.node.value;
            sb.append(value.substring(value.length()-1));
        }
        return sb.toString();
    }

    static Stream<String> getAllBinaryCombination(int k) {
        return IntStream.range(0,(int)Math.pow(2.0,(double)k)).mapToObj(Integer::toBinaryString).map(s->String.format("%"+k+"s",s).replace(' ', '0'));
    }

    static class PairedComposition implements Comparable{
        String pattern1, pattern2;
        PairedComposition(String p1, String p2) {
            pattern1 = p1;
            pattern2 = p2;
        }
        @Override
        public String toString() {
            return String.format("(%s|%s)", pattern1, pattern2);
        }

        @Override
        public int compareTo(Object o) {
            PairedComposition p = (PairedComposition)o;
            if( p.pattern1.equals(this.pattern1) ) {
                return this.pattern2.compareTo(p.pattern2);
            }
            return this.pattern1.compareTo(p.pattern1);
        }
    }

    static List<PairedComposition> getParedComposition(String text, int k, int d ) {
        final List<String> kmer = IntStream.range(0,text.length()-k+1).mapToObj(i->text.substring(i,i+k)).collect(Collectors.toList());
        return IntStream.range(0,text.length()-2*k-d+1).mapToObj(i->new PairedComposition(kmer.get(i), kmer.get(i + k + d))).collect(Collectors.toList());
    }

}
