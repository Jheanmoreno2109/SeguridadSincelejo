package co.edu.redsegura.algorithm;

import co.edu.redsegura.graph.MixedGraph;
import co.edu.redsegura.model.Edge;
import co.edu.redsegura.model.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Ruta mínima con el algoritmo de Dijkstra.
 *
 * <p>La idea del algoritmo es sencilla:</p>
 * <ol>
 *   <li>Todos los vértices empiezan con distancia infinita, menos el origen,
 *       que empieza en 0.</li>
 *   <li>Se saca de la cola de prioridad el vértice con menor distancia
 *       conocida.</li>
 *   <li>Se revisan sus conexiones activas: si llegar a un vecino por esa
 *       conexión es más barato que la distancia que ya tenía, se actualiza.</li>
 *   <li>Al final se reconstruye el camino usando los predecesores guardados.</li>
 * </ol>
 *
 * <p>Se respeta la orientación del grafo mixto y se omiten las conexiones
 * bloqueadas. Los pesos son positivos, condición validada por {@link Edge}.</p>
 *
 * <p>Complejidad temporal: O((V + E) log V) con montículo binario.
 * Complejidad espacial: O(V + E).</p>
 */
public final class DijkstraSolver {

    /** Distancia que representa "no hay camino conocido". */
    private static final int INF = Integer.MAX_VALUE;

    /** Entrada de la cola de prioridad: un vértice y su distancia tentativa. */
    private record QueueNode(String vertexId, int distance) {
    }

    /**
     * Estado interno del cálculo.
     *
     * <p>Guarda la mejor distancia conocida a cada vértice y los predecesores
     * necesarios para reconstruir la ruta.</p>
     */
    private static final class SearchState {
        private final Map<String, Integer> distances = new HashMap<>();
        private final Map<String, String> previousVertex = new HashMap<>();
        private final Map<String, String> previousEdge = new HashMap<>();
    }

    private final MixedGraph graph;

    public DijkstraSolver(MixedGraph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("El grafo es obligatorio");
        }
        this.graph = graph;
    }

    /**
     * Calcula la ruta de menor peso entre dos vértices.
     *
     * @throws co.edu.redsegura.exception.VertexNotFoundException si alguno de
     *         los vértices no existe
     */
    public ShortestPathResult shortestPath(String sourceId, String targetId) {
        String source = graph.getVertex(sourceId).getId();
        String target = graph.getVertex(targetId).getId();

        SearchState state = runDijkstra(source);

        if (state.distances.get(target) == INF) {
            return ShortestPathResult.unreachable(source, target);
        }

        // Se retrocede desde el destino hasta el origen guardando cada tramo.
        LinkedList<String> vertices = new LinkedList<>();
        LinkedList<String> edges = new LinkedList<>();
        String cursor = target;
        vertices.addFirst(cursor);

        while (!cursor.equals(source)) {
            String previous = state.previousVertex.get(cursor);
            if (previous == null) {
                return ShortestPathResult.unreachable(source, target);
            }
            edges.addFirst(state.previousEdge.get(cursor));
            vertices.addFirst(previous);
            cursor = previous;
        }

        return new ShortestPathResult(source, target, state.distances.get(target),
                new ArrayList<>(vertices), new ArrayList<>(edges));
    }

    /**
     * Distancias mínimas desde un origen a todos los vértices alcanzables.
     * Los vértices inalcanzables aparecen con {@link Integer#MAX_VALUE}.
     */
    public Map<String, Integer> distancesFrom(String sourceId) {
        String source = graph.getVertex(sourceId).getId();
        SearchState state = runDijkstra(source);
        return Collections.unmodifiableMap(state.distances);
    }

    /**
     * Ejecuta el núcleo de Dijkstra desde un origen ya validado.
     */
    private SearchState runDijkstra(String source) {
        SearchState state = new SearchState();

        // Al principio nadie es alcanzable, excepto el origen.
        for (Vertex vertex : graph.getVertices()) {
            state.distances.put(vertex.getId(), INF);
        }
        state.distances.put(source, 0);

        // La cola siempre entrega primero la distancia más pequeña.
        PriorityQueue<QueueNode> queue = new PriorityQueue<>(
                Comparator.comparingInt(QueueNode::distance)
                        .thenComparing(QueueNode::vertexId));
        queue.add(new QueueNode(source, 0));

        while (!queue.isEmpty()) {
            QueueNode current = queue.poll();
            int knownDistance = state.distances.get(current.vertexId());

            // Si la entrada quedó vieja, se ignora.
            if (current.distance() != knownDistance) {
                continue;
            }

            // Relajación: probar si se mejora la distancia de cada vecino.
            for (Edge edge : graph.getNeighbors(current.vertexId())) {
                if (edge.isBlocked()) {
                    continue;
                }
                String neighbor = graph.oppositeVertex(edge, current.vertexId());
                int candidateDistance = knownDistance + edge.getWeight();

                if (candidateDistance < state.distances.get(neighbor)) {
                    state.distances.put(neighbor, candidateDistance);
                    state.previousVertex.put(neighbor, current.vertexId());
                    state.previousEdge.put(neighbor, edge.getId());
                    queue.add(new QueueNode(neighbor, candidateDistance));
                }
            }
        }

        return state;
    }
}
