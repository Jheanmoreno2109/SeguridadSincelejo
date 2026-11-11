package co.edu.redsegura.graph;

import co.edu.redsegura.model.Edge;
import co.edu.redsegura.model.Vertex;

import java.util.List;

/**
 * Contrato común de un grafo etiquetado y ponderado.
 *
 * <p>Permite que las representaciones y los algoritmos dependan de una
 * abstracción y no de una implementación concreta. {@link MixedGraph} es la
 * implementación usada por RedSegura.</p>
 */
public interface Graph {

    /** Agrega un vértice al grafo. */
    void addVertex(Vertex vertex);

    /** Agrega una conexión entre dos vértices existentes. */
    void addEdge(Edge edge);

    /** Devuelve el vértice o lanza una excepción si no existe. */
    Vertex getVertex(String vertexId);

    /** Devuelve la conexión o lanza una excepción si no existe. */
    Edge getEdge(String edgeId);

    /** Vértices en orden de inserción. */
    List<Vertex> getVertices();

    /** Conexiones en orden de inserción. */
    List<Edge> getEdges();

    /**
     * Conexiones que salen del vértice o, si son no dirigidas, que lo tocan.
     */
    List<Edge> getNeighbors(String vertexId);

    /** Indica si el vértice pertenece al grafo. */
    boolean containsVertex(String vertexId);

    /** Cantidad de vértices. */
    int vertexCount();

    /** Cantidad de conexiones. */
    int edgeCount();
}
