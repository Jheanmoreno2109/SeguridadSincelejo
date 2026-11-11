# Informe breve — RedSegura Sincelejo

**Parcial 2 de Estructuras de Datos II**
**Estudiante:** _(completar)_
**Docente:** Jaider Enrique Reyes Herazo
**Periodo:** 2026

---

## 1. Introducción

RedSegura modela 12 lugares de Sincelejo y 18 conexiones de movilidad. El sistema permite buscar lugares en un ABB y en un AVL, consultar un grafo mixto, obtener sus representaciones, recorrerlo con BFS/DFS y calcular rutas mínimas con Dijkstra. La aplicación de consola integra todos los módulos mediante `RedSeguraCore` y ofrece las siete consultas obligatorias.

Los tiempos son datos didácticos de simulación y no representan información oficial de tránsito o respuesta a emergencias.

---

## 2. Decisiones de diseño

### 2.1 Modelo

- `Vertex` es inmutable y valida ID, etiqueta y tipo. La igualdad se define por ID.
- `Edge` conserva ID, etiqueta, peso entero en minutos, orientación y extremos. Rechaza pesos menores o iguales a cero, IDs vacíos y autolazos.
- El estado de bloqueo se modela con `block()`/`unblock()` para no perder la información de la conexión al simular un cierre.
- Las excepciones de dominio (`DuplicateIdException`, `VertexNotFoundException`, `EdgeNotFoundException`, `InvalidDataException`) permiten mensajes claros y un manejo uniforme en la CLI.

### 2.2 ABB y AVL

- Ambas estructuras son genéricas: `<K extends Comparable<K>, V>`.
- Se usan con la etiqueta del lugar como clave y el `Vertex` como valor; así Q1 busca exactamente "Hospital Universitario".
- El ABB no permite claves duplicadas.
- El AVL conserva la altura en cada nodo y actualiza el factor de balance tras insertar o eliminar. Una sola rutina `rebalance` distingue los casos LL, RR, LR y RL usando el factor del hijo.
- Las rotaciones se registran en una lista observable para la demostración Q2.
- La altura se mide en niveles: vacío = 0, hoja = 1.
- Los recorridos se implementan con pilas explícitas para evitar problemas de pila en árboles degenerados.

### 2.3 Grafo mixto

- `MixedGraph` usa `LinkedHashMap` para preservar el orden de inserción.
- Un arco dirigido se guarda solo en la adyacencia del origen; una arista no dirigida se guarda en ambos extremos usando el mismo objeto `Edge`.
- Se validan IDs duplicados, vértices inexistentes y conexiones inexistentes.
- `oppositeVertex` valida que un arco se recorra únicamente desde su origen.
- Las conexiones bloqueadas permanecen en el grafo, pero BFS, DFS y Dijkstra las omiten.

### 2.4 Representaciones

- **Lista de adyacencia:** vecino, ID de conexión, peso, orientación y estado de bloqueo.
- **Matriz de adyacencia ponderada:** `n × n`, diagonal 0, `INF` si no hay conexión. Una arista no dirigida produce simetría; un arco dirigido solo ocupa la celda origen-destino.
- **Matriz de incidencia:** `V × E` (filas = vértices, columnas = conexiones). Arco dirigido: origen `-1`, destino `+1`. Arista no dirigida: `+1` en ambos extremos. Las columnas suman 0 para arcos y 2 para aristas no dirigidas.
- Las tres representaciones respetan el mismo orden de vértices y conexiones.

### 2.5 BFS, DFS y Dijkstra

- **BFS:** cola `ArrayDeque`, conjunto de visitados, mapa de padres, niveles y conexión de descubrimiento. El mapa de padres y la conexión usada constituyen el árbol BFS.
- **DFS:** recursivo con colores blanco/gris/negro, tiempos de descubrimiento y finalización. Una arista hacia un nodo gris produce un ciclo dirigido. Las componentes débiles se calculan ignorando la orientación.
- **Dijkstra:** `PriorityQueue` de distancias, relajación de conexiones activas, mapa de vértices predecesores y mapa de conexiones predecesoras. La reconstrucción recorre los predecesores desde el destino hasta el origen.
- En caso de empate se conserva el primer predecesor encontrado (`candidate < distancia`), lo que hace determinista la ruta de control.

### 2.6 Integración

- `RedSeguraApplication` solo usa métodos públicos de `RedSeguraCore`.
- `RedSeguraCore` posee el grafo y los dos árboles, y expone operaciones de búsqueda, representación, recorrido, ruta y bloqueo.
- `shortestPathAvoiding` restaura la conexión en un bloque `finally`, incluso si ocurre una excepción.
- La CLI captura excepciones de dominio y sigue ejecutándose; Q7 lo demuestra con el vértice S99.

---

## 3. Complejidades

| Estructura / algoritmo | Tiempo | Espacio | Justificación |
|---|---|---|---|
| ABB búsqueda/inserción/eliminación | O(h); peor O(n), promedio O(log n) | O(n) | Se recorre un camino desde la raíz |
| AVL búsqueda/inserción/eliminación | O(log n) | O(n) | Altura acotada por el balance |
| Recorridos ABB/AVL | O(n) | O(n) con pila explícita | Cada nodo se visita una vez |
| `MixedGraph.addVertex/addEdge` | O(1) amortizado | O(V + E) | Mapas y listas |
| `getNeighbors` | O(grado(v)) | O(grado(v)) | Copia defensiva |
| Lista de adyacencia | O(V + E) | O(V + E) | Recorre todas las conexiones |
| Matriz de adyacencia | O(V²) | O(V²) | Inicializa y llena la matriz |
| Matriz de incidencia | O(V·E) | O(V·E) | Una columna por conexión |
| BFS | O(V + E) | O(V) | Cola y visitados |
| DFS | O(V + E) | O(V) | Recursión, colores y camino |
| Componentes débiles | O(V + E) | O(V + E) | Grafo auxiliar no dirigido |
| Dijkstra | O((V + E) log V) | O(V + E) | Montículo binario y relajación |

Para el conjunto del parcial, V = 12 y E = 18.

---

## 4. Resultados de control

### Q1

- ABB: Hospital Universitario encontrado en 3 comparaciones; altura 7.
- AVL: encontrado en 1 comparación; altura 4.

### Q2

| Caso | Secuencia | Resultado |
|---|---|---|
| LL | 30, 20, 10 | rotación simple a la derecha; raíz 20 |
| RR | 10, 20, 30 | rotación simple a la izquierda; raíz 20 |
| LR | 30, 10, 20 | rotación doble izquierda-derecha; raíz 20 |
| RL | 10, 30, 20 | rotación doble derecha-izquierda; raíz 20 |

### Q3

Lista, matriz de adyacencia 12 × 12 y matriz de incidencia 12 × 18 generadas con el mismo orden. La evidencia completa está en `EVIDENCIA_EJECUCION.txt`.

### Q4

- BFS: S02, S01, S06, S07, S09, S05, S08, S10, S12, S03, S04, S11.
- DFS: S02, S01, S07, S09, S05, S10, S08, S12, S03, S04, S11, S06.
- Ciclo detectado: S01 → S07 → S01.
- Componentes débiles: 1.

La diferencia se explica por el uso de cola FIFO en BFS frente a la recursión/pila LIFO en DFS, y por el orden de la lista de adyacencia.

### Q5

- Ruta: **S02 → S01 → S07 → S08**
- Conexiones: R04 → R01 → R16
- Costo: **24 minutos**

### Q6

- Ruta con R16 bloqueada: **S02 → S01 → S09 → S10 → S08**
- Conexiones: R04 → R03 → R08 → R09
- Costo: **24 minutos**

El costo no cambia porque existe una ruta alternativa de igual peso. Cambian las conexiones y el recorrido. R16 se restaura al finalizar.

### Q7

La consulta de S99 produce una `VertexNotFoundException`, se informa como error controlado y el ciclo del menú continúa.

---

## 5. Pruebas y casos borde

Se implementaron 61 pruebas JUnit 5 distribuidas en 10 clases. Incluyen:

- Operaciones normales de ABB y AVL.
- Las cuatro rotaciones.
- Eliminación de hoja, nodo con un hijo y nodo con dos hijos.
- Duplicados de vértice, conexión y clave.
- Vértice y conexión inexistentes.
- Pesos 0 y negativos.
- Autolazo.
- Grafo desconectado y destino inalcanzable.
- Arco dirigido recorrido en sentido contrario.
- Bloqueo de R16 y restauración.
- Menú completo Q1..Q7 y Q7 sin terminación.

Comando de verificación:

```bash
mvn test
```

Resultado: `Tests run: 61, Failures: 0, Errors: 0, Skipped: 0`.

---

## 6. Conclusiones

- El ABB es sencillo y eficiente cuando el árbol queda razonablemente balanceado, pero puede degenerar. El AVL garantiza O(log n) a costa de rotaciones.
- El grafo mixto requiere distinguir cuidadosamente aristas y arcos en cada representación y en cada recorrido.
- BFS y DFS visitan el mismo conjunto alcanzable, pero responden preguntas distintas: BFS privilegia el camino con menos conexiones; DFS facilita ciclos, tiempos y componentes.
- Dijkstra con `PriorityQueue` y predecesores resuelve la ruta mínima y permite explicar el efecto de un bloqueo.
- La separación en modelo, estructuras, algoritmos, fachada y CLI facilita las pruebas y la sustentación.

## 7. Nota sobre originalidad

El diseño, los nombres de clases y los contratos siguen el enunciado del parcial. Las estructuras y algoritmos se implementaron en Java sin bibliotecas que resuelvan AVL, BFS, DFS o Dijkstra. Antes de entregar, el estudiante debe revisar el código, ejecutar las pruebas y estar en capacidad de modificar cualquier peso, conexión o secuencia durante la sustentación.
