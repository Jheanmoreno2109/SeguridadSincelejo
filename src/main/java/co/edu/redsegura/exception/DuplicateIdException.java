package co.edu.redsegura.exception;

/**
 * Se lanza cuando se intenta registrar un identificador ya existente.
 */
public class DuplicateIdException extends RedSeguraException {

    public DuplicateIdException(String message) {
        super(message);
    }
}
