package co.edu.redsegura.exception;

/**
 * Se lanza cuando un dato no cumple las reglas del dominio, por ejemplo un
 * peso menor o igual a cero, un identificador vacio o un extremo repetido.
 */
public class InvalidDataException extends RedSeguraException {

    public InvalidDataException(String message) {
        super(message);
    }
}
