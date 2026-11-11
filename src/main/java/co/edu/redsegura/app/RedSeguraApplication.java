package co.edu.redsegura.app;

import co.edu.redsegura.algorithm.BfsResult;
import co.edu.redsegura.algorithm.DfsResult;
import co.edu.redsegura.algorithm.ShortestPathResult;
import co.edu.redsegura.exception.RedSeguraException;
import co.edu.redsegura.exception.VertexNotFoundException;
import co.edu.redsegura.graph.GraphRepresentations;
import co.edu.redsegura.model.Edge;
import co.edu.redsegura.model.Vertex;
import co.edu.redsegura.tree.AvlTree;
import co.edu.redsegura.tree.SearchResult;
import co.edu.redsegura.tree.TreeEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Aplicación de consola RedSegura.
 *
 * <p>Ofrece las siete consultas obligatorias del parcial: búsqueda en ABB y
 * AVL, rotaciones AVL, representaciones del grafo, BFS/DFS, ruta mínima,
 * bloqueo de R16 y consulta de un vértice inexistente.</p>
 *
 * <p>Los errores de dominio se informan y el menú continúa, en particular la
 * consulta Q7.</p>
 */
public final class RedSeguraApplication {

    private static final String HOSPITAL_LABEL = "Hospital Universitario";
    private static final String SEPARATOR = "=".repeat(78);

    private final RedSeguraCore core;
    private final BufferedReader reader;
    private final PrintStream out;

    public RedSeguraApplication(RedSeguraCore core, BufferedReader reader, PrintStream out) {
        if (core == null || reader == null || out == null) {
            throw new IllegalArgumentException("Core, lector y salida son obligatorios");
        }
        this.core = core;
        this.reader = reader;
        this.out = out;
    }

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintStream utf8Out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        new RedSeguraApplication(new RedSeguraCore(), reader, utf8Out).runInteractive();
    }

    /** Menú interactivo; se repite hasta que el usuario elige 0. */
    public void runInteractive() {
        printHeader();
        boolean running = true;
        while (running) {
            printMenu();
            String line = readLine();
            if (line == null) {
                break;
            }
            String option = line.trim();
            if (option.equals("0")) {
                running = false;
                out.println("Fin de la sesión RedSegura.");
                continue;
            }
            executeSafely(option);
        }
    }

    private void executeSafely(String option) {
        try {
            switch (option) {
                case "1" -> queryOne();
                case "2" -> queryTwo();
                case "3" -> queryThree();
                case "4" -> queryFour();
                case "5" -> queryFive();
                case "6" -> querySix();
                case "7" -> querySeven();
                default -> out.println("Opción no válida. Escriba 1 a 7, 0 para salir.");
            }
        } catch (RedSeguraException | IllegalArgumentException exception) {
            out.println("[ERROR CONTROLADO] " + exception.getMessage());
            out.println("La aplicación continúa en ejecución.");
        } catch (RuntimeException exception) {
            out.println("[ERROR INESPERADO] " + exception.getClass().getSimpleName()
                    + ": " + exception.getMessage());
            out.println("La aplicación continúa en ejecución.");
        }
    }

    // ------------------------------------------------------------------
    // Q1: búsqueda en ABB y AVL
    // ------------------------------------------------------------------
    private void queryOne() {
        printQueryHeader("Q1", "Buscar Hospital Universitario en el ABB y en el AVL");
        SearchResult<Vertex> bstResult = core.searchInBst(HOSPITAL_LABEL);
        SearchResult<Vertex> avlResult = core.searchInAvl(HOSPITAL_LABEL);
        printSearchResult("ABB", bstResult);
        printSearchResult("AVL", avlResult);
        out.printf("Altura del ABB: %d niveles; altura del AVL: %d niveles.%n",
                core.bstHeight(), core.avlHeight());
        if (bstResult.comparisons() > avlResult.comparisons()) {
            out.printf("El AVL necesitó %d comparación(es) menos que el ABB.%n",
                    bstResult.comparisons() - avlResult.comparisons());
        } else if (bstResult.comparisons() < avlResult.comparisons()) {
            out.printf("En esta instancia el ABB necesitó %d comparación(es) menos; "
                            + "el AVL garantiza O(log n) en el peor caso.%n",
                    avlResult.comparisons() - bstResult.comparisons());
        } else {
            out.println("Ambos árboles necesitaron la misma cantidad de comparaciones.");
        }
    }

    private void printSearchResult(String treeName, SearchResult<Vertex> result) {
        if (result.found()) {
            Vertex vertex = result.value();
            out.printf("%s: encontrado %s (%s) en %d comparaciones.%n",
                    treeName, vertex.getLabel(), vertex.getId(), result.comparisons());
        } else {
            out.printf("%s: clave no encontrada después de %d comparaciones.%n",
                    treeName, result.comparisons());
        }
    }

    // ------------------------------------------------------------------
    // Q2: rotaciones AVL
    // ------------------------------------------------------------------
    private void queryTwo() {
        printQueryHeader("Q2", "Demostrar las cuatro rotaciones AVL con tres claves");
        record RotationCase(String code, String description, List<Integer> sequence) {
        }
        List<RotationCase> cases = List.of(
                new RotationCase("LL", "Rotación simple a la derecha (izquierda-izquierda)",
                        List.of(30, 20, 10)),
                new RotationCase("RR", "Rotación simple a la izquierda (derecha-derecha)",
                        List.of(10, 20, 30)),
                new RotationCase("LR", "Rotación doble izquierda-derecha",
                        List.of(30, 10, 20)),
                new RotationCase("RL", "Rotación doble derecha-izquierda",
                        List.of(10, 30, 20))
        );

        for (RotationCase rotationCase : cases) {
            AvlTree<Integer, String> tree = new AvlTree<>();
            for (Integer key : rotationCase.sequence()) {
                tree.insert(key, "clave-" + key);
            }
            out.printf("Caso %s: %s%n", rotationCase.code(), rotationCase.description());
            out.printf("  Secuencia insertada: %s%n", formatNumbers(rotationCase.sequence()));
            out.printf("  Rotaciones registradas: %s%n", tree.rotationLog());
            List<Integer> inOrderKeys = new ArrayList<>();
            for (TreeEntry<Integer, String> entry : tree.inOrder()) {
                inOrderKeys.add(entry.key());
            }
            out.printf("  Raíz resultante: %d | Altura: %d | Inorden: %s%n",
                    tree.rootKey(), tree.height(), formatNumbers(inOrderKeys));
            out.println("  El recorrido inorden permanece ordenado.");
        }
    }

    // ------------------------------------------------------------------
    // Q3: representaciones
    // ------------------------------------------------------------------
    private void queryThree() {
        printQueryHeader("Q3", "Lista de adyacencia y matrices de adyacencia e incidencia");
        GraphRepresentations representations = core.representations();
        out.println("Orden de vértices: " + representations.vertexIds());
        out.println("Orden de conexiones: " + representations.edgeIds());
        out.println();
        out.print(representations.formatAdjacencyList());
        out.println();
        out.print(representations.formatAdjacencyMatrix());
        out.println();
        out.print(representations.formatIncidenceMatrix());
        out.println();
        out.println("Convención de incidencia: arco dirigido = -1 en origen y +1 en destino;");
        out.println("arista no dirigida = +1 en sus dos extremos.");
    }

    // ------------------------------------------------------------------
    // Q4: BFS y DFS
    // ------------------------------------------------------------------
    private void queryFour() {
        printQueryHeader("Q4", "Ejecutar BFS y DFS desde S02 y explicar el orden");
        BfsResult bfs = core.bfsFrom("S02");
        DfsResult dfs = core.dfsFrom("S02");

        out.println("BFS desde S02 (cola FIFO, por niveles):");
        out.println("  Orden: " + formatVertices(bfs.visitOrder()));
        out.println("  Niveles: " + formatLevels(bfs.levelByVertex()));
        out.println("  Árbol BFS (padre -> hijos):");
        for (Map.Entry<String, List<String>> entry : bfs.treeChildren().entrySet()) {
            if (!entry.getValue().isEmpty()) {
                out.printf("    %s -> %s%n", entry.getKey(), String.join(", ", entry.getValue()));
            }
        }

        out.println("DFS desde S02 (recursión/pila LIFO, por ramas):");
        out.println("  Preorden: " + formatVertices(dfs.preOrder()));
        out.println("  Postorden: " + formatVertices(dfs.postOrder()));
        out.println("  Ciclo dirigido detectado: "
                + (dfs.hasCycle() ? String.join(" -> ", dfs.cycle()) : "ninguno"));
        List<List<String>> components = core.weakComponents();
        out.printf("  Componentes débiles: %d%n", components.size());
        for (int i = 0; i < components.size(); i++) {
            out.printf("    C%d: %s%n", i + 1, formatVertices(components.get(i)));
        }

        out.println("Por qué puede diferir el orden:");
        out.println("  BFS usa una cola FIFO y descubre primero todos los vecinos de un nivel.");
        out.println("  DFS usa recursión (pila LIFO) y agota una rama antes de retroceder.");
        out.println("  Ambos usan un conjunto de visitados y visitan los mismos vértices alcanzables,");
        out.println("  pero el orden depende de la estrategia y del orden de la lista de adyacencia.");
    }

    // ------------------------------------------------------------------
    // Q5: Dijkstra S02 -> S08
    // ------------------------------------------------------------------
    private void queryFive() {
        printQueryHeader("Q5", "Ruta mínima S02 -> S08 con Dijkstra");
        ShortestPathResult result = core.shortestPath("S02", "S08");
        printPathResult(result);
        if (result.reachable() && result.totalMinutes() == 24) {
            out.println("[OK] Resultado de control cumplido: 24 minutos.");
        } else {
            out.println("[ALERTA] El resultado no coincide con el control de 24 minutos.");
        }
    }

    // ------------------------------------------------------------------
    // Q6: bloquear R16 y recalcular
    // ------------------------------------------------------------------
    private void querySix() {
        printQueryHeader("Q6", "Bloquear R16, recalcular y explicar el cambio");
        Edge blockedEdge = core.edge("R16");
        ShortestPathResult original = core.shortestPath("S02", "S08");
        ShortestPathResult alternative = core.shortestPathAvoiding("R16", "S02", "S08");

        out.printf("Conexión bloqueada: %s (%s -> %s, %d min, %s).%n",
                blockedEdge.getId(), blockedEdge.getOriginId(), blockedEdge.getDestinationId(),
                blockedEdge.getWeight(), blockedEdge.getLabel());
        out.println("Ruta original:");
        printPathResult(original);
        out.println("Ruta con R16 bloqueada:");
        printPathResult(alternative);
        out.println("Explicación:");
        out.printf("  %s era el tramo directo %s -> %s de %d minutos. Al bloquearlo,%n",
                blockedEdge.getId(), blockedEdge.getOriginId(), blockedEdge.getDestinationId(),
                blockedEdge.getWeight());
        out.println("  Dijkstra ya no puede relajar esa conexión y busca un camino alternativo.");
        if (alternative.reachable()) {
            out.println("  Ruta alternativa: " + formatVertices(alternative.vertexPath())
                    + " (" + alternative.totalMinutes() + " min).");
        }
        if (alternative.reachable() && original.reachable()
                && alternative.totalMinutes() == original.totalMinutes()) {
            out.println("  El costo se conserva en " + original.totalMinutes()
                    + " minutos porque existe una ruta alternativa de igual peso.");
            out.println("  Lo que cambia es el conjunto de conexiones utilizado.");
        } else if (alternative.reachable() && original.reachable()) {
            out.println("  El costo pasa de " + original.totalMinutes() + " a "
                    + alternative.totalMinutes() + " minutos.");
        } else {
            out.println("  Sin R16 el destino queda inalcanzable desde el origen.");
        }
        out.println("  La conexión se restaura automáticamente al terminar la consulta.");
    }

    // ------------------------------------------------------------------
    // Q7: vértice inexistente
    // ------------------------------------------------------------------
    private void querySeven() {
        printQueryHeader("Q7", "Consultar un vértice inexistente sin terminar el programa");
        String unknownId = "S99";
        out.println("Consultando el vértice " + unknownId + "...");
        try {
            Vertex vertex = core.vertex(unknownId);
            out.println("Vértice encontrado: " + vertex);
        } catch (VertexNotFoundException exception) {
            out.println("[ERROR CONTROLADO] " + exception.getMessage());
            out.println("La aplicación captura la excepción y continúa; el proceso no termina.");
        }
    }

    // ------------------------------------------------------------------
    // Presentación
    // ------------------------------------------------------------------
    private void printHeader() {
        out.println(SEPARATOR);
        out.println(" REDSEGURA SINCELEJO - Estructuras de Datos II");
        out.println(" Árboles y grafos aplicados a una red de atención de emergencias");
        out.println(" Datos simulados con fines académicos; no son información oficial.");
        out.println(SEPARATOR);
    }

    private void printMenu() {
        out.println();
        out.println("Consultas obligatorias:");
        out.println("  1. Q1: buscar Hospital Universitario en ABB y AVL");
        out.println("  2. Q2: demostrar rotaciones LL, RR, LR y RL");
        out.println("  3. Q3: lista de adyacencia y matrices de adyacencia e incidencia");
        out.println("  4. Q4: BFS y DFS desde S02");
        out.println("  5. Q5: Dijkstra de S02 a S08");
        out.println("  6. Q6: bloquear R16 y recalcular la ruta");
        out.println("  7. Q7: consultar un vértice inexistente");
        out.println("  0. Salir");
        out.print("Seleccione una opción: ");
    }

    private void printQueryHeader(String code, String description) {
        out.println();
        out.println(SEPARATOR);
        out.println(code + ": " + description);
        out.println(SEPARATOR);
    }

    private void printPathResult(ShortestPathResult result) {
        if (!result.reachable()) {
            out.printf("  No existe ruta entre %s y %s.%n", result.sourceId(), result.targetId());
            return;
        }
        out.printf("  Tiempo total: %d minutos%n", result.totalMinutes());
        out.println("  Ruta: " + formatVertices(result.vertexPath()));
        out.println("  Conexiones: " + String.join(" -> ", result.edgePath()));
    }

    private String formatVertices(List<String> vertexIds) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < vertexIds.size(); i++) {
            if (i > 0) {
                builder.append(" -> ");
            }
            String id = vertexIds.get(i);
            builder.append(id).append(" (").append(core.vertexLabel(id)).append(")");
        }
        return builder.toString();
    }

    private String formatLevels(Map<String, Integer> levels) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Integer> entry : levels.entrySet()) {
            if (!first) {
                builder.append(", ");
            }
            builder.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }
        return builder.toString();
    }

    private String formatNumbers(List<Integer> numbers) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < numbers.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(numbers.get(i));
        }
        return builder.toString();
    }

    private String readLine() {
        try {
            return reader.readLine();
        } catch (IOException exception) {
            out.println("[ERROR DE ENTRADA] " + exception.getMessage());
            return null;
        }
    }
}
