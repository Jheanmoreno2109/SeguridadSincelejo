package co.edu.redsegura.tree;

import co.edu.redsegura.exception.DuplicateIdException;
import co.edu.redsegura.exception.InvalidDataException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * Arbol binario de busqueda (ABB) generico, implementado sin librerias de
 * colecciones que resuelvan el algoritmo.
 *
 * <p>Invariante: para todo nodo, las claves del subarbol izquierdo son menores
 * y las del subarbol derecho son mayores. No se admiten claves duplicadas.</p>
 *
 * <p>La altura se expresa en niveles: un arbol vacio tiene altura 0 y una hoja
 * tiene altura 1.</p>
 *
 * @param <K> tipo de clave, debe ser {@link Comparable}
 * @param <V> tipo del valor almacenado
 */
public class BinarySearchTree<K extends Comparable<K>, V> {

    private static final class Node<K, V> {
        private K key;
        private V value;
        private Node<K, V> left;
        private Node<K, V> right;

        private Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node<K, V> root;
    private int size;

    /**
     * Inserta una pareja clave-valor.
     *
     * @throws DuplicateIdException si la clave ya existe
     * @throws InvalidDataException si la clave es nula
     */
    public void insert(K key, V value) {
        requireKey(key);
        // Caso base: el árbol está vacío y el nuevo nodo será la raíz.
        if (root == null) {
            root = new Node<>(key, value);
            size++;
            return;
        }
        // Descendemos comparando la clave nueva con la clave actual.
        Node<K, V> current = root;
        while (true) {
            int comparison = key.compareTo(current.key);
            if (comparison == 0) {
                throw new DuplicateIdException("La clave '" + key + "' ya existe en el ABB");
            }
            if (comparison < 0) {
                if (current.left == null) {
                    current.left = new Node<>(key, value);
                    size++;
                    return;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new Node<>(key, value);
                    size++;
                    return;
                }
                current = current.right;
            }
        }
    }

    /**
     * Busca una clave y cuenta las comparaciones realizadas.
     *
     * @param key clave buscada
     * @return resultado con valor encontrado y numero de pasos
     */
    public SearchResult<V> search(K key) {
        requireKey(key);
        Node<K, V> current = root;
        int comparisons = 0;
        // Cada comparación descarta un subárbol completo.
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

    /** Indica si la clave existe en el arbol. */
    public boolean contains(K key) {
        return search(key).found();
    }

    /**
     * Elimina una clave.
     *
     * @return {@code true} si la clave existia y fue eliminada
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
            return node;
        }
        if (comparison > 0) {
            node.right = deleteRecursive(node.right, key);
            return node;
        }

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
        return node;
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
        return node;
    }

    /** Altura en niveles: arbol vacio 0, hoja 1. */
    public int height() {
        if (root == null) {
            return 0;
        }
        int maximum = 0;
        Deque<NodeWithDepth<K, V>> pending = new ArrayDeque<>();
        pending.push(new NodeWithDepth<>(root, 1));
        while (!pending.isEmpty()) {
            NodeWithDepth<K, V> current = pending.pop();
            if (current.depth > maximum) {
                maximum = current.depth;
            }
            if (current.node.left != null) {
                pending.push(new NodeWithDepth<>(current.node.left, current.depth + 1));
            }
            if (current.node.right != null) {
                pending.push(new NodeWithDepth<>(current.node.right, current.depth + 1));
            }
        }
        return maximum;
    }

    /** Nodo junto con su profundidad, usado para calcular la altura sin recursión. */
    private record NodeWithDepth<K, V>(Node<K, V> node, int depth) {
    }

    /** Recorrido inorden (izquierda, raiz, derecha). */
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

    /** Recorrido preorden (raiz, izquierda, derecha). */
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

    /** Recorrido postorden (izquierda, derecha, raiz). */
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

    /** Clave de la raiz, o {@code null} si el arbol esta vacio. */
    public K rootKey() {
        return root == null ? null : root.key;
    }

    public void clear() {
        root = null;
        size = 0;
    }

    private static <K> void requireKey(K key) {
        if (Objects.isNull(key)) {
            throw new InvalidDataException("La clave del ABB no puede ser nula");
        }
    }
}
