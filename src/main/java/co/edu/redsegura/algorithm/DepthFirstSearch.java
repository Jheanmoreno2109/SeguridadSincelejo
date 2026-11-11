package co.edu.redsegura.algorithm;

import co.edu.redsegura.graph.MixedGraph;
import co.edu.redsegura.model.Edge;
import co.edu.redsegura.model.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Recorrido en profundidad (DFS) recursivo con control de visitados y colores.
 *
 * <p>Los colores representan el estado de cada vértice:</p>
 * <ul>
 *   <li>BLANCO: todavía no se ha visitado.</li>
 *   <li>GRIS: se está visitando y forma parte del camino actual.</li>
 *   <li>NEGRO: ya terminó su visita.</li>
 * </ul>
 *
 * <p>Si desde un vértice se llega a otro que está GRIS, se encontró una arista
 * de retroceso, es decir, un ciclo dirigido.</p>
 *
 * <p>También calcula las componentes débiles: grupos de vértices conectados si
 * se ignorara la orientación de las conexiones.</p>
 */
public final class DepthFirstSearch {

    private static final int WHITE = 0;
    private static final int GRAY = 1;
    private static final int BLACK = 2;

    /**
     * Estado compartido por toda la exploración DFS.
     *
     * <p>Agrupar estos mapas y listas evita pasar diez parámetros a cada
     * llamada recursiva y hace más fácil seguir el algoritmo.</p>
     */
    private static final class DfsState {
        private final Map<String, Integer> color = new HashMap<>();
        private final Map<String, String> parent = new LinkedHashMap<>();
        private final Map<String, Integer> discoveryTime = new LinkedHashMap<>();
        private final Map<String, Integer> finishTime = new LinkedHashMap<>();
        private final List<String> preOrder = new ArrayList<>();
        private final List<String> postOrder = new ArrayList<>();
        private final List<String> currentPath = new ArrayList<>();
        private final List<String> cycle = new ArrayList<>();
        private int time;
    }

    private final MixedGraph graph;

    public DepthFirstSearch(MixedGraph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("El grafo es obligatorio");
        }
        this.graph = graph;
    }

    /** DFS desde un vértice, respetando la orientación de las conexiones. */
    public DfsResult traverse(String sourceId) {
        Vertex source = graph.getVertex(sourceId);
        return explore(List.of(source.getId()));
    }

    /** Indica si el grafo dirigido contiene al menos un ciclo. */
    public boolean hasCycle() {
        return !findCycle().isEmpty();
    }

    /** Devuelve un ciclo dirigido cerrado o una lista vacía si el grafo es acíclico. */
    public List<String> findCycle() {
        List<String> roots = new ArrayList<>();
        for (Vertex vertex : graph.getVertices()) {
            roots.add(vertex.getId());
        }
        return explore(roots).cycle();
    }

    /**
     * Componentes débiles del grafo: se recorren todas las conexiones como si
     * fueran no dirigidas. Incluye conexiones bloqueadas porque el bloqueo es
     * una condición temporal de movilidad, no un cambio estructural.
     */
    public List<List<String>> weakComponents() {
        // Primero se construye una vecindad que ignora la orientación.
        Map<String, List<String>> undirectedAdjacency = new LinkedHashMap<>();
        for (Vertex vertex : graph.getVertices()) {
            undirectedAdjacency.put(vertex.getId(), new ArrayList<>());
        }
        for (Edge edge : graph.getEdges()) {
            undirectedAdjacency.get(edge.getOriginId()).add(edge.getDestinationId());
            undirectedAdjacency.get(edge.getDestinationId()).add(edge.getOriginId());
        }

        Set<String> visited = new LinkedHashSet<>();
        List<List<String>> components = new ArrayList<>();

        // Cada vértice no visitado inicia una nueva componente.
        for (Vertex vertex : graph.getVertices()) {
            if (!visited.add(vertex.getId())) {
                continue;
            }

            List<String> component = new ArrayList<>();
            List<String> pending = new ArrayList<>();
            pending.add(vertex.getId());

            while (!pending.isEmpty()) {
                String current = pending.remove(pending.size() - 1);
                component.add(current);

                List<String> neighbors = undirectedAdjacency.get(current);
                for (int i = neighbors.size() - 1; i >= 0; i--) {
                    String neighbor = neighbors.get(i);
                    if (visited.add(neighbor)) {
                        pending.add(neighbor);
                    }
                }
            }

            components.add(List.copyOf(component));
        }

        return List.copyOf(components);
    }

    /**
     * Prepara el estado y explora desde todas las raíces indicadas.
     */
    private DfsResult explore(List<String> roots) {
        DfsState state = new DfsState();

        // Al inicio todos los vértices están sin visitar.
        for (Vertex vertex : graph.getVertices()) {
            state.color.put(vertex.getId(), WHITE);
        }

        // Si una raíz ya fue visitada, no se repite.
        for (String root : roots) {
            if (state.color.get(root) == WHITE) {
                dfsVisit(root, state);
            }
        }

        return new DfsResult(roots.isEmpty() ? "" : roots.get(0),
                state.preOrder, state.postOrder, state.parent,
                state.discoveryTime, state.finishTime, state.cycle);
    }

    /**
     * Visita un vértice y, recursivamente, todos sus vecinos alcanzables.
     */
    private void dfsVisit(String vertexId, DfsState state) {
        // Se marca GRIS: está en el camino actual.
        state.color.put(vertexId, GRAY);
        state.time++;
        state.discoveryTime.put(vertexId, state.time);
        state.preOrder.add(vertexId);
        state.currentPath.add(vertexId);

        for (Edge edge : graph.getNeighbors(vertexId)) {
            if (edge.isBlocked()) {
                continue;
            }

            String neighbor = graph.oppositeVertex(edge, vertexId);
            int neighborColor = state.color.get(neighbor);

            if (neighborColor == WHITE) {
                // Vecino nuevo: se continúa por esa rama.
                state.parent.put(neighbor, vertexId);
                dfsVisit(neighbor, state);
            } else if (neighborColor == GRAY && state.cycle.isEmpty()) {
                // Vecino gris: se cerró un ciclo con el camino actual.
                int cycleStart = state.currentPath.indexOf(neighbor);
                if (cycleStart >= 0) {
                    state.cycle.addAll(
                            state.currentPath.subList(cycleStart, state.currentPath.size()));
                    state.cycle.add(neighbor);
                }
            }
        }

        // Se termina la visita: ya no está en el camino y queda NEGRO.
        state.currentPath.remove(state.currentPath.size() - 1);
        state.color.put(vertexId, BLACK);
        state.time++;
        state.finishTime.put(vertexId, state.time);
        state.postOrder.add(vertexId);
    }

    /** Vista de solo lectura de los vértices en el orden del grafo. */
    public List<String> vertexIds() {
        List<String> ids = new ArrayList<>();
        for (Vertex vertex : graph.getVertices()) {
            ids.add(vertex.getId());
        }
        return Collections.unmodifiableList(ids);
    }
}
