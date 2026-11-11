package co.edu.redsegura.algorithm;

import co.edu.redsegura.app.RedSeguraDataFactory;
import co.edu.redsegura.exception.VertexNotFoundException;
import co.edu.redsegura.graph.MixedGraph;
import co.edu.redsegura.model.Edge;
import co.edu.redsegura.model.EdgeOrientation;
import co.edu.redsegura.model.Vertex;
import co.edu.redsegura.model.VertexType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BreadthFirstSearchTest {

    @Test
    void bfsVisitsByLevelsFromS02() {
        BreadthFirstSearch bfs = new BreadthFirstSearch(RedSeguraDataFactory.createGraph());
        BfsResult result = bfs.traverse("S02");

        assertEquals(List.of("S02", "S01", "S06", "S07", "S09", "S05",
                "S08", "S10", "S12", "S03", "S04", "S11"), result.visitOrder());
        assertEquals(0, result.levelOf("S02"));
        assertEquals(1, result.levelOf("S01"));
        assertEquals(2, result.levelOf("S07"));
        assertEquals(3, result.levelOf("S08"));
        assertEquals(5, result.levelOf("S11"));
        assertEquals("S02", result.parentByVertex().get("S01"));
        assertEquals("S01", result.parentByVertex().get("S07"));
        assertEquals("S06", result.parentByVertex().get("S05"));
        assertEquals("S07", result.parentByVertex().get("S08"));
        assertEquals("R16", result.treeEdgeByVertex().get("S08"));
        assertEquals(List.of("S10", "S12", "S03"), result.treeChildren().get("S09"));
        assertEquals(List.of("S02", "S01", "S09", "S12", "S04", "S11"),
                result.pathTo("S11"));
    }

    @Test
    void bfsRespectsBlockedConnections() {
        MixedGraph graph = RedSeguraDataFactory.createGraph();
        graph.blockEdge("R16");
        try {
            BfsResult result = new BreadthFirstSearch(graph).traverse("S02");
            assertTrue(result.reached("S08"));
            assertEquals("S10", result.parentByVertex().get("S08"));
            assertEquals(4, result.levelOf("S08"));
        } finally {
            graph.unblockEdge("R16");
        }
    }

    @Test
    void bfsOnDisconnectedGraphVisitsOnlyItsComponent() {
        MixedGraph graph = new MixedGraph();
        graph.addVertex(new Vertex("A", "A", VertexType.UNIVERSIDAD));
        graph.addVertex(new Vertex("B", "B", VertexType.SALUD));
        graph.addVertex(new Vertex("C", "C", VertexType.TRANSPORTE));
        graph.addEdge(new Edge("E1", "A a B", 1, EdgeOrientation.DIRIGIDA, "A", "B"));

        BfsResult fromC = new BreadthFirstSearch(graph).traverse("C");

        assertEquals(List.of("C"), fromC.visitOrder());
        assertFalse(fromC.reached("A"));
        assertTrue(fromC.pathTo("A").isEmpty());
    }

    @Test
    void bfsRespectsDirectedOrientation() {
        MixedGraph graph = new MixedGraph();
        graph.addVertex(new Vertex("A", "A", VertexType.UNIVERSIDAD));
        graph.addVertex(new Vertex("B", "B", VertexType.SALUD));
        graph.addVertex(new Vertex("C", "C", VertexType.TRANSPORTE));
        graph.addEdge(new Edge("E1", "A a B", 1, EdgeOrientation.DIRIGIDA, "A", "B"));
        graph.addEdge(new Edge("E2", "B a C", 1, EdgeOrientation.DIRIGIDA, "B", "C"));

        assertEquals(List.of("B", "C"), new BreadthFirstSearch(graph).traverse("B").visitOrder());
        assertEquals(List.of("A", "B", "C"), new BreadthFirstSearch(graph).traverse("A").visitOrder());
    }

    @Test
    void nonexistentSourceIsRejected() {
        BreadthFirstSearch bfs = new BreadthFirstSearch(RedSeguraDataFactory.createGraph());
        assertThrows(VertexNotFoundException.class, () -> bfs.traverse("S99"));
    }
}
