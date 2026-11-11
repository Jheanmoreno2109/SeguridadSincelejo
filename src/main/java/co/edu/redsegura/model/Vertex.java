package co.edu.redsegura.model;

import co.edu.redsegura.exception.InvalidDataException;

import java.util.Objects;

/**
 * Vertice o lugar de la red RedSegura.
 *
 * <p>Es inmutable: el identificador, la etiqueta y el tipo se validan en el
 * constructor y no cambian durante la ejecucion. La igualdad se define por el
 * identificador, que es la clave natural del grafo.</p>
 */
public final class Vertex {

    private final String id;
    private final String label;
    private final VertexType type;

    /**
     * Crea un vertice.
     *
     * @param id    identificador unico, por ejemplo {@code S01}
     * @param label etiqueta legible, por ejemplo {@code UAJS Sede C}
     * @param type  tipo funcional del lugar
     * @throws InvalidDataException si el id o la etiqueta estan vacios
     */
    public Vertex(String id, String label, VertexType type) {
        this.id = requireText(id, "El identificador del vertice");
        this.label = requireText(label, "La etiqueta del vertice");
        this.type = Objects.requireNonNull(type, "El tipo del vertice es obligatorio");
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public VertexType getType() {
        return type;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException(field + " no puede ser nulo ni vacio");
        }
        return value.trim();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Vertex vertex)) {
            return false;
        }
        return id.equals(vertex.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id + " " + label + " (" + type.getDisplayName() + ")";
    }
}
