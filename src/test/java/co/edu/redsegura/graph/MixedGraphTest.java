package co.edu.redsegura.graph;

import co.edu.redsegura.app.RedSeguraDataFactory;
import co.edu.redsegura.exception.DuplicateIdException;
import co.edu.redsegura.exception.EdgeNotFoundException;
import co.edu.redsegura.exception.InvalidDataException;
import co.edu.redsegura.exception.VertexNotFoundException;
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

class MixedGraphTest {

    private List<String> edgeIds(List<Edge> edges) {
        return edges.stream().map(Edge::getId).toList();
    }

    @Test
    void factoryGraphHasExpectedSize() {
        MixedGraph graph = RedSeguraDataFactory.createGraph();

        assertEquals(12, graph.vertexCount());
        assertEquals(18, graph.edgeCount());
        assertEquals("UAJS Sede E", graph.getVertex("S02").getLabel());
        assertEquals(4, graph.getEdge("R01").getWeight());
    }

    @Test
    void duplicateVertexAndEdgeAreRejected() {
        MixedGraph graph = RedSeguraDataFactory.createGraph();

        assertThrows(DuplicateIdException.class, () -> graph.addVertex(
                new Vertex("S01", "Repetido", VertexType.UNIVERSIDAD)));
        assertThrows(DuplicateIdException.class, () -> graph.addEdge(
                new Edge("R01", "Repetida", 1, EdgeOrientation.DIRIGIDA, "S01", "S07")));
    }

    @Test
    void nonexistentVertexAndEdgeAreRejected() {
        MixedGraph graph = RedSeguraDataFactory.createGraph();

        assertThrows(VertexNotFoundException.class, () -> graph.getVertex("S99"));
        assertThrows(VertexNotFoundException.class, () -> graph.addEdge(
                new Edge("R99", "Fantasma", 1, EdgeOrientation.DIRIGIDA, "S01", "S99")));
        assertThrows(EdgeNotFoundException.class, () -> graph.getEdge("R99"));
        assertThrows(VertexNotFoundException.class, () -> graph.getNeighbors("S99"));
    }

    @Test
    void orientedNeighborsRespectConnectionDirection() {
        MixedGraph graph = RedSeguraDataFactory.createGraph();

        assertEquals(List.of("R01", "R03", "R04"), edgeIds(graph.getNeighbors("S01")));
        assertEquals(List.of("R04", "R05"), edgeIds(graph.getNeighbors("S02")));
        assertEquals(List.of("R02", "R15", "R16"), edgeIds(graph.getNeighbors("S07")));
        assertEquals(List.of("R10"), edgeIds(graph.getNeighbors("S08")));
        assertEquals(List.of("R03", "R07", "R08", "R12", "R15", "R17"),
                edgeIds(graph.getNeighbors("S09")));
    }

    @Test
    void oppositeVertexValidatesDirection() {
        MixedGraph graph = RedSeguraDataFactory.createGraph();
        Edge directed = graph.getEdge("R01");
        Edge undirected = graph.getEdge("R03");

        assertEquals("S07", graph.oppositeVertex(directed, "S01"));
        assertThrows(InvalidDataException.class, () -> graph.oppositeVertex(directed, "S07"));
        assertEquals("S09", graph.oppositeVertex(undirected, "S01"));
        assertEquals("S01", graph.oppositeVertex(undirected, "S09"));
    }

    @Test
    void blockingHidesConnectionFromActiveNeighbors() {
        MixedGraph graph = RedSeguraDataFactory.createGraph();

        graph.blockEdge("R16");
        assertTrue(graph.getEdge("R16").isBlocked());
        assertEquals(List.of("R02", "R15"), edgeIds(graph.getActiveNeighbors("S07")));
        assertEquals(17, graph.getActiveEdges().size());

        graph.unblockEdge("R16");
        assertFalse(graph.getEdge("R16").isBlocked());
        assertEquals(18, graph.getActiveEdges().size());
    }

    @Test
    void removeEdgeUpdatesAdjacencyAndCount() {
        MixedGraph graph = RedSeguraDataFactory.createGraph();

        graph.removeEdge("R16");
        assertEquals(17, graph.edgeCount());
        assertEquals(List.of("R02", "R15"), edgeIds(graph.getNeighbors("S07")));
        assertThrows(EdgeNotFoundException.class, () -> graph.getEdge("R16"));
    }

    @Test
    void graphAcceptsDirectedUndirectedAndMixedConnections() {
        MixedGraph graph = new MixedGraph();
        graph.addVertex(new Vertex("A", "A", VertexType.UNIVERSIDAD));
        graph.addVertex(new Vertex("B", "B", VertexType.SALUD));
        graph.addVertex(new Vertex("C", "C", VertexType.TRANSPORTE));
        graph.addEdge(new Edge("E1", "dirigida", 2, EdgeOrientation.DIRIGIDA, "A", "B"));
        graph.addEdge(new Edge("E2", "no dirigida", 3, EdgeOrientation.NO_DIRIGIDA, "B", "C"));

        assertEquals(1, graph.getNeighbors("A").size());
        assertEquals(1, graph.getNeighbors("B").size());
        assertEquals(1, graph.getNeighbors("C").size());
    }
}
