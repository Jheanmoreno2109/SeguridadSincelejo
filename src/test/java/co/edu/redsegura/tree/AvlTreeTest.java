package co.edu.redsegura.tree;

import co.edu.redsegura.exception.DuplicateIdException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AvlTreeTest {

    private List<Integer> keys(AvlTree<Integer, String> tree) {
        return tree.inOrder().stream().map(TreeEntry::key).toList();
    }

    @Test
    void fourRotationsProduceBalancedTree() {
        record RotationCase(String code, List<Integer> sequence) {
        }
        List<RotationCase> cases = List.of(
                new RotationCase("LL", List.of(30, 20, 10)),
                new RotationCase("RR", List.of(10, 20, 30)),
                new RotationCase("LR", List.of(30, 10, 20)),
                new RotationCase("RL", List.of(10, 30, 20))
        );

        for (RotationCase rotationCase : cases) {
            AvlTree<Integer, String> tree = new AvlTree<>();
            for (Integer key : rotationCase.sequence()) {
                tree.insert(key, "v" + key);
            }

            assertEquals(20, tree.rootKey(), "raiz del caso " + rotationCase.code());
            assertEquals(List.of(10, 20, 30), keys(tree), "inorden del caso " + rotationCase.code());
            assertEquals(2, tree.height());
            assertTrue(tree.isBalanced());
            assertFalse(tree.rotationLog().isEmpty());
            assertTrue(tree.rotationLog().get(0).startsWith(rotationCase.code()),
                    "se esperaba rotacion " + rotationCase.code() + " y fue " + tree.rotationLog());
        }
    }

    @Test
    void sequentialInsertionsKeepLogarithmicHeight() {
        AvlTree<Integer, String> tree = new AvlTree<>();
        for (int i = 1; i <= 1000; i++) {
            tree.insert(i, "v" + i);
        }

        assertEquals(1000, tree.size());
        assertTrue(tree.isBalanced());
        assertTrue(tree.height() <= 15, "altura inesperada: " + tree.height());
        assertEquals(1, keys(tree).get(0));
        assertEquals(1000, keys(tree).get(999));
    }

    @Test
    void deletionRebalancesAndKeepsOrder() {
        AvlTree<Integer, String> tree = new AvlTree<>();
        for (int i = 1; i <= 20; i++) {
            tree.insert(i, "v" + i);
        }

        assertTrue(tree.delete(1));
        assertTrue(tree.delete(8));
        assertTrue(tree.delete(15));
        assertFalse(tree.delete(99));

        assertEquals(17, tree.size());
        assertTrue(tree.isBalanced());
        assertFalse(tree.search(8).found());
        List<Integer> sorted = keys(tree);
        for (int i = 1; i < sorted.size(); i++) {
            assertTrue(sorted.get(i - 1) < sorted.get(i));
        }
    }

    @Test
    void deleteRootAndAllNodes() {
        AvlTree<Integer, String> tree = new AvlTree<>();
        for (int key : new int[]{50, 30, 70, 20, 40, 60, 80}) {
            tree.insert(key, "v" + key);
        }
        assertTrue(tree.delete(50));
        assertFalse(tree.search(50).found());
        assertTrue(tree.isBalanced());

        for (int key : new int[]{30, 70, 20, 40, 60, 80}) {
            assertTrue(tree.delete(key));
        }
        assertTrue(tree.isEmpty());
        assertEquals(0, tree.height());
    }

    @Test
    void duplicateKeyIsRejected() {
        AvlTree<Integer, String> tree = new AvlTree<>();
        tree.insert(10, "a");

        assertThrows(DuplicateIdException.class, () -> tree.insert(10, "b"));
        assertEquals(1, tree.size());
    }

    @Test
    void balanceFactorAndRotationLogAreObservable() {
        AvlTree<Integer, String> tree = new AvlTree<>();
        tree.insert(30, "a");
        tree.insert(20, "b");
        tree.insert(10, "c");

        assertEquals(20, tree.rootKey());
        assertEquals(0, tree.balanceFactorOf(20));
        assertNotNull(tree.balanceFactorOf(10));
        tree.clearRotationLog();
        assertTrue(tree.rotationLog().isEmpty());
    }
}
