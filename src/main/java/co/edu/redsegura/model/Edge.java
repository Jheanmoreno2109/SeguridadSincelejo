package co.edu.redsegura.model;

import co.edu.redsegura.exception.InvalidDataException;

import java.util.Objects;

/**
 * Conexion de la red RedSegura.
 *
 * <p>Conserva identificador, etiqueta, peso (minutos), orientacion y extremos.
 * Una conexion puede bloquearse de forma temporal, por ejemplo para simular el
 * cierre de la ruta R16 sin perder su informacion original.</p>
 *
 * <p>El peso debe ser estrictamente mayor que cero y los extremos no pueden
 * coincidir. La validacion de existencia de los extremos corresponde al grafo.</p>
 */
public final class Edge {

    private final String id;
    private final String label;
    private final int weight;
    private final EdgeOrientation orientation;
    private final String originId;
    private final String destinationId;
    private boolean blocked;

    /**
     * Crea una conexion activa.
     *
     * @param id            identificador unico, por ejemplo {@code R01}
     * @param label         etiqueta legible
     * @param weight        minutos estimados, mayor que cero
     * @param orientation   dirigida o no dirigida
     * @param originId      identificador del vertice de origen
     * @param destinationId identificador del vertice de destino
     * @throws InvalidDataException si algun dato no es valido
     */
    public Edge(String id, String label, int weight, EdgeOrientation orientation,
                String originId, String destinationId) {
        this.id = requireText(id, "El identificador de la conexion");
        this.label = requireText(label, "La etiqueta de la conexion");
        if (weight <= 0) {
            throw new InvalidDataException(
                    "El peso de la conexion " + this.id + " debe ser mayor que cero; recibido: " + weight);
        }
        this.weight = weight;
        this.orientation = Objects.requireNonNull(orientation,
                "La orientacion de la conexion " + this.id + " es obligatoria");
        this.originId = requireText(originId, "El origen de la conexion " + this.id);
        this.destinationId = requireText(destinationId, "El destino de la conexion " + this.id);
        if (this.originId.equals(this.destinationId)) {
            throw new InvalidDataException(
                    "La conexion " + this.id + " no puede tener el mismo origen y destino: " + this.originId);
        }
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public int getWeight() {
        return weight;
    }

    public EdgeOrientation getOrientation() {
        return orientation;
    }

    public String getOriginId() {
        return originId;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public boolean isDirected() {
        return orientation == EdgeOrientation.DIRIGIDA;
    }

    public boolean isBlocked() {
        return blocked;
    }

    /** Bloquea la conexion sin eliminarla del grafo. */
    public void block() {
        blocked = true;
    }

    /** Reactiva una conexion previamente bloqueada. */
    public void unblock() {
        blocked = false;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException(field + " no puede ser nulo ni vacio");
        }
        return value.trim();
    }

    @Override
    public String toString() {
        String arrow = isDirected() ? " -> " : " <-> ";
        String state = blocked ? " [BLOQUEADA]" : "";
        return id + " " + originId + arrow + destinationId
                + " (" + weight + " min, " + label + ")" + state;
    }
}
