package co.edu.redsegura.algorithm;

import java.util.List;

/**
 * Ruta minima calculada por Dijkstra.
 *
 * @param sourceId     vertice de origen
 * @param targetId     vertice de destino
 * @param totalMinutes suma de pesos; {@link Integer#MAX_VALUE} si no hay ruta
 * @param vertexPath   vertices en orden, incluidos origen y destino
 * @param edgePath     conexiones usadas, en el mismo orden que los tramos
 */
public record ShortestPathResult(
        String sourceId,
        String targetId,
        int totalMinutes,
        List<String> vertexPath,
        List<String> edgePath) {

    public ShortestPathResult {
        vertexPath = List.copyOf(vertexPath);
        edgePath = List.copyOf(edgePath);
    }

    public boolean reachable() {
        return !vertexPath.isEmpty();
    }

    /** Ruta legible, por ejemplo {@code S02 -> S01 -> S07 -> S08}. */
    public String formattedPath() {
        return reachable() ? String.join(" -> ", vertexPath) : "(sin ruta)";
    }

    public static ShortestPathResult unreachable(String sourceId, String targetId) {
        return new ShortestPathResult(sourceId, targetId, Integer.MAX_VALUE, List.of(), List.of());
    }
}
