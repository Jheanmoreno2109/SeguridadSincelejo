package co.edu.redsegura.tree;

import co.edu.redsegura.exception.DuplicateIdException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BinarySearchTreeTest {

    private BinarySearchTree<Integer, String> sampleTree() {
        BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
        for (int key : new int[]{50, 30, 70, 20, 40, 60, 80}) {
            tree.insert(key, "valor-" + key);
        }
        return tree;
    }

    private List<Integer> keys(List<TreeEntry<Integer, String>> entries) {
        return entries.stream().map(TreeEntry::key).toList();
    }

    @Test
    void insertSearchAndSizeWork() {
        BinarySearchTree<Integer, String> tree = sampleTree();

        assertEquals(7, tree.size());
        assertEquals(3, tree.height());
        assertTrue(tree.search(40).found());
        assertEquals("valor-40", tree.search(40).value());
        assertTrue(tree.search(40).comparisons() > 0);
        assertFalse(tree.search(99).found());
        assertNull(tree.search(99).value());
    }

    @Test
    void duplicateKeyIsRejected() {
        BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
        tree.insert(10, "a");

        assertThrows(DuplicateIdException.class, () -> tree.insert(10, "b"));
        assertEquals(1, tree.size());
        assertEquals("a", tree.search(10).value());
    }

    @Test
    void traversalsRespectBstOrder() {
        BinarySearchTree<Integer, String> tree = sampleTree();

        assertEquals(List.of(20, 30, 40, 50, 60, 70, 80), keys(tree.inOrder()));
        assertEquals(List.of(50, 30, 20, 40, 70, 60, 80), keys(tree.preOrder()));
        assertEquals(List.of(20, 40, 30, 60, 80, 70, 50), keys(tree.postOrder()));
    }

    @Test
    void deleteHandlesLeafOneChildAndTwoChildren() {
        BinarySearchTree<Integer, String> tree = sampleTree();

        assertTrue(tree.delete(20));
        assertFalse(tree.contains(20));
        assertEquals(6, tree.size());
        assertEquals(List.of(30, 40, 50, 60, 70, 80), keys(tree.inOrder()));

        assertTrue(tree.delete(30));
        assertFalse(tree.contains(30));
        assertEquals(List.of(40, 50, 60, 70, 80), keys(tree.inOrder()));

        assertTrue(tree.delete(50));
        assertEquals(4, tree.size());
        assertEquals(List.of(40, 60, 70, 80), keys(tree.inOrder()));
        assertEquals(60, tree.rootKey());

        assertFalse(tree.delete(999));
        assertEquals(4, tree.size());
    }

    @Test
    void emptyTreeHasZeroHeightAndNullRoot() {
        BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();

        assertTrue(tree.isEmpty());
        assertEquals(0, tree.height());
        assertNull(tree.rootKey());
        assertEquals(0, tree.search(1).comparisons());
        assertTrue(tree.inOrder().isEmpty());
    }

    @Test
    void sequentialInsertionProducesDegenerateTree() {
        BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
        for (int i = 1; i <= 5; i++) {
            tree.insert(i, "v" + i);
        }

        assertEquals(5, tree.height());
        assertEquals(5, tree.search(5).comparisons());
    }

    @Test
    void clearRemovesAllNodes() {
        BinarySearchTree<Integer, String> tree = sampleTree();
        tree.clear();

        assertEquals(0, tree.size());
        assertEquals(0, tree.height());
        assertNull(tree.rootKey());
    }
}
