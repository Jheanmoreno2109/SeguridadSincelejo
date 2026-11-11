package co.edu.redsegura.app;

import co.edu.redsegura.algorithm.ShortestPathResult;
import co.edu.redsegura.exception.VertexNotFoundException;
import co.edu.redsegura.graph.GraphRepresentations;
import co.edu.redsegura.model.Vertex;
import co.edu.redsegura.tree.SearchResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedSeguraCoreTest {

    private final RedSeguraCore core = new RedSeguraCore();

    @Test
    void coreLoadsFactoryDataAndTrees() {
        assertEquals(12, core.vertices().size());
        assertEquals(18, core.edges().size());
        assertEquals(12, core.bstSize());
        assertEquals(12, core.avlSize());
        assertEquals(7, core.bstHeight());
        assertEquals(4, core.avlHeight());
    }

    @Test
    void hospitalIsFoundInBothTrees() {
        SearchResult<Vertex> bstResult = core.searchInBst("Hospital Universitario");
        SearchResult<Vertex> avlResult = core.searchInAvl("Hospital Universitario");

        assertTrue(bstResult.found());
        assertTrue(avlResult.found());
        assertEquals("S05", bstResult.value().getId());
        assertEquals("S05", avlResult.value().getId());
        assertTrue(avlResult.comparisons() <= bstResult.comparisons());
        assertFalse(core.searchInBst("Lugar inexistente").found());
        assertFalse(core.searchInAvl("Lugar inexistente").found());
    }

    @Test
    void treeTraversalsAreOrdered() {
        List<String> bstLabels = core.bstInOrderLabels();
        List<String> avlLabels = core.avlInOrderLabels();
        List<String> sorted = new ArrayList<>(bstLabels);
        sorted.sort(String::compareTo);

        assertEquals(sorted, bstLabels);
        assertEquals(sorted, avlLabels);
    }

    @Test
    void coreExposesRepresentations() {
        GraphRepresentations representations = core.representations();
        assertEquals(12, representations.vertexIds().size());
        assertEquals(18, representations.edgeIds().size());
        assertEquals(4, representations.weightedAdjacencyMatrix()
                [representations.indexOf("S01")][representations.indexOf("S07")]);
    }

    @Test
    void blockingThroughCoreIsTemporary() {
        ShortestPathResult result = core.shortestPathAvoiding("R16", "S02", "S08");

        assertEquals(24, result.totalMinutes());
        assertEquals(List.of("S02", "S01", "S09", "S10", "S08"), result.vertexPath());
        assertFalse(core.edge("R16").isBlocked());
        assertEquals(24, core.shortestPath("S02", "S08").totalMinutes());
    }

    @Test
    void treeAndGraphStructuresAgreeOnVertices() {
        assertEquals(core.vertices().size(), core.avlInOrderLabels().size());
        for (Vertex vertex : core.vertices()) {
            assertTrue(core.searchInAvl(vertex.getLabel()).found());
        }
    }

    @Test
    void unknownVertexProducesDomainException() {
        assertThrows(VertexNotFoundException.class, () -> core.vertex("S99"));
    }

    @Test
    void nullGraphIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new RedSeguraCore(null));
    }
}
