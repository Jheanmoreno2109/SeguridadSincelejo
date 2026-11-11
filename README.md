# RedSegura Sincelejo

Implementación del **Parcial 2 de Estructuras de Datos II**: árboles y grafos aplicados a una red de atención de emergencias en Sincelejo.

- **Estudiante:** _(completar con nombre y código)_
- **Docente:** Jaider Enrique Reyes Herazo
- **Asignatura:** Estructuras de Datos II
- **Periodo:** 2026
- **Tecnologías:** Java 17, Maven, JUnit 5, Apache NetBeans

> **Aviso académico:** los tiempos y conexiones son datos didácticos de simulación. No constituyen información oficial de tránsito ni de respuesta a emergencias.

---

## 1. Qué incluye

| Componente | Clase |
|---|---|
| Modelo | `Vertex`, `Edge`, `VertexType`, `EdgeOrientation` |
| Árbol binario de búsqueda | `BinarySearchTree` |
| Árbol AVL | `AvlTree` |
| Grafo | `Graph`, `MixedGraph` |
| Representaciones | `GraphRepresentations` |
| Recorridos | `BreadthFirstSearch`, `DepthFirstSearch` |
| Ruta mínima | `DijkstraSolver` |
| Integración | `RedSeguraCore`, `RedSeguraDataFactory` |
| Aplicación de consola | `RedSeguraApplication` |
| Excepciones | `RedSeguraException` y subclases |

No se usan bibliotecas externas para AVL, BFS, DFS ni Dijkstra: todas las estructuras y algoritmos están implementados en el proyecto.

Resultado de control:

```
Ruta mínima S02 -> S08: 24 minutos
S02 -> S01 -> S07 -> S08
Conexiones: R04 -> R01 -> R16
```

---

## 2. Ejecución

### Desde Apache NetBeans

1. `File > Open Project` y seleccionar la carpeta que contiene `pom.xml`.
2. Verificar que el proyecto use JDK 17 o superior.
3. Ejecutar el proyecto (`Run Project`). La clase principal es `co.edu.redsegura.app.RedSeguraApplication`.
4. Para las pruebas: clic derecho sobre el proyecto → `Test`, o ejecutar `mvn test`.

### Desde terminal

```bash
mvn clean package
java -jar target/redsegura-1.0.0.jar
```

La aplicación muestra un menú interactivo con las consultas obligatorias Q1 a Q7. La opción `0` termina la sesión.

Si la consola de Windows no muestra bien las tildes:

```powershell
chcp 65001
java "-Dstdout.encoding=UTF-8" -jar target/redsegura-1.0.0.jar
```

---

## 3. Estructura del proyecto

```
redsegura/
├── pom.xml
├── README.md
├── docs/
│   ├── INFORME.md
│   ├── DIAGRAMA.md
│   ├── EVIDENCIA_EJECUCION.txt
│   ├── EVIDENCIA_PRUEBAS.txt
├── src/main/java/co/edu/redsegura/
│   ├── app/
│   │   ├── RedSeguraApplication.java   # menú Q1..Q7
│   │   ├── RedSeguraCore.java          # fachada de integración
│   │   └── RedSeguraDataFactory.java   # 12 vértices y 18 conexiones
│   ├── model/
│   │   ├── Vertex.java
│   │   ├── Edge.java
│   │   ├── VertexType.java
│   │   └── EdgeOrientation.java
│   ├── exception/
│   │   ├── RedSeguraException.java
│   │   ├── InvalidDataException.java
│   │   ├── DuplicateIdException.java
│   │   ├── VertexNotFoundException.java
│   │   └── EdgeNotFoundException.java
│   ├── graph/
│   │   ├── Graph.java
│   │   ├── MixedGraph.java
│   │   └── GraphRepresentations.java
│   ├── tree/
│   │   ├── BinarySearchTree.java
│   │   ├── AvlTree.java
│   │   ├── SearchResult.java
│   │   └── TreeEntry.java
│   └── algorithm/
│       ├── BreadthFirstSearch.java
│       ├── BfsResult.java
│       ├── DepthFirstSearch.java
│       ├── DfsResult.java
│       ├── DijkstraSolver.java
│       └── ShortestPathResult.java
└── src/test/java/co/edu/redsegura/
    ├── model/ModelValidationTest.java
    ├── tree/BinarySearchTreeTest.java
    ├── tree/AvlTreeTest.java
    ├── graph/MixedGraphTest.java
    ├── graph/GraphRepresentationsTest.java
    ├── algorithm/BreadthFirstSearchTest.java
    ├── algorithm/DepthFirstSearchTest.java
    ├── algorithm/DijkstraSolverTest.java
    ├── app/RedSeguraCoreTest.java
    └── app/RedSeguraApplicationTest.java
```

---

## 4. Arquitectura

```
RedSeguraApplication
        │  (solo usa métodos públicos)
        ▼
   RedSeguraCore
   ├── BinarySearchTree<String, Vertex>
   ├── AvlTree<String, Vertex>
   ├── MixedGraph (implementa Graph)
   │   ├── Vertex / Edge
   │   ├── GraphRepresentations
   │   ├── BreadthFirstSearch
   │   ├── DepthFirstSearch
   │   └── DijkstraSolver
   └── RedSeguraDataFactory
```

- `RedSeguraApplication` no accede a campos privados de otros módulos: solo llama métodos públicos de `RedSeguraCore`.
- `RedSeguraCore` construye el grafo con `RedSeguraDataFactory`, llena el ABB y el AVL con las etiquetas de los vértices y coordina las consultas.
- `MixedGraph` encapsula vértices, conexiones y listas de adyacencia. Los algoritmos reciben el grafo por constructor y usan su API pública.
- El diagrama completo está en [`docs/DIAGRAMA.md`](docs/DIAGRAMA.md).

| Componente | Responsabilidad |
|---|---|
| `Vertex`, `Edge` | Encapsular y validar IDs, etiquetas, pesos y orientación |
| `BinarySearchTree` | Inserción, búsqueda, eliminación, altura y tres recorridos |
| `AvlTree` | Inserción/eliminación balanceadas, factor de balance y rotaciones |
| `MixedGraph` | Aristas/arcos, vecinos, duplicados, inexistencias y bloqueos |
| `GraphRepresentations` | Lista de adyacencia, matriz ponderada y matriz de incidencia |
| `BreadthFirstSearch` | Recorrido por niveles, visitados y árbol BFS |
| `DepthFirstSearch` | Recorrido profundo, ciclos y componentes débiles |
| `DijkstraSolver` | `PriorityQueue`, relajación, distancias y reconstrucción |
| `RedSeguraCore`, `RedSeguraApplication` | Fachada, CLI y manejo de errores |

---

## 5. Datos del caso de estudio

### Vértices

| ID | Etiqueta | Tipo |
|---|---|---|
| S01 | UAJS Sede C | Universidad |
| S02 | UAJS Sede E | Universidad |
| S03 | Universidad de Sucre | Universidad |
| S04 | CECAR | Universidad |
| S05 | Hospital Universitario | Salud |
| S06 | Clínica Las Américas | Salud |
| S07 | Estación Central SETP | Transporte |
| S08 | Terminal de Transporte | Transporte |
| S09 | Parque Santander | Punto de encuentro |
| S10 | Mercado de Sincelejo | Comercio |
| S11 | PEP Argelia | Paradero |
| S12 | Glorieta Sincelejito | Movilidad |

### Conexiones

| ID | Origen | Destino | Tipo | Min | Etiqueta |
|---|---|---|---|---|---|
| R01 | S01 | S07 | Dirigida | 4 | Corredor universitario |
| R02 | S07 | S01 | Dirigida | 6 | Retorno por vía alterna |
| R03 | S01 | S09 | No dirigida | 5 | Corredor peatonal |
| R04 | S02 | S01 | No dirigida | 8 | Conexión entre sedes |
| R05 | S02 | S06 | Dirigida | 10 | Ruta de atención |
| R06 | S06 | S05 | Dirigida | 4 | Corredor clínico |
| R07 | S05 | S09 | No dirigida | 6 | Vía hospitalaria |
| R08 | S09 | S10 | No dirigida | 3 | Centro histórico |
| R09 | S10 | S08 | Dirigida | 8 | Salida al terminal |
| R10 | S08 | S10 | Dirigida | 9 | Ingreso desde terminal |
| R11 | S03 | S12 | No dirigida | 4 | Corredor Puerta Roja |
| R12 | S12 | S09 | No dirigida | 7 | Troncal al centro |
| R13 | S04 | S11 | No dirigida | 6 | Conexión universitaria |
| R14 | S11 | S07 | Dirigida | 8 | Ruta SETP al centro |
| R15 | S07 | S09 | No dirigida | 3 | Transferencia central |
| R16 | S07 | S08 | Dirigida | 12 | Ruta SETP al terminal |
| R17 | S09 | S03 | Dirigida | 7 | Ruta hacia universidad |
| R18 | S12 | S04 | Dirigida | 9 | Ruta hacia CECAR |

---

## 6. Decisiones de diseño

1. **Árboles genéricos `BinarySearchTree<K,V>` y `AvlTree<K,V>`.** Se usan con `String` (etiqueta) como clave y `Vertex` como valor. Así la búsqueda de Q1 se hace por la etiqueta visible y no por el ID.
2. **Altura en niveles.** Árbol vacío = 0, hoja = 1. La convención se usa igual en el ABB y en el AVL.
3. **Duplicados rechazados.** Insertar una clave repetida lanza `DuplicateIdException`; no se reemplaza silenciosamente.
4. **Recorridos iterativos en los árboles.** Inorden, preorden y postorden usan pilas explícitas para evitar desbordes de pila en árboles degenerados.
5. **Grafo mixto con una sola clase `MixedGraph`.** Una arista no dirigida se agrega a la lista de adyacencia de sus dos extremos, pero es el mismo objeto `Edge`; un arco dirigido solo aparece en el origen.
6. **Bloqueo sin borrado.** `block()`/`unblock()` permiten simular el cierre de R16 conservando el ID y el peso originales. Los algoritmos filtran las conexiones bloqueadas.
7. **Orden estable.** Vértices y conexiones se guardan en `LinkedHashMap`, de modo que lista, matriz de adyacencia e incidencia siempre usan el mismo orden.
8. **Matriz de adyacencia.** `INF` = sin conexión; diagonal = 0. Si hubiera más de una conexión en el mismo sentido entre dos vértices, se muestra el menor peso.
9. **Matriz de incidencia.** `V × E` (filas = vértices, columnas = conexiones). Arco dirigido: `-1` en el origen y `+1` en el destino. Arista no dirigida: `+1` en ambos extremos. La convención se imprime en la propia salida.
10. **BFS y DFS respetan la orientación.** Las aristas no dirigidas se recorren en ambos sentidos; los arcos dirigidos solo del origen al destino. Las conexiones bloqueadas se omiten.
11. **DFS con colores.** Blanco/gris/negro permite detectar ciclos dirigidos por aristas de retroceso. Las componentes débiles se calculan sobre el grafo subyacente no dirigido.
12. **Dijkstra con relajación estricta (`<`).** En caso de empate no se reemplaza el predecesor, lo que hace determinista la ruta de control S02 → S01 → S07 → S08.
13. **Errores controlados.** La CLI captura `RedSeguraException` y `IllegalArgumentException`, informa el problema y continúa. Q7 demuestra este comportamiento.

---

## 7. Complejidades

| Operación | Tiempo | Espacio | Observación |
|---|---|---|---|
| ABB inserción/búsqueda/eliminación | O(h); peor O(n), promedio O(log n) | O(n) | `h` = altura; el ABB no se autobalancea |
| AVL inserción/búsqueda/eliminación | O(log n) | O(n) | Garantiza `h = O(log n)` |
| Recorridos del árbol | O(n) | O(n) con pila explícita | Inorden, preorden y postorden |
| Agregar vértice/conexión | O(1) amortizado | O(V + E) | Validaciones con mapas |
| Consultar vecinos | O(grado(v)) | O(grado(v)) | Copia defensiva de la lista |
| Lista de adyacencia | O(V + E) | O(V + E) | Incluye ambas direcciones de aristas |
| Matriz de adyacencia | O(V²) | O(V²) | Pesos e `INF` |
| Matriz de incidencia | O(V·E) | O(V·E) | Una columna por conexión |
| BFS | O(V + E) | O(V) | Cola y conjunto de visitados |
| DFS | O(V + E) | O(V) | Recursión, colores y pila de camino |
| Componentes débiles | O(V + E) | O(V + E) | Recorrido no dirigido auxiliar |
| Dijkstra con `PriorityQueue` | O((V + E) log V) | O(V + E) | Pesos positivos |

La justificación ampliada está en [`docs/INFORME.md`](docs/INFORME.md).

---

## 8. Consultas obligatorias y resultados

### Q1. Hospital Universitario en ABB y AVL

```
ABB: encontrado Hospital Universitario (S05) en 3 comparaciones.
AVL: encontrado Hospital Universitario (S05) en 1 comparaciones.
Altura del ABB: 7 niveles; altura del AVL: 4 niveles.
```

El AVL requiere menos comparaciones porque mantiene el balance. El ABB, con el orden de inserción del conjunto, queda con altura 7.

### Q2. Rotaciones AVL

| Caso | Secuencia | Rotación | Raíz | Inorden |
|---|---|---|---|---|
| LL | 30, 20, 10 | simple a la derecha | 20 | 10, 20, 30 |
| RR | 10, 20, 30 | simple a la izquierda | 20 | 10, 20, 30 |
| LR | 30, 10, 20 | doble izquierda-derecha | 20 | 10, 20, 30 |
| RL | 10, 30, 20 | doble derecha-izquierda | 20 | 10, 20, 30 |

Cada caso queda registrado en `AvlTree.rotationLog()` y se imprime en la consulta Q2.

### Q3. Representaciones

Se imprimen la lista de adyacencia, la matriz de adyacencia ponderada de 12 × 12 y la matriz de incidencia de 12 × 18. Todas usan el mismo orden S01..S12 y R01..R18. La salida completa está en [`docs/EVIDENCIA_EJECUCION.txt`](docs/EVIDENCIA_EJECUCION.txt).

### Q4. BFS y DFS desde S02

```
BFS: S02, S01, S06, S07, S09, S05, S08, S10, S12, S03, S04, S11
DFS: S02, S01, S07, S09, S05, S10, S08, S12, S03, S04, S11, S06
```

BFS usa una cola FIFO y descubre por niveles. DFS usa recursión (pila LIFO) y agota una rama antes de retroceder. Ambos usan visitados y recorren los mismos 12 vértices alcanzables, pero el orden depende de la estrategia y del orden de la lista de adyacencia. El DFS detecta el ciclo `S01 -> S07 -> S01`; el grafo tiene una sola componente débil.

### Q5. Dijkstra S02 → S08

```
Tiempo total: 24 minutos
Ruta: S02 -> S01 -> S07 -> S08
Conexiones: R04 -> R01 -> R16
```

La prueba JUnit `DijkstraSolverTest.controlRouteS02ToS08Takes24Minutes` verifica este resultado.

### Q6. Bloquear R16

```
Ruta original: S02 -> S01 -> S07 -> S08 (24 min, R04 -> R01 -> R16)
Ruta con R16 bloqueada: S02 -> S01 -> S09 -> S10 -> S08 (24 min, R04 -> R03 -> R08 -> R09)
```

R16 era el tramo directo S07 → S08 de 12 minutos. Al bloquearlo, Dijkstra busca otra ruta. El costo se conserva en 24 minutos porque existe una alternativa de igual peso; cambian las conexiones utilizadas. La conexión se restaura automáticamente en `finally`.

### Q7. Vértice inexistente

```
[ERROR CONTROLADO] El vértice 'S99' no existe en el grafo RedSegura
La aplicación captura la excepción y continúa; el proceso no termina.
```

La prueba `RedSeguraApplicationTest.nonexistentVertexDoesNotTerminateInteractiveSession` simula la opción 7 y luego la salida, verificando que el menú siga vivo.

---

## 9. Pruebas JUnit 5

```bash
mvn test
```

Resultado: **61 pruebas, 0 fallos, 0 errores**.

| Clase de prueba | Casos cubiertos |
|---|---|
| `ModelValidationTest` | IDs/etiquetas vacíos, pesos 0 y negativos, autolazo, bloqueo |
| `BinarySearchTreeTest` | Inserción, búsqueda, duplicados, tres recorridos, borrado de hoja/uno/dos hijos, altura |
| `AvlTreeTest` | LL, RR, LR, RL, altura logarítmica, borrado con rebalanceo, duplicados |
| `MixedGraphTest` | Duplicados, vértice/arista inexistente, orientación, bloqueo, eliminación |
| `GraphRepresentationsTest` | Orden, lista, matriz ponderada, incidencia y convención de signos |
| `BreadthFirstSearchTest` | Orden por niveles, padres, árbol BFS, bloqueo, desconexión, orientación |
| `DepthFirstSearchTest` | Pre/postorden, tiempos, ciclo, componentes débiles, orientación |
| `DijkstraSolverTest` | Ruta 24, bloqueo de R16, distancias, inalcanzable, dirección |
| `RedSeguraCoreTest` | Integración, búsqueda Q1, representaciones, bloqueo temporal |
| `RedSeguraApplicationTest` | Menú completo Q1..Q7, Q7 sin terminar, opción inválida |

Casos borde incluidos: vértice inexistente, arista inexistente, ID duplicado, peso inválido, autolazo, grafo desconectado, destino inalcanzable, conexión dirigida recorrida al revés y bloqueo/restauración.

---

## 10. Limitaciones

- No hay interfaz gráfica ni persistencia: el alcance es una aplicación de consola con datos en memoria.
- El bloqueo de conexiones es temporal y no se guarda entre ejecuciones.
- La matriz de adyacencia representa un solo peso por par ordenado; si se agregaran conexiones paralelas en el mismo sentido, se muestra el menor.
- Los tiempos son simulados y no deben usarse para decisiones reales de tránsito o emergencias.
