package ci;

import jakarta.servlet.ServletException;

/**
 * Custom {@code Exception} that includes a HTTP status code.
 */
public class RequestException extends ServletException {
    /** The HTTP status code of the exception */
    public int status;

    /** 
     * Wraps another exception with a HTTP status code 
     * @param status the HTTP status of the exception
     * @param cause the exception to wrap
    */
    public RequestException(int status, Throwable cause) {
        super(cause);
        this.status = status;
    }

    /** 
     * A message with a HTTP status code
     * @param status the HTTP status of the exception
     * @param message the message
    */
    public RequestException(int status, String message) {
        super(message);
        this.status = status;
    }
}
