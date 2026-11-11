package co.edu.redsegura.tree;

import co.edu.redsegura.exception.DuplicateIdException;
import co.edu.redsegura.exception.InvalidDataException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * Arbol AVL generico con insercion y eliminacion balanceadas.
 *
 * <p>Cada nodo conserva su altura en niveles (hoja = 1). Despues de cada
 * insercion o eliminacion se actualizan las alturas y se calcula el factor de
 * balance. Si el factor sale del rango [-1, 1] se aplica una de las cuatro
 * rotaciones: LL, RR, LR o RL.</p>
 *
 * <p>Las rotaciones aplicadas se registran en {@link #rotationLog()} para
 * poder demostrar la consulta Q2 sin inspeccionar campos privados.</p>
 *
 * @param <K> tipo de clave, debe ser {@link Comparable}
 * @param <V> tipo del valor almacenado
 */
public final class AvlTree<K extends Comparable<K>, V> {

    private static final class Node<K, V> {
        private K key;
        private V value;
        private Node<K, V> left;
        private Node<K, V> right;
        private int height;

        private Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.height = 1;
        }
    }

    private Node<K, V> root;
    private int size;
    private final List<String> rotationLog = new ArrayList<>();

    /**
     * Inserta una pareja clave-valor y rebalancea el camino recorrido.
     *
     * @throws DuplicateIdException si la clave ya existe
     * @throws InvalidDataException si la clave es nula
     */
    public void insert(K key, V value) {
        requireKey(key);
        root = insertRecursive(root, key, value);
    }

    private Node<K, V> insertRecursive(Node<K, V> node, K key, V value) {
        if (node == null) {
            size++;
            return new Node<>(key, value);
        }
        int comparison = key.compareTo(node.key);
        if (comparison == 0) {
            throw new DuplicateIdException("La clave '" + key + "' ya existe en el AVL");
        }
        if (comparison < 0) {
            node.left = insertRecursive(node.left, key, value);
        } else {
            node.right = insertRecursive(node.right, key, value);
        }
        return rebalance(node);
    }

    /**
     * Busca una clave y cuenta las comparaciones realizadas.
     */
    public SearchResult<V> search(K key) {
        requireKey(key);
        Node<K, V> current = root;
        int comparisons = 0;
        while (current != null) {
            comparisons++;
            int comparison = key.compareTo(current.key);
            if (comparison == 0) {
                return new SearchResult<>(true, current.value, comparisons);
            }
            current = comparison < 0 ? current.left : current.right;
        }
        return new SearchResult<>(false, null, comparisons);
    }

    public boolean contains(K key) {
        return search(key).found();
    }

    /**
     * Elimina una clave y rebalancea el arbol.
     *
     * @return {@code true} si la clave existia
     */
    public boolean delete(K key) {
        requireKey(key);
        int previousSize = size;
        root = deleteRecursive(root, key);
        return size < previousSize;
    }

    private Node<K, V> deleteRecursive(Node<K, V> node, K key) {
        if (node == null) {
            return null;
        }
        int comparison = key.compareTo(node.key);
        if (comparison < 0) {
            node.left = deleteRecursive(node.left, key);
        } else if (comparison > 0) {
            node.right = deleteRecursive(node.right, key);
        } else {
            size--;
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            Node<K, V> successor = minimum(node.right);
            node.key = successor.key;
            node.value = successor.value;
            node.right = deleteMinimum(node.right);
        }
        return rebalance(node);
    }

    private Node<K, V> minimum(Node<K, V> node) {
        Node<K, V> current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    private Node<K, V> deleteMinimum(Node<K, V> node) {
        if (node.left == null) {
            return node.right;
        }
        node.left = deleteMinimum(node.left);
        return rebalance(node);
    }

    /**
     * Actualiza la altura, calcula el factor de balance y aplica la rotación
     * necesaria.
     *
     * <p>El factor de balance es:</p>
     * <pre>
     *   altura(izquierda) - altura(derecha)
     * </pre>
     *
     * <ul>
     *   <li>Mayor que 1: el lado izquierdo pesa más (casos LL o LR).</li>
     *   <li>Menor que -1: el lado derecho pesa más (casos RR o RL).</li>
     *   <li>Entre -1 y 1: el nodo ya está balanceado.</li>
     * </ul>
     */
    private Node<K, V> rebalance(Node<K, V> node) {
        updateHeight(node);
        int balance = balanceFactor(node);

        if (balance > 1) {
            // El subárbol izquierdo es más alto.
            if (balanceFactor(node.left) >= 0) {
                return applyLeftLeftRotation(node);
            }
            return applyLeftRightRotation(node);
        }

        if (balance < -1) {
            // El subárbol derecho es más alto.
            if (balanceFactor(node.right) <= 0) {
                return applyRightRightRotation(node);
            }
            return applyRightLeftRotation(node);
        }

        return node;
    }

    /** Caso LL: una sola rotación a la derecha. */
    private Node<K, V> applyLeftLeftRotation(Node<K, V> node) {
        log("LL", node);
        return rotateRight(node);
    }

    /** Caso RR: una sola rotación a la izquierda. */
    private Node<K, V> applyRightRightRotation(Node<K, V> node) {
        log("RR", node);
        return rotateLeft(node);
    }

    /** Caso LR: primero rotación izquierda en el hijo y luego derecha en la raíz. */
    private Node<K, V> applyLeftRightRotation(Node<K, V> node) {
        log("LR", node);
        node.left = rotateLeft(node.left);
        return rotateRight(node);
    }

    /** Caso RL: primero rotación derecha en el hijo y luego izquierda en la raíz. */
    private Node<K, V> applyRightLeftRotation(Node<K, V> node) {
        log("RL", node);
        node.right = rotateRight(node.right);
        return rotateLeft(node);
    }

    private void log(String rotation, Node<K, V> node) {
        rotationLog.add(rotation + " en clave " + node.key
                + " (factor " + balanceFactor(node) + ", altura " + node.height + ")");
    }

    /**
     * Rotación simple a la derecha.
     *
     * <pre>
     *        A                 B
     *       / \               / \
     *      B   C     -->     D   A
     *     / \                   / \
     *    D   E                 E   C
     * </pre>
     */
    private Node<K, V> rotateRight(Node<K, V> node) {
        Node<K, V> newRoot = node.left;          // B pasa a ser la nueva raíz
        Node<K, V> transferred = newRoot.right;  // E se mueve al lado izquierdo de A
        newRoot.right = node;                    // A queda como hijo derecho de B
        node.left = transferred;                 // A adopta a E
        updateHeight(node);
        updateHeight(newRoot);
        return newRoot;
    }

    /**
     * Rotación simple a la izquierda.
     *
     * <pre>
     *      A                 B
     *     / \               / \
     *    C   B     -->     A   E
     *       / \           / \
     *      D   E         C   D
     * </pre>
     */
    private Node<K, V> rotateLeft(Node<K, V> node) {
        Node<K, V> newRoot = node.right;         // B pasa a ser la nueva raíz
        Node<K, V> transferred = newRoot.left;   // D se mueve al lado derecho de A
        newRoot.left = node;                     // A queda como hijo izquierdo de B
        node.right = transferred;                // A adopta a D
        updateHeight(node);
        updateHeight(newRoot);
        return newRoot;
    }

    private void updateHeight(Node<K, V> node) {
        node.height = 1 + Math.max(heightOf(node.left), heightOf(node.right));
    }

    private int heightOf(Node<K, V> node) {
        return node == null ? 0 : node.height;
    }

    private int balanceFactor(Node<K, V> node) {
        return node == null ? 0 : heightOf(node.left) - heightOf(node.right);
    }

    /** Altura en niveles: arbol vacio 0, hoja 1. */
    public int height() {
        return heightOf(root);
    }

    /** Factor de balance del nodo que contiene la clave, o 0 si no existe. */
    public int balanceFactorOf(K key) {
        requireKey(key);
        Node<K, V> current = root;
        while (current != null) {
            int comparison = key.compareTo(current.key);
            if (comparison == 0) {
                return balanceFactor(current);
            }
            current = comparison < 0 ? current.left : current.right;
        }
        return 0;
    }

    /** Verifica recursivamente que todos los nodos respeten el balance AVL. */
    public boolean isBalanced() {
        return isBalancedRecursive(root);
    }

    private boolean isBalancedRecursive(Node<K, V> node) {
        if (node == null) {
            return true;
        }
        int expectedHeight = 1 + Math.max(heightOf(node.left), heightOf(node.right));
        if (node.height != expectedHeight || Math.abs(balanceFactor(node)) > 1) {
            return false;
        }
        return isBalancedRecursive(node.left) && isBalancedRecursive(node.right);
    }

    /** Recorrido inorden. */
    public List<TreeEntry<K, V>> inOrder() {
        List<TreeEntry<K, V>> result = new ArrayList<>(size);
        Deque<Node<K, V>> stack = new ArrayDeque<>();
        Node<K, V> current = root;
        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();
            result.add(new TreeEntry<>(current.key, current.value));
            current = current.right;
        }
        return result;
    }

    /** Recorrido preorden. */
    public List<TreeEntry<K, V>> preOrder() {
        List<TreeEntry<K, V>> result = new ArrayList<>(size);
        if (root == null) {
            return result;
        }
        Deque<Node<K, V>> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Node<K, V> node = stack.pop();
            result.add(new TreeEntry<>(node.key, node.value));
            if (node.right != null) {
                stack.push(node.right);
            }
            if (node.left != null) {
                stack.push(node.left);
            }
        }
        return result;
    }

    /** Recorrido postorden. */
    public List<TreeEntry<K, V>> postOrder() {
        List<TreeEntry<K, V>> result = new ArrayList<>(size);
        if (root == null) {
            return result;
        }
        Deque<Node<K, V>> stack = new ArrayDeque<>();
        Deque<Node<K, V>> output = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Node<K, V> node = stack.pop();
            output.push(node);
            if (node.left != null) {
                stack.push(node.left);
            }
            if (node.right != null) {
                stack.push(node.right);
            }
        }
        while (!output.isEmpty()) {
            Node<K, V> node = output.pop();
            result.add(new TreeEntry<>(node.key, node.value));
        }
        return result;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public K rootKey() {
        return root == null ? null : root.key;
    }

    /** Copia de las rotaciones aplicadas desde la ultima limpieza. */
    public List<String> rotationLog() {
        return List.copyOf(rotationLog);
    }

    public void clearRotationLog() {
        rotationLog.clear();
    }

    public void clear() {
        root = null;
        size = 0;
        rotationLog.clear();
    }

    private static <K> void requireKey(K key) {
        if (Objects.isNull(key)) {
            throw new InvalidDataException("La clave del AVL no puede ser nula");
        }
    }
}
