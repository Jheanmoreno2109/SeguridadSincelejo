package co.edu.redsegura.algorithm;

import co.edu.redsegura.graph.MixedGraph;
import co.edu.redsegura.model.Edge;
import co.edu.redsegura.model.Vertex;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Recorrido en anchura (BFS) con cola y conjunto de visitados.
 *
 * <p>Respeta la orientacion del grafo mixto: los arcos dirigidos solo se
 * recorren del origen al destino; las aristas no dirigidas se recorren en
 * ambos sentidos. Las conexiones bloqueadas se omiten.</p>
 */
public final class BreadthFirstSearch {

    private final MixedGraph graph;

    public BreadthFirstSearch(MixedGraph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("El grafo es obligatorio");
        }
        this.graph = graph;
    }

    /**
     * Ejecuta BFS desde el vertice indicado.
     *
     * @throws co.edu.redsegura.exception.VertexNotFoundException si el vertice no existe
     */
    public BfsResult traverse(String sourceId) {
        Vertex source = graph.getVertex(sourceId);
        String start = source.getId();

        // Estructuras del recorrido.
        Set<String> visited = new LinkedHashSet<>();          // orden de descubrimiento
        Map<String, String> parent = new LinkedHashMap<>();  // árbol BFS
        Map<String, Integer> level = new LinkedHashMap<>();  // nivel de cada vértice
        Map<String, String> treeEdge = new LinkedHashMap<>(); // conexión de descubrimiento
        Deque<String> queue = new ArrayDeque<>();            // cola FIFO

        visited.add(start);
        parent.put(start, null);
        level.put(start, 0);
        queue.addLast(start);

        while (!queue.isEmpty()) {
            // Se atiende primero el vértice que entró primero (FIFO).
            String current = queue.removeFirst();

            for (Edge edge : graph.getNeighbors(current)) {
                if (edge.isBlocked()) {
                    continue;
                }

                String neighbor = graph.oppositeVertex(edge, current);

                // add devuelve true solo si el vecino no estaba visitado.
                if (visited.add(neighbor)) {
                    parent.put(neighbor, current);
                    level.put(neighbor, level.get(current) + 1);
                    treeEdge.put(neighbor, edge.getId());
                    queue.addLast(neighbor);
                }
            }
        }

        return new BfsResult(start, new ArrayList<>(visited), parent, level, treeEdge);
    }
}
