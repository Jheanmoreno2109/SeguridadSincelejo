package co.edu.redsegura.tree;

/**
 * Par clave-valor almacenado en un arbol de busqueda.
 *
 * @param key   clave ordenable
 * @param value valor asociado
 * @param <K>   tipo de la clave
 * @param <V>   tipo del valor
 */
public record TreeEntry<K, V>(K key, V value) {
}
