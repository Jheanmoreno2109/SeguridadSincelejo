package co.edu.redsegura.exception;

/**
 * Excepcion base del dominio RedSegura.
 *
 * <p>Todas las validaciones del modelo, del grafo y de los arboles lanzan una
 * subclase de esta excepcion para que la capa de consola pueda informar el
 * error y continuar con la ejecucion.</p>
 */
public class RedSeguraException extends RuntimeException {

    public RedSeguraException(String message) {
        super(message);
    }

    public RedSeguraException(String message, Throwable cause) {
        super(message, cause);
    }
}
