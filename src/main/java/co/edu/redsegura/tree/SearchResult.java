package co.edu.redsegura.tree;

/**
 * Resultado de una busqueda en un arbol.
 *
 * @param found       {@code true} si la clave existe
 * @param value       valor asociado, o {@code null} si no se encontro
 * @param comparisons cantidad de claves comparadas durante la busqueda
 * @param <V>         tipo del valor
 */
public record SearchResult<V>(boolean found, V value, int comparisons) {

    /** Devuelve el valor o lanza una excepcion si la clave no existe. */
    public V requireValue() {
        if (!found) {
            throw new IllegalStateException("La clave buscada no existe");
        }
        return value;
    }
}
