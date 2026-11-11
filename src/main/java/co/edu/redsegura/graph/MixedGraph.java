package co.edu.redsegura.graph;

import co.edu.redsegura.exception.DuplicateIdException;
import co.edu.redsegura.exception.EdgeNotFoundException;
import co.edu.redsegura.exception.InvalidDataException;
import co.edu.redsegura.exception.VertexNotFoundException;
import co.edu.redsegura.model.Edge;
import co.edu.redsegura.model.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Grafo mixto etiquetado y ponderado.
 *
 * <p>Admite aristas no dirigidas (se recorren en ambos sentidos) y arcos
 * dirigidos (solo del origen al destino). Conserva el orden de insercion de
 * vertices y conexiones, de modo que todas las representaciones usan el mismo
 * orden y pueden compararse entre si.</p>
 *
 * <p>La lista de adyacencia contiene, para cada vertice, los arcos que salen de
 * el y las aristas no dirigidas que lo tocan. Una arista no dirigida aparece
 * en las listas de sus dos extremos, pero es el mismo objeto {@link Edge}.</p>
 */
public final class MixedGraph implements Graph {

    private final Map<String, Vertex> vertices = new LinkedHashMap<>();
    private final Map<String, Edge> edges = new LinkedHashMap<>();
    private final Map<String, List<Edge>> adjacency = new LinkedHashMap<>();

    /**
     * Agrega un vertice.
     *
     * @throws DuplicateIdException si el identificador ya existe
     */
    public void addVertex(Vertex vertex) {
        Objects.requireNonNull(vertex, "El vertice es obligatorio");
        if (vertices.containsKey(vertex.getId())) {
            throw new DuplicateIdException(
                    "Ya existe un vertice con el identificador " + vertex.getId());
        }
        vertices.put(vertex.getId(), vertex);
        adjacency.put(vertex.getId(), new ArrayList<>());
    }

    /**
     * Agrega una conexion. Valida que ambos extremos existan y que el
     * identificador de la conexion no este repetido.
     *
     * @throws DuplicateIdException si el identificador de la conexion ya existe
     * @throws VertexNotFoundException si el origen o el destino no existen
     */
    public void addEdge(Edge edge) {
        Objects.requireNonNull(edge, "La conexion es obligatoria");
        if (edges.containsKey(edge.getId())) {
            throw new DuplicateIdException(
                    "Ya existe una conexion con el identificador " + edge.getId());
        }
        requireVertex(edge.getOriginId());
        requireVertex(edge.getDestinationId());
        edges.put(edge.getId(), edge);

        // Un arco dirigido solo se agrega a la lista de su origen.
        adjacency.get(edge.getOriginId()).add(edge);

        // Una arista no dirigida se agrega a los dos extremos,
        // pero se guarda el mismo objeto Edge.
        if (!edge.isDirected()) {
            adjacency.get(edge.getDestinationId()).add(edge);
        }
    }

    /**
     * Elimina definitivamente una conexion.
     *
     * @throws EdgeNotFoundException si la conexion no existe
     */
    public void removeEdge(String edgeId) {
        Edge edge = getEdge(edgeId);
        edges.remove(edgeId);
        adjacency.get(edge.getOriginId()).remove(edge);
        if (!edge.isDirected()) {
            adjacency.get(edge.getDestinationId()).remove(edge);
        }
    }

    /** Bloquea temporalmente una conexion; permanece en el grafo. */
    public void blockEdge(String edgeId) {
        getEdge(edgeId).block();
    }

    /** Reactiva una conexion bloqueada. */
    public void unblockEdge(String edgeId) {
        getEdge(edgeId).unblock();
    }

    /** Busca un vertice o lanza una excepcion con un mensaje claro. */
    public Vertex getVertex(String vertexId) {
        Vertex vertex = findVertex(vertexId);
        if (vertex == null) {
            throw new VertexNotFoundException(
                    "El vertice '" + vertexId + "' no existe en el grafo RedSegura");
        }
        return vertex;
    }

    /** Busca un vertice y retorna {@code null} si no existe. */
    public Vertex findVertex(String vertexId) {
        if (vertexId == null) {
            return null;
        }
        return vertices.get(vertexId.trim());
    }

    /** Busca una conexion o lanza una excepcion con un mensaje claro. */
    public Edge getEdge(String edgeId) {
        Edge edge = findEdge(edgeId);
        if (edge == null) {
            throw new EdgeNotFoundException(
                    "La conexion '" + edgeId + "' no existe en el grafo RedSegura");
        }
        return edge;
    }

    /** Busca una conexion y retorna {@code null} si no existe. */
    public Edge findEdge(String edgeId) {
        if (edgeId == null) {
            return null;
        }
        return edges.get(edgeId.trim());
    }

    public boolean containsVertex(String vertexId) {
        return findVertex(vertexId) != null;
    }

    public boolean containsEdge(String edgeId) {
        return findEdge(edgeId) != null;
    }

    /**
     * Conexiones que salen del vertice o, si son no dirigidas, que lo tocan.
     * Incluye conexiones bloqueadas; los algoritmos las filtran.
     */
    public List<Edge> getNeighbors(String vertexId) {
        requireVertex(vertexId);
        return List.copyOf(adjacency.get(vertexId.trim()));
    }

    /** Conexiones activas (no bloqueadas) del vertice. */
    public List<Edge> getActiveNeighbors(String vertexId) {
        List<Edge> result = new ArrayList<>();
        for (Edge edge : getNeighbors(vertexId)) {
            if (!edge.isBlocked()) {
                result.add(edge);
            }
        }
        return List.copyOf(result);
    }

    /**
     * Devuelve el identificador del otro extremo de una conexion.
     *
     * @throws InvalidDataException si el vertice no es extremo de la conexion
     */
    public String oppositeVertex(Edge edge, String vertexId) {
        Objects.requireNonNull(edge, "La conexion es obligatoria");
        String id = vertexId == null ? null : vertexId.trim();

        // En un arco dirigido solo se puede avanzar desde el origen.
        if (edge.isDirected()) {
            if (edge.getOriginId().equals(id)) {
                return edge.getDestinationId();
            }
            throw new InvalidDataException(
                    "El arco " + edge.getId() + " no sale del vertice " + vertexId);
        }

        // En una arista no dirigida se puede entrar por cualquiera de los dos extremos.
        if (edge.getOriginId().equals(id)) {
            return edge.getDestinationId();
        }
        if (edge.getDestinationId().equals(id)) {
            return edge.getOriginId();
        }
        throw new InvalidDataException(
                "La arista " + edge.getId() + " no incide en el vertice " + vertexId);
    }

    /** Vertices en orden de insercion. */
    public List<Vertex> getVertices() {
        return List.copyOf(vertices.values());
    }

    /** Conexiones en orden de insercion. */
    public List<Edge> getEdges() {
        return List.copyOf(edges.values());
    }

    /** Conexiones activas en orden de insercion. */
    public List<Edge> getActiveEdges() {
        List<Edge> result = new ArrayList<>();
        for (Edge edge : edges.values()) {
            if (!edge.isBlocked()) {
                result.add(edge);
            }
        }
        return List.copyOf(result);
    }

    /** Mapa de solo lectura con los vertices indexados por identificador. */
    public Map<String, Vertex> vertexMap() {
        return Collections.unmodifiableMap(vertices);
    }

    public int vertexCount() {
        return vertices.size();
    }

    public int edgeCount() {
        return edges.size();
    }

    private void requireVertex(String vertexId) {
        getVertex(vertexId);
    }
}
