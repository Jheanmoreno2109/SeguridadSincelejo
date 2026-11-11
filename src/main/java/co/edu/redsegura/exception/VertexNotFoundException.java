package co.edu.redsegura.exception;

/**
 * Se lanza cuando se consulta un vertice que no pertenece al grafo.
 */
public class VertexNotFoundException extends RedSeguraException {

    public VertexNotFoundException(String message) {
        super(message);
    }
}
