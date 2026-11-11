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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DijkstraSolverTest {

    @Test
    void controlRouteS02ToS08Takes24Minutes() {
        DijkstraSolver solver = new DijkstraSolver(RedSeguraDataFactory.createGraph());

        ShortestPathResult result = solver.shortestPath("S02", "S08");

        assertTrue(result.reachable());
        assertEquals(24, result.totalMinutes());
        assertEquals(List.of("S02", "S01", "S07", "S08"), result.vertexPath());
        assertEquals(List.of("R04", "R01", "R16"), result.edgePath());
        assertEquals("S02 -> S01 -> S07 -> S08", result.formattedPath());
    }

    @Test
    void blockingR16ChangesRouteButKeepsCost() {
        MixedGraph graph = RedSeguraDataFactory.createGraph();
        DijkstraSolver solver = new DijkstraSolver(graph);

        graph.blockEdge("R16");
        try {
            ShortestPathResult result = solver.shortestPath("S02", "S08");
            assertEquals(24, result.totalMinutes());
            assertEquals(List.of("S02", "S01", "S09", "S10", "S08"), result.vertexPath());
            assertEquals(List.of("R04", "R03", "R08", "R09"), result.edgePath());
        } finally {
            graph.unblockEdge("R16");
        }

        assertFalse(graph.getEdge("R16").isBlocked());
        assertEquals(24, solver.shortestPath("S02", "S08").totalMinutes());
    }

    @Test
    void distancesFromSourceAreCorrect() {
        DijkstraSolver solver = new DijkstraSolver(RedSeguraDataFactory.createGraph());

        Map<String, Integer> distances = solver.distancesFrom("S02");

        assertEquals(0, distances.get("S02"));
        assertEquals(8, distances.get("S01"));
        assertEquals(10, distances.get("S06"));
        assertEquals(12, distances.get("S07"));
        assertEquals(13, distances.get("S09"));
        assertEquals(14, distances.get("S05"));
        assertEquals(16, distances.get("S10"));
        assertEquals(20, distances.get("S03"));
        assertEquals(20, distances.get("S12"));
        assertEquals(24, distances.get("S08"));
        assertEquals(29, distances.get("S04"));
        assertEquals(35, distances.get("S11"));
    }

    @Test
    void disconnectedTargetIsUnreachable() {
        MixedGraph graph = new MixedGraph();
        graph.addVertex(new Vertex("A", "A", VertexType.UNIVERSIDAD));
        graph.addVertex(new Vertex("B", "B", VertexType.SALUD));

        ShortestPathResult result = new DijkstraSolver(graph).shortestPath("A", "B");

        assertFalse(result.reachable());
        assertEquals(Integer.MAX_VALUE, result.totalMinutes());
        assertTrue(result.vertexPath().isEmpty());
        assertTrue(result.edgePath().isEmpty());
        assertEquals("(sin ruta)", result.formattedPath());
    }

    @Test
    void directedConnectionCannotBeTraversedBackwards() {
        MixedGraph graph = new MixedGraph();
        graph.addVertex(new Vertex("A", "A", VertexType.UNIVERSIDAD));
        graph.addVertex(new Vertex("B", "B", VertexType.SALUD));
        graph.addEdge(new Edge("E1", "A a B", 5, EdgeOrientation.DIRIGIDA, "A", "B"));

        DijkstraSolver solver = new DijkstraSolver(graph);
        assertEquals(5, solver.shortestPath("A", "B").totalMinutes());
        assertFalse(solver.shortestPath("B", "A").reachable());
    }

    @Test
    void nonexistentVerticesAreRejected() {
        DijkstraSolver solver = new DijkstraSolver(RedSeguraDataFactory.createGraph());

        assertThrows(VertexNotFoundException.class, () -> solver.shortestPath("S99", "S08"));
        assertThrows(VertexNotFoundException.class, () -> solver.shortestPath("S02", "S99"));
    }
}
