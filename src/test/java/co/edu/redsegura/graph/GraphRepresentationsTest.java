package co.edu.redsegura.graph;

import co.edu.redsegura.app.RedSeguraDataFactory;
import co.edu.redsegura.model.EdgeOrientation;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphRepresentationsTest {

    private final GraphRepresentations representations =
            new GraphRepresentations(RedSeguraDataFactory.createGraph());

    @Test
    void allRepresentationsUseInsertionOrder() {
        assertEquals(List.of("S01", "S02", "S03", "S04", "S05", "S06",
                "S07", "S08", "S09", "S10", "S11", "S12"), representations.vertexIds());
        assertEquals(18, representations.edgeIds().size());
        assertEquals("R01", representations.edgeIds().get(0));
        assertEquals("R18", representations.edgeIds().get(17));
    }

    @Test
    void adjacencyListShowsWeightsAndOrientation() {
        Map<String, List<GraphRepresentations.Neighbor>> adjacency =
                representations.adjacencyList();

        List<GraphRepresentations.Neighbor> fromS07 = adjacency.get("S07");
        assertEquals(List.of("S01", "S09", "S08"),
                fromS07.stream().map(GraphRepresentations.Neighbor::vertexId).toList());
        assertEquals(List.of(6, 3, 12),
                fromS07.stream().map(GraphRepresentations.Neighbor::weight).toList());
        assertEquals(EdgeOrientation.DIRIGIDA, fromS07.get(0).orientation());
        assertEquals(EdgeOrientation.NO_DIRIGIDA, fromS07.get(1).orientation());

        List<GraphRepresentations.Neighbor> fromS02 = adjacency.get("S02");
        assertEquals(List.of("S01", "S06"),
                fromS02.stream().map(GraphRepresentations.Neighbor::vertexId).toList());
    }

    @Test
    void weightedAdjacencyMatrixReflectsDirectionAndSymmetry() {
        int[][] matrix = representations.weightedAdjacencyMatrix();
        int s01 = representations.indexOf("S01");
        int s02 = representations.indexOf("S02");
        int s07 = representations.indexOf("S07");
        int s08 = representations.indexOf("S08");
        int s09 = representations.indexOf("S09");
        int s10 = representations.indexOf("S10");

        assertEquals(4, matrix[s01][s07]);
        assertEquals(6, matrix[s07][s01]);
        assertEquals(8, matrix[s02][s01]);
        assertEquals(8, matrix[s01][s02]);
        assertEquals(12, matrix[s07][s08]);
        assertEquals(GraphRepresentations.INF, matrix[s08][s07]);
        assertEquals(3, matrix[s09][s10]);
        assertEquals(3, matrix[s10][s09]);
        assertEquals(8, matrix[s10][s08]);
        assertEquals(9, matrix[s08][s10]);

        for (int i = 0; i < matrix.length; i++) {
            assertEquals(0, matrix[i][i]);
        }
        assertEquals(GraphRepresentations.INF, matrix[s02][s08]);
    }

    @Test
    void incidenceMatrixUsesDocumentedConvention() {
        int[][] matrix = representations.incidenceMatrix();
        int s01 = representations.indexOf("S01");
        int s07 = representations.indexOf("S07");
        int s09 = representations.indexOf("S09");

        // R01 es un arco S01 -> S07
        assertEquals(-1, matrix[s01][0]);
        assertEquals(1, matrix[s07][0]);
        // R03 es una arista no dirigida S01 - S09
        assertEquals(1, matrix[s01][2]);
        assertEquals(1, matrix[s09][2]);
        assertEquals(12, matrix.length);
        assertEquals(18, matrix[0].length);

        for (int column = 0; column < matrix[0].length; column++) {
            int sum = 0;
            for (int row = 0; row < matrix.length; row++) {
                sum += matrix[row][column];
            }
            String edgeId = representations.edgeIds().get(column);
            boolean directed = !edgeId.equals("R03") && !edgeId.equals("R04")
                    && !edgeId.equals("R07") && !edgeId.equals("R08")
                    && !edgeId.equals("R11") && !edgeId.equals("R12")
                    && !edgeId.equals("R13") && !edgeId.equals("R15");
            assertEquals(directed ? 0 : 2, sum, "columna " + edgeId);
        }
    }

    @Test
    void formattedRepresentationsContainHeadersAndSentinel() {
        assertTrue(representations.formatAdjacencyList().contains("LISTA DE ADYACENCIA"));
        assertTrue(representations.formatAdjacencyMatrix().contains("MATRIZ DE ADYACENCIA"));
        assertTrue(representations.formatAdjacencyMatrix().contains("INF"));
        assertTrue(representations.formatIncidenceMatrix().contains("MATRIZ DE INCIDENCIA"));
        assertTrue(representations.formatAdjacencyMatrix().contains("S12"));
    }

    @Test
    void unknownVertexIndexIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> representations.indexOf("S99"));
    }
}
