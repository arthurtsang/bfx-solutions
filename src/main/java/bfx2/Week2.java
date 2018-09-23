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
        boolean shouldStop = false;

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

    static class ReadPair implements Comparable{
        String pattern1, pattern2;
        ReadPair(String p1, String p2) {
            pattern1 = p1;
            pattern2 = p2;
        }
        ReadPair getPrefix() {
            int k = pattern1.length();
            return new ReadPair( pattern1.substring(0,k-1), pattern2.substring(0,k-1));
        }
        ReadPair getSuffix() {
            return new ReadPair( pattern1.substring(1), pattern2.substring(1));
        }
        @Override
        public String toString() {
            return String.format("(%s|%s)", pattern1, pattern2);
        }
        @Override
        public int compareTo(Object o) {
            ReadPair p = (ReadPair)o;
            if( p.pattern1.equals(this.pattern1) ) {
                return this.pattern2.compareTo(p.pattern2);
            }
            return this.pattern1.compareTo(p.pattern1);
        }
    }

    static List<ReadPair> getParedComposition(String text, int k, int d ) {
        final List<String> kmer = IntStream.range(0,text.length()-k+1).mapToObj(i->text.substring(i,i+k)).collect(Collectors.toList());
        return IntStream.range(0,text.length()-2*k-d+1).mapToObj(i->new ReadPair(kmer.get(i), kmer.get(i + k + d))).collect(Collectors.toList());
    }

    static class PairedNode {
        ReadPair readPair;
        List<PairedNode> incoming = new ArrayList<>();
        List<PairedNode> outgoing = new ArrayList<>();
        PairedNode(ReadPair readPair) {
            this.readPair = readPair;
        }
        int inDegree() { return incoming.size(); }
        int outDegree() { return outgoing.size(); }
        boolean isBalance() { return incoming.size() == outgoing.size(); }
        @Override
        public int hashCode() {
            return (readPair.pattern1+readPair.pattern2).hashCode();
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            incoming.stream().map(n->n.readPair).forEach(sb::append);
            sb.append(" -> ");
            sb.append( readPair );
            sb.append(" -> ");
            outgoing.stream().map(n->n.readPair).forEach(sb::append);
            return sb.toString();
        }
    }

    static Stream<ReadPair> text2ReadPair(Stream<String> text) {
        return text.map(s -> s.split("\\|")).map(s -> new ReadPair(s[0], s[1]));
    }

    static Map<String, PairedNode> pairedDeBruijnGraph(Stream<ReadPair> readPairs) {
        Map<String,PairedNode> nodes = new HashMap<>();
        readPairs.forEach(readPair->{
            ReadPair prefix = readPair.getPrefix();
            ReadPair suffix = readPair.getSuffix();
            PairedNode prefixNode = nodes.getOrDefault(prefix.toString(), new PairedNode(prefix));
            nodes.putIfAbsent(prefix.toString(), prefixNode);
            PairedNode suffixNode = nodes.getOrDefault(suffix.toString(), new PairedNode(suffix) );
            nodes.putIfAbsent(suffix.toString(), suffixNode);
            prefixNode.outgoing.add(suffixNode);
            suffixNode.incoming.add(prefixNode);
        });
        return nodes;
    }

    static class PairedStep {
        PairedNode node;
        PairedStep next;
        public PairedStep(PairedNode node) {
            this.node = node;
        }
    }

    static LinkedList<PairedStep> pairedEulerianPath(Map<String,PairedNode> graph) {
        Optional<PairedNode> unbalanceNode = graph.values().stream().filter(n -> n.outDegree() > n.inDegree()).findFirst();
        if( unbalanceNode.isPresent() ) {
            return pairedEulerian(unbalanceNode.get().readPair.toString(), graph);
        } else {
            throw new RuntimeException("the graph is perfectly balanced");
        }
    }
    private static LinkedList<PairedStep> pairedEulerian(String start, Map<String,PairedNode> graph) {
        LinkedList<PairedStep> path = new LinkedList<>();
        int insertPos = 0;
        do {
            LinkedList<PairedStep> part = pairedWalkGraph(graph.get(start));
            if( path.size() > 0 ) part.removeFirst();
            path.addAll(insertPos, part);
            Optional<Map.Entry<Integer, PairedStep>> entry = IntStream.range(0, path.size()).mapToObj(i -> entry(i, path.get(i))).filter(e -> e.getValue().node.outDegree() > 0).findFirst();
            if( entry.isPresent() ) {
                start = entry.get().getValue().node.readPair.toString();
                insertPos = entry.get().getKey()+1;
            } else {
                start = null;
            }
        }while ( start != null );
        return path;
    }

    private static LinkedList<PairedStep> pairedWalkGraph(PairedNode start) {
        LinkedList<PairedStep> path = new LinkedList<>();
        PairedStep current = new PairedStep(start);
        path.add(current);
        do {
            PairedNode next = current.node.outgoing.remove(0);
            current.next = new PairedStep(next);
            current = current.next;
            path.add(current);
        } while ( current.node.outDegree() != 0 );
        return path;
    }

    static String reconstructFromPairedPath( LinkedList<PairedStep> path, int k, int d ) {
        ReadPair firstReadPair = path.removeFirst().node.readPair;
        StringBuilder p1sb = new StringBuilder(firstReadPair.pattern1);
        StringBuilder p2sb = new StringBuilder(firstReadPair.pattern2);
        for( PairedStep step : path ) {
            ReadPair readPair = step.node.readPair;
            p1sb.append(readPair.pattern1.substring(readPair.pattern1.length()-1));
            p2sb.append(readPair.pattern2.substring(readPair.pattern2.length()-1));
        }
        String p1 = p1sb.toString();
        String p2 = p2sb.toString();
        return p1.substring(0,k+d) + p2;
    }

    static List<String> contig( Map<String, Node> graph ) {
        List<String> contigs = new ArrayList<>();
        for( Node node : graph.values() ) {
            if (node.inDegree() != 1 || node.outDegree() != 1) {
                node.shouldStop = true;
            }
        }
        do {
            for( Node node : graph.values() ) {
                if( node.shouldStop ) {
                    if( node.outDegree() == 0 ) continue;
                    LinkedList<Step> branch = findBranch(node, graph);
                    contigs.add( reconstructFromPath(branch) );
                    break;
                }
            }
            Set<String> keys = new HashSet<>(graph.keySet());
            for( String key : keys ) {
                Node node = graph.get(key);
                if( node.inDegree() == 0 && node.outDegree() == 0 ) {
                    graph.remove(node.value);
                }
            }
        } while( graph.size() > 0 );
        return contigs;
    }

    private static LinkedList<Step> findBranch(Node node, Map<String, Node> graph ) {
        LinkedList<Step> path = new LinkedList<>();
        path.add( new Step(node) );
        do {
            Node nextNode = node.outgoing.remove(0);
            nextNode.incoming.remove(node);
            node = nextNode;
            path.add( new Step(node) );
        }while( !node.shouldStop);
        return path;
    }
}
