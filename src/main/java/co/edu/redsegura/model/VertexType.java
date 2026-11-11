package co.edu.redsegura.model;

/**
 * Tipo funcional de un lugar de la red RedSegura.
 */
public enum VertexType {
    UNIVERSIDAD("Universidad"),
    SALUD("Salud"),
    TRANSPORTE("Transporte"),
    PUNTO_ENCUENTRO("Punto de encuentro"),
    COMERCIO("Comercio"),
    PARADERO("Paradero"),
    MOVILIDAD("Movilidad");

    private final String displayName;

    VertexType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
