package co.edu.redsegura.model;

import co.edu.redsegura.exception.InvalidDataException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelValidationTest {

    @Test
    void vertexRejectsBlankIdAndLabel() {
        assertThrows(InvalidDataException.class,
                () -> new Vertex("  ", "UAJS", VertexType.UNIVERSIDAD));
        assertThrows(InvalidDataException.class,
                () -> new Vertex("S01", "", VertexType.UNIVERSIDAD));
        assertThrows(NullPointerException.class,
                () -> new Vertex("S01", "UAJS", null));
    }

    @Test
    void vertexEqualityUsesId() {
        Vertex first = new Vertex("S01", "UAJS Sede C", VertexType.UNIVERSIDAD);
        Vertex second = new Vertex("S01", "Otro nombre", VertexType.SALUD);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void edgeRejectsNonPositiveWeights() {
        assertThrows(InvalidDataException.class,
                () -> new Edge("R01", "Prueba", 0, EdgeOrientation.DIRIGIDA, "S01", "S07"));
        assertThrows(InvalidDataException.class,
                () -> new Edge("R01", "Prueba", -4, EdgeOrientation.DIRIGIDA, "S01", "S07"));
    }

    @Test
    void edgeRejectsSelfLoopAndBlankFields() {
        assertThrows(InvalidDataException.class,
                () -> new Edge("R01", "Prueba", 4, EdgeOrientation.NO_DIRIGIDA, "S01", "S01"));
        assertThrows(InvalidDataException.class,
                () -> new Edge("", "Prueba", 4, EdgeOrientation.DIRIGIDA, "S01", "S07"));
        assertThrows(InvalidDataException.class,
                () -> new Edge("R01", " ", 4, EdgeOrientation.DIRIGIDA, "S01", "S07"));
    }

    @Test
    void edgeCanBeBlockedAndRestored() {
        Edge edge = new Edge("R16", "Ruta SETP al terminal", 12,
                EdgeOrientation.DIRIGIDA, "S07", "S08");

        assertFalse(edge.isBlocked());
        edge.block();
        assertTrue(edge.isBlocked());
        edge.unblock();
        assertFalse(edge.isBlocked());
        assertEquals(12, edge.getWeight());
        assertTrue(edge.isDirected());
    }
}
