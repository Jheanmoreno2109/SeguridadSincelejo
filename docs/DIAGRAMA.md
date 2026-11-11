# Diagrama de clases y dependencias — RedSegura

## 1. Diagrama de clases simplificado

```mermaid
classDiagram
    direction LR

    class Vertex {
        -String id
        -String label
        -VertexType type
        +getId() String
        +getLabel() String
        +getType() VertexType
    }

    class Edge {
        -String id
        -String label
        -int weight
        -EdgeOrientation orientation
        -String originId
        -String destinationId
        -boolean blocked
        +getWeight() int
        +isDirected() boolean
        +isBlocked() boolean
        +block()
        +unblock()
    }

    class Graph {
        <<interface>>
        +addVertex(Vertex)
        +addEdge(Edge)
        +getNeighbors(String) List
    }

    class MixedGraph {
        -Map vertices
        -Map edges
        -Map adjacency
        +addVertex(Vertex)
        +addEdge(Edge)
        +getVertex(String) Vertex
        +getEdge(String) Edge
        +getNeighbors(String) List
        +getActiveNeighbors(String) List
        +blockEdge(String)
        +unblockEdge(String)
        +oppositeVertex(Edge, String) String
    }

    class GraphRepresentations {
        +adjacencyList() Map
        +weightedAdjacencyMatrix() int[][]
        +incidenceMatrix() int[][]
        +formatAdjacencyList() String
        +formatAdjacencyMatrix() String
        +formatIncidenceMatrix() String
    }

    class BinarySearchTree {
        +insert(K, V)
        +search(K) SearchResult
        +delete(K) boolean
        +height() int
        +inOrder() List
        +preOrder() List
        +postOrder() List
    }

    class AvlTree {
        +insert(K, V)
        +search(K) SearchResult
        +delete(K) boolean
        +height() int
        +isBalanced() boolean
        +rotationLog() List
    }

    class BreadthFirstSearch {
        +traverse(String) BfsResult
    }

    class DepthFirstSearch {
        +traverse(String) DfsResult
        +hasCycle() boolean
        +findCycle() List
        +weakComponents() List
    }

    class DijkstraSolver {
        +shortestPath(String, String) ShortestPathResult
        +distancesFrom(String) Map
    }

    class RedSeguraCore {
        -MixedGraph graph
        -BinarySearchTree bst
        -AvlTree avl
        +searchInBst(String) SearchResult
        +searchInAvl(String) SearchResult
        +shortestPath(String, String) ShortestPathResult
        +shortestPathAvoiding(String, String, String) ShortestPathResult
    }

    class RedSeguraApplication {
        +main(String[])
        +runInteractive()
    }

    class RedSeguraDataFactory {
        +vertices() List
        +edges() List
        +createGraph() MixedGraph
        +createBst() BinarySearchTree
        +createAvl() AvlTree
    }

    RedSeguraApplication --> RedSeguraCore : usa API pública
    RedSeguraCore *-- MixedGraph
    RedSeguraCore *-- BinarySearchTree
    RedSeguraCore *-- AvlTree
    RedSeguraCore ..> BreadthFirstSearch
    RedSeguraCore ..> DepthFirstSearch
    RedSeguraCore ..> DijkstraSolver
    RedSeguraCore ..> GraphRepresentations

    MixedGraph ..|> Graph
    MixedGraph o-- Vertex
    MixedGraph o-- Edge
    GraphRepresentations ..> MixedGraph
    BreadthFirstSearch ..> MixedGraph
    DepthFirstSearch ..> MixedGraph
    DijkstraSolver ..> MixedGraph

    RedSeguraDataFactory ..> Vertex
    RedSeguraDataFactory ..> Edge
    RedSeguraDataFactory ..> MixedGraph
    RedSeguraDataFactory ..> BinarySearchTree
    RedSeguraDataFactory ..> AvlTree
```

## 2. Flujo de dependencias en ejecución

```mermaid
flowchart TD
    MAIN[main] --> APP[RedSeguraApplication]
    APP --> CORE[RedSeguraCore]
    CORE --> FACTORY[RedSeguraDataFactory]
    FACTORY --> GRAPH[MixedGraph]
    FACTORY --> BST[BinarySearchTree]
    FACTORY --> AVL[AvlTree]

    CORE --> GRAPH
    CORE --> BST
    CORE --> AVL
    CORE --> BFS[BreadthFirstSearch]
    CORE --> DFS[DepthFirstSearch]
    CORE --> DIJ[DijkstraSolver]
    CORE --> REP[GraphRepresentations]

    BFS --> GRAPH
    DFS --> GRAPH
    DIJ --> GRAPH
    REP --> GRAPH

    GRAPH --> VERTEX[Vertex]
    GRAPH --> EDGE[Edge]
```

## 3. Regla de encapsulamiento

- `RedSeguraApplication` solo conversa con `RedSeguraCore`.
- Los algoritmos reciben `MixedGraph` por constructor y usan métodos públicos.
- Ninguna clase lee directamente campos privados de otra.
- Los resultados (`BfsResult`, `DfsResult`, `ShortestPathResult`, `SearchResult`) son objetos de solo lectura con listas y mapas copiados.
- `GraphRepresentations` entrega arreglos nuevos en cada llamada, de modo que la vista no modifica el estado interno del grafo.
