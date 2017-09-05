package io.tessilab.oss.openutils.data;

/**
 * Throw when a content loader has loaded the content, but this content is malformed and does not respect the 
 * constraints that must respect
 * @author david
 */
public class ConsistancyException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -3423434797053706826L;

    public ConsistancyException() {
        super();
    }

    public ConsistancyException(String message) {
        super(message);
    }

    public ConsistancyException(Throwable cause) {
        super(cause);
    }
    
    

}
