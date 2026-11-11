package co.edu.redsegura.graph;

import co.edu.redsegura.model.Edge;
import co.edu.redsegura.model.EdgeOrientation;
import co.edu.redsegura.model.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Representaciones clasicas del grafo mixto:
 *
 * <ul>
 *   <li>Lista de adyacencia con vecino, conexion, peso y orientacion.</li>
 *   <li>Matriz de adyacencia ponderada. {@code INF} indica ausencia de
 *       conexion y la diagonal vale 0. Si existieran dos conexiones en el
 *       mismo sentido entre un par de vertices, se muestra el menor peso.</li>
 *   <li>Matriz de incidencia {@code V x E}: una fila por vertice y una columna
 *       por conexion. Convencion: arco dirigido = -1 en el origen y +1 en el
 *       destino; arista no dirigida = +1 en ambos extremos.</li>
 * </ul>
 *
 * <p>Todas las representaciones respetan el orden de insercion del
 * {@link MixedGraph}, por lo que las filas y columnas son comparables.</p>
 */
public final class GraphRepresentations {

    /** Valor usado en la matriz de adyacencia cuando no hay conexion. */
    public static final int INF = Integer.MAX_VALUE;

    /**
     * Entrada de la lista de adyacencia.
     *
     * @param vertexId    vecino alcanzable
     * @param edgeId      conexion utilizada
     * @param weight      peso en minutos
     * @param orientation orientacion de la conexion
     * @param blocked     si la conexion esta bloqueada
     */
    public record Neighbor(String vertexId, String edgeId, int weight,
                           EdgeOrientation orientation, boolean blocked) {
    }

    private final List<Vertex> vertices;
    private final List<Edge> edges;
    private final Map<String, Integer> vertexIndex;

    public GraphRepresentations(MixedGraph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("El grafo es obligatorio");
        }
        this.vertices = graph.getVertices();
        this.edges = graph.getEdges();
        this.vertexIndex = new LinkedHashMap<>();
        for (int i = 0; i < vertices.size(); i++) {
            vertexIndex.put(vertices.get(i).getId(), i);
        }
    }

    /** Identificadores de vertices en el orden usado por las matrices. */
    public List<String> vertexIds() {
        List<String> ids = new ArrayList<>(vertices.size());
        for (Vertex vertex : vertices) {
            ids.add(vertex.getId());
        }
        return List.copyOf(ids);
    }

    /** Identificadores de conexiones en el orden usado por la incidencia. */
    public List<String> edgeIds() {
        List<String> ids = new ArrayList<>(edges.size());
        for (Edge edge : edges) {
            ids.add(edge.getId());
        }
        return List.copyOf(ids);
    }

    /**
     * Lista de adyacencia indexada por vertice de origen (o extremo, si la
     * arista es no dirigida).
     */
    public Map<String, List<Neighbor>> adjacencyList() {
        Map<String, List<Neighbor>> result = new LinkedHashMap<>();
        for (Vertex vertex : vertices) {
            result.put(vertex.getId(), new ArrayList<>());
        }
        for (Edge edge : edges) {
            Neighbor forward = new Neighbor(edge.getDestinationId(), edge.getId(),
                    edge.getWeight(), edge.getOrientation(), edge.isBlocked());
            result.get(edge.getOriginId()).add(forward);
            if (!edge.isDirected()) {
                Neighbor backward = new Neighbor(edge.getOriginId(), edge.getId(),
                        edge.getWeight(), edge.getOrientation(), edge.isBlocked());
                result.get(edge.getDestinationId()).add(backward);
            }
        }
        Map<String, List<Neighbor>> immutable = new LinkedHashMap<>();
        for (Map.Entry<String, List<Neighbor>> entry : result.entrySet()) {
            immutable.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        return Collections.unmodifiableMap(immutable);
    }

    /**
     * Matriz de adyacencia ponderada de {@code n x n}.
     * Las filas representan el origen y las columnas el destino.
     */
    public int[][] weightedAdjacencyMatrix() {
        int vertexCount = vertices.size();
        int[][] matrix = new int[vertexCount][vertexCount];

        // Diagonal en 0 y resto en INF (sin conexión directa).
        for (int row = 0; row < vertexCount; row++) {
            for (int column = 0; column < vertexCount; column++) {
                matrix[row][column] = row == column ? 0 : INF;
            }
        }

        // Cada conexión ocupa la celda origen-destino.
        for (Edge edge : edges) {
            int origin = indexOf(edge.getOriginId());
            int destination = indexOf(edge.getDestinationId());
            matrix[origin][destination] =
                    minimum(matrix[origin][destination], edge.getWeight());

            // Si es no dirigida, también se marca el sentido contrario.
            if (!edge.isDirected()) {
                matrix[destination][origin] =
                        minimum(matrix[destination][origin], edge.getWeight());
            }
        }
        return matrix;
    }

    /**
     * Matriz de incidencia de {@code V x E}: filas = vertices, columnas =
     * conexiones. Arco dirigido: -1 en origen, +1 en destino. Arista no
     * dirigida: +1 en sus dos extremos.
     */
    public int[][] incidenceMatrix() {
        int[][] matrix = new int[vertices.size()][edges.size()];

        // Cada columna representa una conexión.
        for (int column = 0; column < edges.size(); column++) {
            Edge edge = edges.get(column);
            int origin = indexOf(edge.getOriginId());
            int destination = indexOf(edge.getDestinationId());

            if (edge.isDirected()) {
                // Arco: -1 en el origen y +1 en el destino.
                matrix[origin][column] = -1;
                matrix[destination][column] = 1;
            } else {
                // Arista no dirigida: +1 en sus dos extremos.
                matrix[origin][column] = 1;
                matrix[destination][column] = 1;
            }
        }
        return matrix;
    }

    /** Indice de un vertice en las matrices. */
    public int indexOf(String vertexId) {
        Integer index = vertexIndex.get(vertexId);
        if (index == null) {
            throw new IllegalArgumentException("El vertice '" + vertexId + "' no pertenece al grafo");
        }
        return index;
    }

    /** Lista de adyacencia en texto legible. */
    public String formatAdjacencyList() {
        StringBuilder builder = new StringBuilder();
        builder.append("LISTA DE ADYACENCIA").append(System.lineSeparator());
        Map<String, List<Neighbor>> adjacency = adjacencyList();
        for (Vertex vertex : vertices) {
            builder.append(vertex.getId()).append(" ").append(vertex.getLabel()).append(" -> ");
            List<Neighbor> neighbors = adjacency.get(vertex.getId());
            if (neighbors.isEmpty()) {
                builder.append("(sin conexiones)");
            } else {
                for (int i = 0; i < neighbors.size(); i++) {
                    Neighbor neighbor = neighbors.get(i);
                    if (i > 0) {
                        builder.append("; ");
                    }
                    builder.append(neighbor.vertexId())
                            .append(" [").append(neighbor.edgeId())
                            .append(", ").append(neighbor.weight()).append(" min, ")
                            .append(neighbor.orientation() == EdgeOrientation.DIRIGIDA
                                    ? "dirigida" : "no dirigida")
                            .append(neighbor.blocked() ? ", BLOQUEADA" : "")
                            .append("]");
                }
            }
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }

    /** Matriz de adyacencia ponderada en texto legible. */
    public String formatAdjacencyMatrix() {
        int[][] matrix = weightedAdjacencyMatrix();
        int width = 5;
        StringBuilder builder = new StringBuilder("MATRIZ DE ADYACENCIA (filas=origen, columnas=destino; INF=sin conexion)")
                .append(System.lineSeparator());
        builder.append(pad("", width + 1));
        for (Vertex vertex : vertices) {
            builder.append(pad(vertex.getId(), width));
        }
        builder.append(System.lineSeparator());
        for (int i = 0; i < vertices.size(); i++) {
            builder.append(pad(vertices.get(i).getId(), width + 1));
            for (int j = 0; j < vertices.size(); j++) {
                String text = matrix[i][j] == INF ? "INF" : String.valueOf(matrix[i][j]);
                builder.append(pad(text, width));
            }
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }

    /** Matriz de incidencia en texto legible. */
    public String formatIncidenceMatrix() {
        int[][] matrix = incidenceMatrix();
        int width = 5;
        StringBuilder builder = new StringBuilder("MATRIZ DE INCIDENCIA (filas=vertices, columnas=conexiones; -1=origen, +1=destino o extremo no dirigido)")
                .append(System.lineSeparator());
        builder.append(pad("", width + 1));
        for (Edge edge : edges) {
            builder.append(pad(edge.getId(), width));
        }
        builder.append(System.lineSeparator());
        for (int row = 0; row < vertices.size(); row++) {
            builder.append(pad(vertices.get(row).getId(), width + 1));
            for (int column = 0; column < edges.size(); column++) {
                builder.append(pad(String.valueOf(matrix[row][column]), width));
            }
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }

    private static int minimum(int current, int candidate) {
        return Math.min(current, candidate);
    }

    private static String pad(String text, int width) {
        StringBuilder builder = new StringBuilder();
        builder.append(text);
        while (builder.length() < width) {
            builder.insert(0, ' ');
        }
        return builder.toString();
    }
}
