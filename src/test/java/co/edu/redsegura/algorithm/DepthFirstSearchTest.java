package co.edu.redsegura.algorithm;

import co.edu.redsegura.app.RedSeguraDataFactory;
import co.edu.redsegura.exception.VertexNotFoundException;
import co.edu.redsegura.graph.MixedGraph;
import co.edu.redsegura.model.Edge;
import co.edu.redsegura.model.EdgeOrientation;
import co.edu.redsegura.model.Vertex;
import co.edu.redsegura.model.VertexType;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DepthFirstSearchTest {

    @Test
    void dfsProducesDepthFirstOrdersFromS02() {
        DepthFirstSearch dfs = new DepthFirstSearch(RedSeguraDataFactory.createGraph());
        DfsResult result = dfs.traverse("S02");

        assertEquals(List.of("S02", "S01", "S07", "S09", "S05", "S10",
                "S08", "S12", "S03", "S04", "S11", "S06"), result.preOrder());
        assertEquals(List.of("S05", "S08", "S10", "S03", "S11", "S04",
                "S12", "S09", "S07", "S01", "S06", "S02"), result.postOrder());
        assertEquals(12, result.discoveryTime().size());
        assertEquals(1, result.discoveryTime().get("S02"));
        assertEquals(24, result.finishTime().get("S02"));
        assertTrue(result.hasCycle());
        assertEquals(List.of("S01", "S07", "S01"), result.cycle());
    }

    @Test
    void graphCycleIsDetectedGlobally() {
        DepthFirstSearch dfs = new DepthFirstSearch(RedSeguraDataFactory.createGraph());

        assertTrue(dfs.hasCycle());
        assertEquals(List.of("S01", "S07", "S01"), dfs.findCycle());
    }

    @Test
    void acyclicGraphHasNoCycle() {
        MixedGraph graph = new MixedGraph();
        graph.addVertex(new Vertex("A", "A", VertexType.UNIVERSIDAD));
        graph.addVertex(new Vertex("B", "B", VertexType.SALUD));
        graph.addVertex(new Vertex("C", "C", VertexType.TRANSPORTE));
        graph.addEdge(new Edge("E1", "A a B", 1, EdgeOrientation.DIRIGIDA, "A", "B"));
        graph.addEdge(new Edge("E2", "B a C", 1, EdgeOrientation.DIRIGIDA, "B", "C"));

        DepthFirstSearch dfs = new DepthFirstSearch(graph);
        assertFalse(dfs.hasCycle());
        assertTrue(dfs.findCycle().isEmpty());
    }

    @Test
    void weakComponentsIgnoreOrientation() {
        MixedGraph graph = new MixedGraph();
        for (String id : List.of("A", "B", "C", "D", "E")) {
            graph.addVertex(new Vertex(id, "Nodo " + id, VertexType.MOVILIDAD));
        }
        graph.addEdge(new Edge("E1", "A a B", 1, EdgeOrientation.DIRIGIDA, "A", "B"));
        graph.addEdge(new Edge("E2", "C a D", 1, EdgeOrientation.DIRIGIDA, "C", "D"));

        List<List<String>> components = new DepthFirstSearch(graph).weakComponents();

        assertEquals(3, components.size());
        assertEquals(Set.of("A", "B"), new HashSet<>(components.get(0)));
        assertEquals(Set.of("C", "D"), new HashSet<>(components.get(1)));
        assertEquals(List.of("E"), components.get(2));
    }

    @Test
    void fullDatasetIsWeaklyConnected() {
        List<List<String>> components = new DepthFirstSearch(
                RedSeguraDataFactory.createGraph()).weakComponents();

        assertEquals(1, components.size());
        assertEquals(12, components.get(0).size());
    }

    @Test
    void dfsRespectsDirectedOrientation() {
        MixedGraph graph = new MixedGraph();
        graph.addVertex(new Vertex("A", "A", VertexType.UNIVERSIDAD));
        graph.addVertex(new Vertex("B", "B", VertexType.SALUD));
        graph.addVertex(new Vertex("C", "C", VertexType.TRANSPORTE));
        graph.addEdge(new Edge("E1", "A a B", 1, EdgeOrientation.DIRIGIDA, "A", "B"));
        graph.addEdge(new Edge("E2", "B a C", 1, EdgeOrientation.DIRIGIDA, "B", "C"));

        DfsResult fromB = new DepthFirstSearch(graph).traverse("B");
        assertEquals(List.of("B", "C"), fromB.preOrder());
        assertFalse(fromB.reached("A"));
    }

    @Test
    void nonexistentSourceIsRejected() {
        DepthFirstSearch dfs = new DepthFirstSearch(RedSeguraDataFactory.createGraph());
        assertThrows(VertexNotFoundException.class, () -> dfs.traverse("S99"));
    }
}
