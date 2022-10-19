package it.finanze.sanita.fse2.ms.iniclient.exceptions;

public class TokenIntegrityException extends RuntimeException {

    /**
     * Seriale.
     */
    private static final long serialVersionUID = 5632725723070077498L;

    /**
     * Costruttore.
     *
     * @param msg	messaggio
     */
    public TokenIntegrityException(final String msg) {
        super(msg);
    }

    /**
     * Costruttore.
     *
     * @param msg	messaggio
     * @param e		eccezione
     */
    public TokenIntegrityException(final String msg, final Exception e) {
        super(msg, e);
    }

    /**
     * Costruttore.
     *
     * @param e	eccezione.
     */
    public TokenIntegrityException(final Exception e) {
        super(e);
    }
}
