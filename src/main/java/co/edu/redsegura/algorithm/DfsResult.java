package co.edu.redsegura.algorithm;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Resultado de un recorrido DFS.
 *
 * <p>Incluye tiempos de descubrimiento y finalizacion, padre en el bosque DFS,
 * orden previo y posterior, y el primer ciclo dirigido encontrado (si existe).</p>
 *
 * @param sourceId       vertice desde el que inicia el recorrido
 * @param preOrder       orden de descubrimiento
 * @param postOrder      orden de finalizacion
 * @param parentByVertex padre en el bosque DFS
 * @param discoveryTime  instante de descubrimiento
 * @param finishTime     instante de finalizacion
 * @param cycle          ciclo encontrado, cerrado en su primer vertice; vacio si no hay
 */
public record DfsResult(
        String sourceId,
        List<String> preOrder,
        List<String> postOrder,
        Map<String, String> parentByVertex,
        Map<String, Integer> discoveryTime,
        Map<String, Integer> finishTime,
        List<String> cycle) {

    public DfsResult {
        preOrder = List.copyOf(preOrder);
        postOrder = List.copyOf(postOrder);
        parentByVertex = Collections.unmodifiableMap(new LinkedHashMap<>(parentByVertex));
        discoveryTime = Collections.unmodifiableMap(new LinkedHashMap<>(discoveryTime));
        finishTime = Collections.unmodifiableMap(new LinkedHashMap<>(finishTime));
        cycle = List.copyOf(cycle);
    }

    public boolean hasCycle() {
        return !cycle.isEmpty();
    }

    public boolean reached(String vertexId) {
        return discoveryTime.containsKey(vertexId);
    }
}
