package it.finanze.sanita.fse2.ms.iniclient.exceptions;

public class SoapIniException extends RuntimeException {

    /**
     * Seriale.
     */
    private static final long serialVersionUID = 5632725723070077498L;

    /**
     * Costruttore.
     *
     * @param msg	messaggio
     */
    public SoapIniException(final String msg) {
        super(msg);
    }

    /**
     * Costruttore.
     *
     * @param msg	messaggio
     * @param e		eccezione
     */
    public SoapIniException(final String msg, final Exception e) {
        super(msg, e);
    }

    /**
     * Costruttore.
     *
     * @param e	eccezione.
     */
    public SoapIniException(final Exception e) {
        super(e);
    }
}
