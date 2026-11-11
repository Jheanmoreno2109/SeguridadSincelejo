package co.edu.redsegura.app;

import co.edu.redsegura.algorithm.BfsResult;
import co.edu.redsegura.algorithm.BreadthFirstSearch;
import co.edu.redsegura.algorithm.DepthFirstSearch;
import co.edu.redsegura.algorithm.DfsResult;
import co.edu.redsegura.algorithm.DijkstraSolver;
import co.edu.redsegura.algorithm.ShortestPathResult;
import co.edu.redsegura.graph.GraphRepresentations;
import co.edu.redsegura.graph.MixedGraph;
import co.edu.redsegura.model.Edge;
import co.edu.redsegura.model.Vertex;
import co.edu.redsegura.tree.AvlTree;
import co.edu.redsegura.tree.BinarySearchTree;
import co.edu.redsegura.tree.SearchResult;

import java.util.List;

/**
 * Fachada de integracion de RedSegura.
 *
 * <p>La aplicacion de consola solo conversa con esta clase. El core construye y
 * posee el {@link MixedGraph}, el {@link BinarySearchTree} y el {@link AvlTree};
 * expone operaciones publicas documentadas y no permite que la vista lea
 * campos privados de los modulos.</p>
 */
public final class RedSeguraCore {

    private final MixedGraph graph;
    private final BinarySearchTree<String, Vertex> bst;
    private final AvlTree<String, Vertex> avl;
    private final BreadthFirstSearch breadthFirstSearch;
    private final DepthFirstSearch depthFirstSearch;
    private final DijkstraSolver dijkstraSolver;

    /** Construye el core con el conjunto de datos oficial del parcial. */
    public RedSeguraCore() {
        this(RedSeguraDataFactory.createGraph());
    }

    /**
     * Construye el core sobre un grafo dado. Los arboles se llenan con los
     * vertices de ese grafo usando la etiqueta como clave.
     */
    public RedSeguraCore(MixedGraph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("El grafo del core es obligatorio");
        }
        this.graph = graph;
        this.bst = new BinarySearchTree<>();
        this.avl = new AvlTree<>();
        for (Vertex vertex : graph.getVertices()) {
            bst.insert(vertex.getLabel(), vertex);
            avl.insert(vertex.getLabel(), vertex);
        }
        this.breadthFirstSearch = new BreadthFirstSearch(graph);
        this.depthFirstSearch = new DepthFirstSearch(graph);
        this.dijkstraSolver = new DijkstraSolver(graph);
    }

    public MixedGraph graph() {
        return graph;
    }

    public List<Vertex> vertices() {
        return graph.getVertices();
    }

    public List<Edge> edges() {
        return graph.getEdges();
    }

    /** Devuelve el vertice o lanza {@link co.edu.redsegura.exception.VertexNotFoundException}. */
    public Vertex vertex(String vertexId) {
        return graph.getVertex(vertexId);
    }

    /** Devuelve la conexion o lanza {@link co.edu.redsegura.exception.EdgeNotFoundException}. */
    public Edge edge(String edgeId) {
        return graph.getEdge(edgeId);
    }

    /** Busca una etiqueta en el ABB y devuelve el vertice con el numero de comparaciones. */
    public SearchResult<Vertex> searchInBst(String label) {
        return bst.search(label);
    }

    /** Busca una etiqueta en el AVL y devuelve el vertice con el numero de comparaciones. */
    public SearchResult<Vertex> searchInAvl(String label) {
        return avl.search(label);
    }

    public int bstHeight() {
        return bst.height();
    }

    public int avlHeight() {
        return avl.height();
    }

    public int bstSize() {
        return bst.size();
    }

    public int avlSize() {
        return avl.size();
    }

    public List<String> bstInOrderLabels() {
        return bst.inOrder().stream().map(entry -> entry.key()).toList();
    }

    public List<String> avlInOrderLabels() {
        return avl.inOrder().stream().map(entry -> entry.key()).toList();
    }

    /** Construye las tres representaciones con el orden de insercion del grafo. */
    public GraphRepresentations representations() {
        return new GraphRepresentations(graph);
    }

    /** Ejecuta BFS desde un vertice; respeta direccion y bloqueos. */
    public BfsResult bfsFrom(String sourceId) {
        return breadthFirstSearch.traverse(sourceId);
    }

    /** Ejecuta DFS desde un vertice; respeta direccion y bloqueos. */
    public DfsResult dfsFrom(String sourceId) {
        return depthFirstSearch.traverse(sourceId);
    }

    /** Indica si existe al menos un ciclo dirigido con las conexiones activas. */
    public boolean hasDirectedCycle() {
        return depthFirstSearch.hasCycle();
    }

    /** Devuelve un ciclo dirigido cerrado o una lista vacia si no hay ciclos. */
    public List<String> findDirectedCycle() {
        return depthFirstSearch.findCycle();
    }

    /** Componentes debiles del grafo subyacente no dirigido. */
    public List<List<String>> weakComponents() {
        return depthFirstSearch.weakComponents();
    }

    /** Ruta de menor peso entre dos vertices. */
    public ShortestPathResult shortestPath(String sourceId, String targetId) {
        return dijkstraSolver.shortestPath(sourceId, targetId);
    }

    /**
     * Calcula una ruta con una conexion temporalmente bloqueada y restaura el
     * estado original aunque ocurra una excepcion.
     */
    public ShortestPathResult shortestPathAvoiding(String blockedEdgeId,
                                                   String sourceId,
                                                   String targetId) {
        Edge edge = graph.getEdge(blockedEdgeId);
        boolean wasBlocked = edge.isBlocked();
        edge.block();
        try {
            return dijkstraSolver.shortestPath(sourceId, targetId);
        } finally {
            if (!wasBlocked) {
                edge.unblock();
            }
        }
    }

    public void blockConnection(String edgeId) {
        graph.blockEdge(edgeId);
    }

    public void unblockConnection(String edgeId) {
        graph.unblockEdge(edgeId);
    }

    /** Hijos de un vertice en el arbol BFS construido desde el origen. */
    public List<String> bfsTreeChildren(String sourceId, String vertexId) {
        return bfsFrom(sourceId).treeChildren().getOrDefault(vertexId, List.of());
    }

    /** Etiqueta legible de un vertice; util para la vista. */
    public String vertexLabel(String vertexId) {
        return graph.getVertex(vertexId).getLabel();
    }

    /** Etiqueta legible de una conexion; util para la vista. */
    public String edgeLabel(String edgeId) {
        return graph.getEdge(edgeId).getLabel();
    }
}
