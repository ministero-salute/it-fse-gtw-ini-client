package it.finanze.sanita.fse2.ms.iniclient.exceptions.base;

public class BadRequestException extends RuntimeException{

    /**
     * Message constructor.
     *
     * @param msg	Message to be shown.
     */
    public BadRequestException(final String msg) {
        super(msg);
    }

}
