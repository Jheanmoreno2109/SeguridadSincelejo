package co.edu.redsegura.app;

import co.edu.redsegura.model.Edge;
import co.edu.redsegura.model.EdgeOrientation;
import co.edu.redsegura.model.Vertex;
import co.edu.redsegura.model.VertexType;
import co.edu.redsegura.graph.MixedGraph;
import co.edu.redsegura.tree.AvlTree;
import co.edu.redsegura.tree.BinarySearchTree;

import java.util.List;

/**
 * Fabrica de los datos didacticos del parcial.
 *
 * <p>Centralizar los 12 lugares y las 18 conexiones evita que la aplicacion y
 * las pruebas construyan copias divergentes. Los tiempos son simulados y no
 * representan informacion oficial de transito.</p>
 */
public final class RedSeguraDataFactory {

    private RedSeguraDataFactory() {
    }

    /** Los 12 lugares, en el orden S01..S12 usado por las matrices. */
    public static List<Vertex> vertices() {
        return List.of(
                new Vertex("S01", "UAJS Sede C", VertexType.UNIVERSIDAD),
                new Vertex("S02", "UAJS Sede E", VertexType.UNIVERSIDAD),
                new Vertex("S03", "Universidad de Sucre", VertexType.UNIVERSIDAD),
                new Vertex("S04", "CECAR", VertexType.UNIVERSIDAD),
                new Vertex("S05", "Hospital Universitario", VertexType.SALUD),
                new Vertex("S06", "Clínica Las Américas", VertexType.SALUD),
                new Vertex("S07", "Estación Central SETP", VertexType.TRANSPORTE),
                new Vertex("S08", "Terminal de Transporte", VertexType.TRANSPORTE),
                new Vertex("S09", "Parque Santander", VertexType.PUNTO_ENCUENTRO),
                new Vertex("S10", "Mercado de Sincelejo", VertexType.COMERCIO),
                new Vertex("S11", "PEP Argelia", VertexType.PARADERO),
                new Vertex("S12", "Glorieta Sincelejito", VertexType.MOVILIDAD)
        );
    }

    /** Las 18 conexiones R01..R18, en el orden usado por la incidencia. */
    public static List<Edge> edges() {
        return List.of(
                new Edge("R01", "Corredor universitario", 4, EdgeOrientation.DIRIGIDA, "S01", "S07"),
                new Edge("R02", "Retorno por vía alterna", 6, EdgeOrientation.DIRIGIDA, "S07", "S01"),
                new Edge("R03", "Corredor peatonal", 5, EdgeOrientation.NO_DIRIGIDA, "S01", "S09"),
                new Edge("R04", "Conexión entre sedes", 8, EdgeOrientation.NO_DIRIGIDA, "S02", "S01"),
                new Edge("R05", "Ruta de atención", 10, EdgeOrientation.DIRIGIDA, "S02", "S06"),
                new Edge("R06", "Corredor clínico", 4, EdgeOrientation.DIRIGIDA, "S06", "S05"),
                new Edge("R07", "Vía hospitalaria", 6, EdgeOrientation.NO_DIRIGIDA, "S05", "S09"),
                new Edge("R08", "Centro histórico", 3, EdgeOrientation.NO_DIRIGIDA, "S09", "S10"),
                new Edge("R09", "Salida al terminal", 8, EdgeOrientation.DIRIGIDA, "S10", "S08"),
                new Edge("R10", "Ingreso desde terminal", 9, EdgeOrientation.DIRIGIDA, "S08", "S10"),
                new Edge("R11", "Corredor Puerta Roja", 4, EdgeOrientation.NO_DIRIGIDA, "S03", "S12"),
                new Edge("R12", "Troncal al centro", 7, EdgeOrientation.NO_DIRIGIDA, "S12", "S09"),
                new Edge("R13", "Conexión universitaria", 6, EdgeOrientation.NO_DIRIGIDA, "S04", "S11"),
                new Edge("R14", "Ruta SETP al centro", 8, EdgeOrientation.DIRIGIDA, "S11", "S07"),
                new Edge("R15", "Transferencia central", 3, EdgeOrientation.NO_DIRIGIDA, "S07", "S09"),
                new Edge("R16", "Ruta SETP al terminal", 12, EdgeOrientation.DIRIGIDA, "S07", "S08"),
                new Edge("R17", "Ruta hacia universidad", 7, EdgeOrientation.DIRIGIDA, "S09", "S03"),
                new Edge("R18", "Ruta hacia CECAR", 9, EdgeOrientation.DIRIGIDA, "S12", "S04")
        );
    }

    /** Construye el grafo mixto completo. */
    public static MixedGraph createGraph() {
        MixedGraph graph = new MixedGraph();
        for (Vertex vertex : vertices()) {
            graph.addVertex(vertex);
        }
        for (Edge edge : edges()) {
            graph.addEdge(edge);
        }
        return graph;
    }

    /**
     * Construye un ABB con los lugares como valores y sus etiquetas como claves.
     * El orden de insercion es S01..S12, el mismo del conjunto de datos.
     */
    public static BinarySearchTree<String, Vertex> createBst() {
        BinarySearchTree<String, Vertex> tree = new BinarySearchTree<>();
        for (Vertex vertex : vertices()) {
            tree.insert(vertex.getLabel(), vertex);
        }
        return tree;
    }

    /**
     * Construye un AVL con los lugares como valores y sus etiquetas como claves.
     * El orden de insercion es S01..S12, el mismo del conjunto de datos.
     */
    public static AvlTree<String, Vertex> createAvl() {
        AvlTree<String, Vertex> tree = new AvlTree<>();
        for (Vertex vertex : vertices()) {
            tree.insert(vertex.getLabel(), vertex);
        }
        return tree;
    }
}
