package it.finanze.sanita.fse2.ms.iniclient.exceptions.base;

public class InputValidationException extends RuntimeException {

    /**
     * Message constructor.
     *
     * @param msg	Message to be shown.
     */
    public InputValidationException(final String msg) {
        super(msg);
    }

}

