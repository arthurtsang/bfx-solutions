package bfx2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Week1 {
    static Stream<String> compositionK( String text, int k ) {
        return IntStream.range(0,text.length()-k+1)
                .mapToObj(i->text.substring(i,i+k));
    }
    static String reconstructGenomeWithPath(Stream<String> gpath) {
        return gpath.reduce( (x,y) -> x+y.substring(y.length()-1)).get();
    }

    static class Node {
        String text;
        String prefix;
        String suffix;
        List<String> next = new ArrayList<>();

        Node(String text) {
            this.text = text;
            this.prefix = text.substring(0,text.length()-1);
            this.suffix = text.substring(1);
        }

        public String toString() {
            return this.text + " -> " + String.join(",", this.next);
        }
    }
    static List<Node> overlayGraph(Stream<String> texts) {
        List<Node> nodes = texts.map(Node::new).collect(Collectors.toList());
        IntStream.range(0,nodes.size()).forEach(i->{
            String suffix = nodes.get(i).suffix;
            nodes.get(i).next = IntStream.range(0, nodes.size())
                    .filter(j -> j != i)
                    .mapToObj(nodes::get)
                    .filter(n -> n.prefix.equals(suffix))
                    .map(n -> n.text)
                    .collect(Collectors.toList());
        });
        return nodes;
    }

    static class dbEdge {
        String text;
        dbNode rightNode;
        dbEdge(String text) {
            this.text = text;
        }
    }

    static class dbNode {
        String text;
        List<dbEdge> rightEdges = new ArrayList<>();
        dbNode(String text) {
            this.text = text;
        }
        public String toString() {
            return this.text + " --> " + rightEdges.stream().map(e->e.rightNode).map(n->n.text).reduce( (x,y)-> x+","+y ).get();
        }
    }

    static List<dbNode> deBruijnGraph(String dna, int k) {
        return deBruijnGraph(compositionK(dna,k));
    }

    static List<dbNode> deBruijnGraph(Stream<String> texts) {
        Map<String,dbNode> nodeMap = new HashMap<>();
        texts.forEach(text->{
            dbEdge edge = new dbEdge(text);
            String prefix = text.substring(0, text.length()-1 );
            dbNode leftNode = nodeMap.getOrDefault(prefix, new dbNode(prefix));
            nodeMap.put(prefix, leftNode);
            String suffix = text.substring(1);
            dbNode rightNode = nodeMap.getOrDefault(suffix, new dbNode(suffix));
            leftNode.rightEdges.add(edge);
            edge.rightNode = rightNode;
            nodeMap.put(suffix, rightNode);
        });
        ArrayList<dbNode> nodes = new ArrayList<>(nodeMap.values());
        nodes.sort((x, y) -> x.text.compareToIgnoreCase(y.text));
        return nodes;
    }

    static List<Node> dbNodeToNode(List<dbNode> dbNodes) {
        return dbNodes.stream().map(d->{
            Node node = new Node(d.text);
            node.next = d.rightEdges.stream().map(e->e.rightNode).map(n->n.text).distinct().collect(Collectors.toList());
            return node;
        }).collect(Collectors.toList());
    }

    static List<Node> deBruijnGraphFromKmer(Stream<String> texts) {
        Map<String,Node> nodes = new HashMap<>();
        texts.forEach(text->{
            String prefix = text.substring(0, text.length()-1);
            String suffix = text.substring(1);
            Node prefixNode = nodes.getOrDefault(prefix, new Node(prefix));
            nodes.putIfAbsent(prefix, prefixNode);
            Node suffixNode = nodes.getOrDefault(suffix, new Node(suffix) );
            nodes.putIfAbsent(suffix, suffixNode);
            prefixNode.next.add(suffix);
        });
        return new ArrayList<>(nodes.values());
    }
}
