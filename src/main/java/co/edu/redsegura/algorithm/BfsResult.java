package co.edu.redsegura.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Resultado de un recorrido BFS.
 *
 * <p>El arbol BFS queda descrito por el mapa de padres y por la conexion usada
 * para descubrir cada vertice. El vertice origen figura con padre {@code null}.</p>
 *
 * @param sourceId        vertice de inicio
 * @param visitOrder      orden de descubrimiento
 * @param parentByVertex  padre de cada vertice en el arbol BFS
 * @param levelByVertex   nivel o distancia en numero de conexiones
 * @param treeEdgeByVertex conexion usada para descubrir cada vertice
 */
public record BfsResult(
        String sourceId,
        List<String> visitOrder,
        Map<String, String> parentByVertex,
        Map<String, Integer> levelByVertex,
        Map<String, String> treeEdgeByVertex) {

    public BfsResult {
        visitOrder = List.copyOf(visitOrder);
        parentByVertex = Collections.unmodifiableMap(new LinkedHashMap<>(parentByVertex));
        levelByVertex = Collections.unmodifiableMap(new LinkedHashMap<>(levelByVertex));
        treeEdgeByVertex = Collections.unmodifiableMap(new LinkedHashMap<>(treeEdgeByVertex));
    }

    public boolean reached(String vertexId) {
        return levelByVertex.containsKey(vertexId);
    }

    public int levelOf(String vertexId) {
        Integer level = levelByVertex.get(vertexId);
        return level == null ? -1 : level;
    }

    /**
     * Reconstruye el camino desde el origen hasta el destino usando los padres
     * del arbol BFS. Devuelve una lista vacia si el destino no fue alcanzado.
     */
    public List<String> pathTo(String vertexId) {
        if (!reached(vertexId)) {
            return List.of();
        }
        List<String> reverse = new ArrayList<>();
        String current = vertexId;
        while (current != null) {
            reverse.add(current);
            current = parentByVertex.get(current);
        }
        Collections.reverse(reverse);
        return List.copyOf(reverse);
    }

    /** Hijos de cada vertice en el arbol BFS. */
    public Map<String, List<String>> treeChildren() {
        Map<String, List<String>> children = new LinkedHashMap<>();
        for (String vertexId : visitOrder) {
            children.put(vertexId, new ArrayList<>());
        }
        for (Map.Entry<String, String> entry : parentByVertex.entrySet()) {
            if (entry.getValue() != null) {
                children.get(entry.getValue()).add(entry.getKey());
            }
        }
        Map<String, List<String>> immutable = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : children.entrySet()) {
            immutable.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        return Collections.unmodifiableMap(immutable);
    }

    /** Vertices visitados como conjunto ordenado. */
    public Set<String> visitedVertices() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(visitOrder));
    }
}
