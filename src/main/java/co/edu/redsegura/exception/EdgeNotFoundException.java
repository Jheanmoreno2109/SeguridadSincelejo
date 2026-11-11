package co.edu.redsegura.exception;

/**
 * Se lanza cuando se consulta una conexion que no pertenece al grafo.
 */
public class EdgeNotFoundException extends RedSeguraException {

    public EdgeNotFoundException(String message) {
        super(message);
    }
}
